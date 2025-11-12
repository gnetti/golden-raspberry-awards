/**
 * Domain Layer - Business Models and Rules
 * 
 * <p>This package contains the domain layer with pure business logic, models, and rules.
 * The domain layer is independent of infrastructure and application concerns.</p>
 * 
 * <h2>Components</h2>
 * <ul>
 *   <li><b>Aggregates</b> - Root entities representing business concepts</li>
 *   <li><b>Value Objects</b> - Immutable objects representing domain values</li>
 *   <li><b>Domain Services</b> - Services containing domain logic</li>
 * </ul>
 * 
 * <h2>Domain Models</h2>
 * <ul>
 *   <li><b>Movie</b> - Represents a movie without ID</li>
 *   <li><b>MovieWithId</b> - Represents a movie with ID</li>
 *   <li><b>Producer</b> - Represents a movie producer</li>
 *   <li><b>ProducerInterval</b> - Represents the interval between producer awards</li>
 *   <li><b>ProducerIntervalResponse</b> - Response containing min/max intervals</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards.core.domain;

