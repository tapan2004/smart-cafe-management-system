# ☕ CafeFlow | AI-Powered Operational Backend

[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?logo=hibernate&logoColor=white)](https://hibernate.org/)

The core intelligence node of the **CafeFlow** ecosystem. This Spring Boot application manages inventory logistics, transactional integrity, AI-driven insights, and real-time operational synchronization.

---

## 🚀 Architectural Pillars

### 🧠 Neural Service Layer
- **Cognitive Insights**: Context-aware engine that aggregates real-time sales data to provide tactical advice through the Neural Concierge.
- **Strategic Recommendation Engine**: Pre-calculates high-probability item pairings for the frontend checkout flow.

### 🛡️ Tactical Security & Audit
- **Immutable Ledgering**: Specialized persistence layer for the **Operational Audit Trail**, capturing all high-risk system mutations.
- **JWT Security**: State-less authentication protocol with role-based access control (RBAC).

### 📦 Material & Inventory Management
- **Ingredient-Level Tracking**: Automated stock deduction based on SKU components (e.g., deducting 30g coffee and 200ml milk for 1 Latte).
- **Criticality Alerts**: Real-time identification of supply chain bottlenecks and low-stock thresholds.

---

## 🛠️ Technology Stack
- **Framework**: Spring Boot 3 & Maven.
- **Security**: Spring Security + JWT + BCrypt.
- **Persistence**: Spring Data JPA + MySQL.
- **Real-Time**: Spring Messaging (WebSockets) for KDS synchronization.
- **Docs**: Swagger UI (OpenAPI 3).

---

## ⚙️ Direct Deployment

### Setup
1. Clone the repository: `git clone https://github.com/tapan2004/smart-cafe-management-system.git`
2. Create a MySQL database named `Cafe`.
3. Configure `application.properties` with your database credentials.
4. Run: `mvn spring-boot:run`

---

## 👨‍💻 Author
**Tapan Manna**
- GitHub: [@tapan2004](https://github.com/tapan2004)
- Specialization: Enterprise Java & AI Strategy

---

> _"Orchestrating operational excellence with clean, concurrent code."_
