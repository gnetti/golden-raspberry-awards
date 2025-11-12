/**
 * Adapter Layer - Driving Adapters (REST Controllers)
 * 
 * <p>This package contains driving adapters that handle incoming requests from external systems,
 * primarily REST API controllers that expose the application's functionality via HTTP endpoints.</p>
 * 
 * <h2>Components</h2>
 * <ul>
 *   <li><b>REST Controllers</b> - Handle HTTP requests and responses</li>
 *   <li><b>DTOs</b> - Data Transfer Objects for API communication</li>
 *   <li><b>Exception Handlers</b> - Global exception handling</li>
 *   <li><b>Converters</b> - Convert between DTOs and domain models</li>
 * </ul>
 * 
 * <h2>REST Endpoints</h2>
 * <ul>
 *   <li>POST /api/movies - Create a new movie</li>
 *   <li>GET /api/movies/{id} - Get movie by ID</li>
 *   <li>GET /api/movies - Get all movies with pagination</li>
 *   <li>PUT /api/movies/{id} - Update a movie</li>
 *   <li>DELETE /api/movies/{id} - Delete a movie</li>
 *   <li>GET /api/movies/producers/intervals - Get producer intervals</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards.adapter;

