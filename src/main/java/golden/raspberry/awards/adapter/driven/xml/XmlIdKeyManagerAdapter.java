package golden.raspberry.awards.adapter.driven.xml;

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
 *
 * <p>Implements IdKeyManagerPort and handles XML file operations
 * for storing and retrieving the last used ID.
 *
 * <p>This adapter follows hexagonal architecture principles:
 * - Implements Port defined by Application layer
 * - Handles file system operations (Infrastructure)
 * - Ensures IDs are never reused for data integrity
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
 * <p>Uses Java 21 features: Optional, var, String Templates, Text Blocks.
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

    public XmlIdKeyManagerAdapter(
            @Value("${csv.key-file:primary-key/key.xml}") String xmlFilePath,
            @Value("${csv.xml-indent-amount:4}") String xmlIndentAmount,
            @Value("${csv.xml-indent-property}") String xmlIndentProperty) {
        this.xmlFilePath = Objects.requireNonNull(xmlFilePath, "XML file path cannot be null");
        this.xmlIndentAmount = Objects.requireNonNull(xmlIndentAmount, "XML indent amount cannot be null");
        this.xmlIndentProperty = Objects.requireNonNull(xmlIndentProperty, "XML indent property cannot be null");
    }

    @Override
    public Optional<Long> getLastId() {
        return createInputStream()
                .flatMap(this::parseXmlSafely)
                .flatMap(this::extractLastIdNode)
                .map(this::extractLastIdValue)
                .flatMap(this::parseLastId);
    }

    private Optional<InputStream> createInputStream() {
        var fileSystemPath = Path.of("src/main/resources", xmlFilePath);
        return Optional.of(fileSystemPath)
                .filter(Files::exists)
                .map(this::readFromFileSystem)
                .or(this::readFromClasspath);
    }

    private InputStream readFromFileSystem(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read XML file from file system: %s".formatted(xmlFilePath), e);
        }
    }

    private Optional<InputStream> readFromClasspath() {
        return Optional.of(new ClassPathResource(xmlFilePath))
                .filter(ClassPathResource::exists)
                .map(this::getResourceInputStream);
    }

    private InputStream getResourceInputStream(ClassPathResource resource) {
        try {
            return resource.getInputStream();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read XML file from classpath: %s".formatted(xmlFilePath), e);
        }
    }

    private Optional<org.w3c.dom.Document> parseXmlSafely(InputStream inputStream) {
        try (var stream = inputStream) {
            return Optional.of(parseXml(stream));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<org.w3c.dom.Node> extractLastIdNode(org.w3c.dom.Document document) {
        return Optional.ofNullable(document.getElementsByTagName(XML_LAST_ID_ELEMENT).item(0));
    }

    private String extractLastIdValue(org.w3c.dom.Node lastIdNode) {
        return Optional.ofNullable(lastIdNode.getTextContent())
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty))
                .orElseThrow(() -> new IllegalStateException("lastId element is empty"));
    }

    private Optional<Long> parseLastId(String lastIdText) {
        try {
            return Optional.of(Long.parseLong(lastIdText));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateLastId(Long lastId) {
        validateLastId(lastId);
        createOrUpdateXmlFile(lastId);
    }

    private void validateLastId(Long lastId) {
        Objects.requireNonNull(lastId, "lastId cannot be null");
        Optional.of(lastId)
                .filter(id -> id >= 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "lastId cannot be negative: %d".formatted(lastId)));
    }

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

    @Override
    public Long synchronizeWithDatabase(Long maxIdFromDatabase) {
        validateMaxIdFromDatabase(maxIdFromDatabase);

        var lastIdFromXml = getLastId().orElse(0L);
        var synchronizedId = Math.max(maxIdFromDatabase, lastIdFromXml);

        Optional.of(synchronizedId)
                .filter(id -> !id.equals(lastIdFromXml))
                .ifPresent(this::updateLastId);

        return synchronizedId;
    }

    @Override
    public Long resetLastId(Long resetValue) {
        validateMaxIdFromDatabase(resetValue);
        updateLastId(resetValue);
        return resetValue;
    }

    private void validateMaxIdFromDatabase(Long maxIdFromDatabase) {
        Objects.requireNonNull(maxIdFromDatabase, "maxIdFromDatabase cannot be null");
        Optional.of(maxIdFromDatabase)
                .filter(id -> id >= 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "maxIdFromDatabase cannot be negative: %d".formatted(maxIdFromDatabase)));
    }


    @Override
    public Long getNextId() {
        var currentLastId = getLastId().orElse(0L);
        var nextId = currentLastId + 1;
        updateLastId(nextId);
        return nextId;
    }

    private org.w3c.dom.Document parseXml(InputStream inputStream) throws Exception {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputStream);
    }
}

