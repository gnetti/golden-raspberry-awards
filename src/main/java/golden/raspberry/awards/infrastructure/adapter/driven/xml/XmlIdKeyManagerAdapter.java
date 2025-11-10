package golden.raspberry.awards.infrastructure.adapter.driven.xml;

import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Output Adapter for managing ID keys in XML file.
 * Implements IdKeyManagerPort and handles XML file operations.
 *
 * <p>XML structure:
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <keys>
 *     <lastId>206</lastId>
 * </keys>
 * }
 * </pre>
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class XmlIdKeyManagerAdapter implements IdKeyManagerPort {

    private static final String XML_ROOT_ELEMENT = "keys";
    private static final String XML_LAST_ID_ELEMENT = "lastId";

    private final String xmlFilePath;
    private final String xmlIndentAmount;
    private final String xmlIndentProperty;

    /**
     * Constructor for dependency injection.
     *
     * @param xmlFilePath      XML file path from properties
     * @param xmlIndentAmount  XML indent amount from properties
     * @param xmlIndentProperty XML indent property from properties
     */
    public XmlIdKeyManagerAdapter(
            @Value("${csv.key-file:primary-key/key.xml}") String xmlFilePath,
            @Value("${csv.xml-indent-amount:4}") String xmlIndentAmount,
            @Value("${csv.xml-indent-property}") String xmlIndentProperty) {
        this.xmlFilePath = Objects.requireNonNull(xmlFilePath, "XML file path cannot be null");
        this.xmlIndentAmount = Objects.requireNonNull(xmlIndentAmount, "XML indent amount cannot be null");
        this.xmlIndentProperty = Objects.requireNonNull(xmlIndentProperty, "XML indent property cannot be null");
    }

    /**
     * Gets the last used ID from XML file.
     *
     * @return Optional containing the last used ID, or empty if file doesn't exist or is invalid
     */
    @Override
    public Optional<Long> getLastId() {
        return createInputStream()
                .flatMap(this::parseXmlSafely)
                .flatMap(this::extractLastIdNode)
                .map(this::extractLastIdValue)
                .flatMap(this::parseLastId);
    }

    /**
     * Creates an InputStream for reading XML file.
     * Attempts to read from file system first, then falls back to classpath if not found.
     *
     * @return Optional containing InputStream if file exists, empty otherwise
     */
    private Optional<InputStream> createInputStream() {
        var fileSystemPath = Path.of("src/main/resources", xmlFilePath);
        return Optional.of(fileSystemPath)
                .filter(Files::exists)
                .map(this::readFromFileSystem)
                .or(this::readFromClasspath);
    }

    /**
     * Reads XML file from file system.
     *
     * @param path File system path to XML file
     * @return InputStream for reading the XML file
     * @throws IllegalStateException if file cannot be read from file system
     */
    private InputStream readFromFileSystem(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read XML file from file system: %s".formatted(xmlFilePath), e);
        }
    }

    /**
     * Attempts to read XML file from classpath.
     *
     * @return Optional containing InputStream if resource exists, empty otherwise
     */
    private Optional<InputStream> readFromClasspath() {
        return Optional.of(new ClassPathResource(xmlFilePath))
                .filter(ClassPathResource::exists)
                .map(this::getResourceInputStream);
    }

    /**
     * Gets InputStream from classpath resource.
     *
     * @param resource ClassPathResource pointing to XML file
     * @return InputStream for reading the XML file
     * @throws IllegalStateException if resource cannot be read from classpath
     */
    private InputStream getResourceInputStream(ClassPathResource resource) {
        try {
            return resource.getInputStream();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read XML file from classpath: %s".formatted(xmlFilePath), e);
        }
    }

    /**
     * Parses XML from InputStream safely.
     *
     * @param inputStream InputStream containing XML data
     * @return Optional containing parsed Document if successful, empty otherwise
     */
    private Optional<org.w3c.dom.Document> parseXmlSafely(InputStream inputStream) {
        try (var stream = inputStream) {
            return Optional.of(parseXml(stream));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the lastId node from XML document.
     *
     * @param document Parsed XML document
     * @return Optional containing lastId node if found, empty otherwise
     */
    private Optional<org.w3c.dom.Node> extractLastIdNode(org.w3c.dom.Document document) {
        return Optional.ofNullable(document.getElementsByTagName(XML_LAST_ID_ELEMENT).item(0));
    }

    /**
     * Extracts the text value from lastId node.
     *
     * @param lastIdNode XML node containing lastId element
     * @return Trimmed text content of the lastId node
     * @throws IllegalStateException if lastId element is empty or null
     */
    private String extractLastIdValue(org.w3c.dom.Node lastIdNode) {
        return Optional.ofNullable(lastIdNode.getTextContent())
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .orElseThrow(() -> new IllegalStateException("lastId element is empty"));
    }

    /**
     * Parses lastId text value to Long.
     *
     * @param lastIdText Text value to parse as Long
     * @return Optional containing parsed Long if successful, empty otherwise
     */
    private Optional<Long> parseLastId(String lastIdText) {
        try {
            return Optional.of(Long.parseLong(lastIdText));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Updates the last used ID in XML file.
     *
     * @param lastId Last used ID to update
     * @throws NullPointerException if lastId is null
     * @throws IllegalArgumentException if lastId is negative
     * @throws IllegalStateException if XML file cannot be created or updated
     */
    @Override
    public void updateLastId(Long lastId) {
        validateLastId(lastId);
        createOrUpdateXmlFile(lastId);
    }

    /**
     * Validates that lastId is not null and not negative.
     *
     * @param lastId Last ID to validate
     * @throws NullPointerException if lastId is null
     * @throws IllegalArgumentException if lastId is negative
     */
    private void validateLastId(Long lastId) {
        Objects.requireNonNull(lastId, "lastId cannot be null");
        Optional.of(lastId)
                .filter(id -> id >= 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "lastId cannot be negative: %d".formatted(lastId)));
    }

    /**
     * Creates or updates XML file with the last used ID.
     *
     * @param lastId Last used ID to write to XML file
     * @throws IllegalStateException if XML file cannot be created or updated
     */
    private void createOrUpdateXmlFile(Long lastId) {
        try {
            var fileSystemPath = Path.of("src/main/resources", xmlFilePath);
            Files.createDirectories(fileSystemPath.getParent());

            var document = createXmlDocument(lastId);
            writeXmlDocument(document, fileSystemPath);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to update lastId in XML: %s".formatted(e.getMessage()), e);
        }
    }

    /**
     * Creates a new XML document with the last used ID.
     *
     * @param lastId Last used ID to include in XML document
     * @return Created XML document
     * @throws Exception if document cannot be created
     */
    private org.w3c.dom.Document createXmlDocument(Long lastId) throws Exception {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.newDocument();

        var rootElement = document.createElement(XML_ROOT_ELEMENT);
        document.appendChild(rootElement);

        var lastIdElement = document.createElement(XML_LAST_ID_ELEMENT);
        lastIdElement.setTextContent(String.valueOf(lastId));
        rootElement.appendChild(lastIdElement);

        return document;
    }

    /**
     * Writes XML document to file system.
     *
     * @param document      XML document to write
     * @param fileSystemPath File system path where XML will be written
     * @throws Exception if document cannot be written to file system
     */
    private void writeXmlDocument(org.w3c.dom.Document document, Path fileSystemPath) throws Exception {
        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(xmlIndentProperty, xmlIndentAmount);

        var source = new DOMSource(document);
        var result = new StreamResult(fileSystemPath.toFile());
        transformer.transform(source, result);
    }

    /**
     * Synchronizes the last ID with the database maximum ID.
     *
     * @param maxIdFromDatabase Maximum ID from database
     * @throws NullPointerException     if maxIdFromDatabase is null
     * @throws IllegalArgumentException if maxIdFromDatabase is negative
     */
    @Override
    public void synchronizeWithDatabase(Long maxIdFromDatabase) {
        validateMaxIdFromDatabase(maxIdFromDatabase);

        var lastIdFromXml = getLastId().orElse(0L);
        var synchronizedId = Math.max(maxIdFromDatabase, lastIdFromXml);

        Optional.of(synchronizedId)
                .filter(id -> !id.equals(lastIdFromXml))
                .ifPresent(this::updateLastId);

    }

    /**
     * Resets the last ID to a specific value.
     *
     * @param resetValue Value to reset the last ID to
     * @throws NullPointerException     if resetValue is null
     * @throws IllegalArgumentException if resetValue is negative
     * @throws IllegalStateException    if XML file cannot be updated
     */
    @Override
    public void resetLastId(Long resetValue) {
        validateMaxIdFromDatabase(resetValue);
        updateLastId(resetValue);
    }

    /**
     * Validates that maxIdFromDatabase is not null and not negative.
     *
     * @param maxIdFromDatabase Maximum ID from database to validate
     * @throws NullPointerException if maxIdFromDatabase is null
     * @throws IllegalArgumentException if maxIdFromDatabase is negative
     */
    private void validateMaxIdFromDatabase(Long maxIdFromDatabase) {
        Objects.requireNonNull(maxIdFromDatabase, "maxIdFromDatabase cannot be null");
        Optional.of(maxIdFromDatabase)
                .filter(id -> id >= 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "maxIdFromDatabase cannot be negative: %d".formatted(maxIdFromDatabase)));
    }

    /**
     * Gets the next available ID and updates the XML file.
     *
     * @return Next available ID
     * @throws IllegalStateException if XML file cannot be read or updated
     */
    @Override
    public Long getNextId() {
        var currentLastId = getLastId().orElse(0L);
        var nextId = currentLastId + 1;
        updateLastId(nextId);
        return nextId;
    }

    /**
     * Parses XML from InputStream into a Document object.
     *
     * @param inputStream InputStream containing XML data
     * @return Parsed XML document
     * @throws Exception if XML cannot be parsed
     */
    private org.w3c.dom.Document parseXml(InputStream inputStream) throws Exception {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputStream);
    }
}

