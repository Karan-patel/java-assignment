# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
- What are we trying to measure? Do we need to understand the total cost of each warehouse and store, or allocate costs further to individual products or orders?
- How should costs be allocated? Some costs, such as warehouse rent, belong to a specific location, while transportation costs may be shared across several stores. What rules should determine each store's share?
- Where does the data come from? Which systems provide cost information, and how do we ensure that records are accurate, complete, and not counted twice?
- How frequently is information needed? Is periodic reporting sufficient, or does the business need more frequent updates to make operational decisions?
- How do we preserve history? If a warehouse is replaced or a store changes its associated warehouse, how do we ensure that past costs remain attributed to the correct entity?

<br>
I would first establish what decisions the business needs to make using this information. 
I would then define clear cost categories, allocation rules, and traceable records. More detailed tracking can provide better insights, but it also increases complexity, so the level of detail should match the business need.

---

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
- Where are the biggest costs? Which categories contribute most to expenditure, and what is driving them?
- What can we improve? Can better warehouse-to-store allocation, inventory placement, or transportation planning reduce costs?
- What trade-offs are acceptable? Could reducing storage costs increase transportation expenses, or could consolidating warehouses negatively affect delivery times?
- How should we prioritize improvements? Which opportunities provide the greatest expected savings relative to their implementation effort and risk?
- How will we measure success? How can we verify that costs have genuinely decreased without compromising product availability or service quality?

<br>
I would establish a baseline using reliable historical data, identify the largest cost drivers, and prioritize improvements based on financial impact, feasibility, and operational risk. Where practical, I would test promising changes on a limited scale before expanding them. The important point is to optimize the overall cost of serving the business, rather than reduce one expense while creating a larger cost elsewhere.

---

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
- Which system owns the data? For example, does the financial system own approved costs and budgets, while the warehouse application manages warehouse and store information?
- What information needs to be exchanged? Which records must be shared, in which direction, and how frequently?
- What does real-time mean for the business? Is an immediate update necessary, or would scheduled synchronization meet the reporting requirements?
- How do we handle failures? How should the system deal with duplicate requests, timeouts, delayed updates, or invalid data?
- How do we ensure financial accuracy? Can every reported cost be traced to its source, and can discrepancies between systems be identified and reconciled?

<br>
I would begin by defining data ownership, stable identifiers, validation rules, and reconciliation procedures. The integration must handle retries safely so that a failed request does not result in duplicate financial transactions.

The technical approach depends on the requirements. Scheduled batches or APIs may be sufficient for simpler use cases, while event-driven messaging can support faster updates and greater decoupling. The trade-off is that event-driven systems introduce additional complexity around delivery, retries, and eventual consistency. I would choose the simplest approach that satisfies the business requirements.

---

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
- What needs to be forecast? Are we estimating total warehouse costs, costs allocated to stores, transportation expenses, or the impact of future operational changes?
- What historical data is available? Is it reliable enough to identify cost patterns and trends?
- What drives future costs? How do demand, seasonality, capacity, labor rates, and transportation prices affect expenditure?
- How are budgets managed? Who approves budgets, how are revisions recorded, and how should actual expenditure be compared with planned expenditure?
- How will forecasts be used? Does the business need early warnings about overspending or the ability to compare different operational scenarios?

<br>
I would start with historical costs, agreed business assumptions, and clear budget-versus-actual reporting. This provides a transparent baseline and helps the business understand why expenditure differs from expectations.

As the data and requirements mature, the system could support scenario analysis, such as estimating the financial impact of replacing a warehouse. More sophisticated forecasting models should be introduced only when they provide demonstrable improvements over simpler approaches.

---

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
- What does the Business Unit Code represent? Does it identify the continuing business unit, while each physical warehouse has its own unique identity?
- What history must be preserved? Must we retain the old warehouse's costs, attributes, store relationships, and audit records?
- How should the budget be handled? Should the new warehouse inherit the existing budget, receive a new budget, or have a separate budget for transition expenses?
- How should transition costs be tracked? How do we distinguish one-time expenses, such as relocation and overlapping rent, from the new warehouse's regular operating costs?
- What happens if replacement fails? How do we prevent the business unit from being left without a valid active warehouse or with multiple active warehouses unintentionally?
- How will we measure performance? How can we compare the old and new warehouses fairly, accounting for differences in capacity, demand, and operating conditions?

<br>  
I would separate the Business Unit Code, which remains stable, from the unique identity of each warehouse. When replacing a warehouse, the system would archive the old instance and create a new one using the same Business Unit Code, while preserving the relationship between the two.

The replacement should be atomic: either the entire operation succeeds, or the existing state remains unchanged. This protects the system from partial updates and preserves a consistent view of the business unit.

Keeping historical costs separate from the new warehouse's expenditure is equally important. It allows the business to compare performance, understand transition expenses, and set realistic budgets without confusing past spending with current operations. Whether the new warehouse inherits the old budget or receives a separate one is a business rule that should be agreed upon before implementation.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
