/**
 * Application Layer - Use Cases and Ports
 * 
 * <p>This package contains the application layer of the system, which orchestrates
 * business logic through use cases and defines ports (interfaces) for communication
 * with external systems.</p>
 * 
 * <h2>Components</h2>
 * <ul>
 *   <li><b>Use Cases</b> - Implement business workflows and orchestrate domain logic</li>
 *   <li><b>Ports (In)</b> - Define input interfaces for use cases</li>
 *   <li><b>Ports (Out)</b> - Define output interfaces for adapters</li>
 *   <li><b>Services</b> - Application services providing business operations</li>
 * </ul>
 * 
 * <h2>Use Cases</h2>
 * <ul>
 *   <li>Create Movie - Creates a new movie with auto-generated ID</li>
 *   <li>Update Movie - Updates an existing movie</li>
 *   <li>Delete Movie - Deletes a movie by ID</li>
 *   <li>Get Movie - Retrieves a movie by ID</li>
 *   <li>Get Movies - Retrieves movies with pagination and filtering</li>
 *   <li>Calculate Intervals - Calculates producer award intervals</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards.core.application;

