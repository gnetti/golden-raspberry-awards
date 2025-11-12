/**
 * Infrastructure Layer - External System Adapters
 * 
 * <p>This package contains adapters that implement the ports defined in the application layer,
 * connecting the core business logic to external systems such as databases, file systems, and APIs.</p>
 * 
 * <h2>Adapters</h2>
 * <ul>
 *   <li><b>Persistence Adapters</b> - Database access using JPA/Hibernate</li>
 *   <li><b>CSV Adapters</b> - CSV file reading and writing</li>
 *   <li><b>XML Adapters</b> - XML file management for ID keys</li>
 *   <li><b>File Adapters</b> - File system operations</li>
 *   <li><b>Configuration</b> - Spring configuration classes</li>
 * </ul>
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>MovieRepositoryAdapter - Implements persistence operations</li>
 *   <li>CsvDataLoader - Loads CSV data on startup</li>
 *   <li>CsvFileWriterAdapter - Writes movies to CSV</li>
 *   <li>XmlIdKeyManagerAdapter - Manages ID generation via XML</li>
 *   <li>DatabaseSchemaInitializer - Ensures database schema creation</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards.infrastructure;

