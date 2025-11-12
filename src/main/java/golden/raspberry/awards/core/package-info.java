/**
 * Core Package - Business Logic Layer
 * 
 * <p>This package contains the core business logic of the application, organized into
 * application services, use cases, domain models, and ports (interfaces) following
 * Hexagonal Architecture principles.</p>
 * 
 * <h2>Sub-packages</h2>
 * <ul>
 *   <li>{@link golden.raspberry.awards.core.application} - Application layer with use cases and ports</li>
 *   <li>{@link golden.raspberry.awards.core.domain} - Domain layer with business models</li>
 * </ul>
 * 
 * <h2>Architecture</h2>
 * <p>The core package is independent of infrastructure concerns and contains:</p>
 * <ul>
 *   <li>Domain models representing business entities</li>
 *   <li>Use cases implementing business workflows</li>
 *   <li>Ports (interfaces) defining contracts for external adapters</li>
 *   <li>Application services orchestrating business logic</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards.core;

