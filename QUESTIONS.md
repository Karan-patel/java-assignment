# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I would refactor the codebase to unify under the Repository Pattern within a Hexagonal Architecture (Ports & Adapters). 
Currently, the project mixes the Active Record pattern (Store.persist()) with the Repository pattern (WarehouseRepository).

Refactoring Strategy:
- Unified Persistence: Transition all entities to the Repository Pattern. While Active Record is convenient for CRUD, it couples domain models to the persistence framework (Hibernate/Panache). A Repository (e.g., WarehouseStore interface) isolates the domain, enabling cleaner unit testing and protecting the business logic from infrastructure changes.
- Transactional Boundaries: I would move @Transactional decorators from the Resource layer to the Use Case/Service layer. This ensures that a "Unit of Work" (e.g., archiving a warehouse AND updating related fulfillments) is atomic and consistent, preventing partial failures that leave the system in an invalid state.
- Entity vs. Domain Model: I would strictly separate JPA Entities (DbWarehouse) from Domain Models (Warehouse). This prevents "Anemic Domain Models" and ensures that database schema changes don't force breaking changes in business logic.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Contract-First (OpenAPI) is the gold standard for cross-team collaboration.

Pros/Cons Analysis:
- Contract-First (Warehouse): 
  - Pros: Single source of truth, enables parallel development (frontend/backend), and ensures strict API governance.
  - Cons: Slower initial setup, "stiff" development loop due to code generation.
- Code-First (Product/Store): 
  - Pros: High development velocity, easier to refactor during early prototyping.
  - Cons: "Documentation Drift" where the code and API docs diverge, harder for external consumers to integrate.

I would move the entire project to Contract-First. In a distributed fulfillment network, the API is the "Legal Contract" between microservices. Using OpenAPI for all modules ensures that breaking changes are caught at build time via generated interfaces, preventing expensive production failures.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would implement a Risk-Weighted Testing Pyramid, prioritizing stability in the "Critical Path."

1. Use Case (Component) Tests (High Priority): I would focus 70% of effort here. By testing the Use Case layer with mocked Repositories, we validate complex business rules (like capacity limits and fulfillment constraints) with millisecond execution times.
2. Integration & Transactional Tests (Medium Priority): Focus on boundary conditions like StoreResource's legacy synchronization. We need to verify that REQUIRES_NEW transactions correctly handle downstream failures.
3. Contract Tests (Medium Priority): Using tools like Pact to ensure the generated OpenAPI implementation actually satisfies consumer expectations.
4. E2E/Smoke Tests (Low Volume): A few high-value flows (e.g., "Full Warehouse Lifecycle") to ensure the DI container (Quarkus/Arc) and DB migrations are healthy.

Maintenance: I would integrate JaCoCo with a 80% threshold and use Mutation Testing (e.g., PITest) to ensure the *quality* of assertions, not just line execution.
```