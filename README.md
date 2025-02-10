# Doodler

Doodler is a **comprehensive scaffolding framework** built on **Spring Boot** and **Spring Cloud**, designed to streamline the development of Spring-based projects. With its modular architecture, Doodler provides 37 highly focused and reusable components, enabling developers to assemble robust applications efficiently while focusing on business logic.

---

## Project Structure

```
doodler
+---doodler-common-amqp
+---doodler-common-aws
+---doodler-common-cache
+---doodler-common-cloud
+---doodler-common-config
+---doodler-common-data
+---doodler-common-elasticsearch
+---doodler-common-feign
+---doodler-common-gateway
+---doodler-common-grpc
+---doodler-common-hazelcast
+---doodler-common-i18n
+---doodler-common-id
+---doodler-common-ip
+---doodler-common-jdbc
+---doodler-common-jpa
+---doodler-common-jta
+---doodler-common-messager
+---doodler-common-mybatis
+---doodler-common-oauth
+---doodler-common-openfeign
+---doodler-common-quartz
+---doodler-common-redis
+---doodler-common-scheduler
+---doodler-common-security
+---doodler-common-swagger
+---doodler-common-timeseries
+---doodler-common-traffic
+---doodler-common-transmitter
+---doodler-common-upms
+---doodler-common-validation
+---doodler-common-webflux
+---doodler-common-webmvc
+---doodler-common-websocket
+---doodler-common-zookeeper
\---doodler-example-client
```
## Module Descriptions

### **1. doodler-common-amqp**
Provides seamless integration with RabbitMQ and other AMQP-compliant messaging brokers. It includes utilities for message serialization, delivery confirmation, and routing key management. Developers can easily set up message producers and consumers with minimal configuration.

### **2. doodler-common-aws**
A set of tools for interacting with AWS services such as S3, DynamoDB, and SNS. This module simplifies authentication, request signing, and API integration, allowing developers to focus on application logic while leveraging AWS's cloud capabilities.

### **3. doodler-common-cache**
Enhances caching capabilities with Redis and local cache integration. It includes tools for key management, cache eviction strategies, and support for distributed caching in clustered environments. Provides annotations for declarative cache management.

### **4. doodler-common-cloud**
A foundational module for Spring Cloud projects, enabling distributed configuration, service discovery, and circuit breaking. It offers preconfigured setups for common Spring Cloud components to accelerate cloud-native development.

### **5. doodler-common-config**
Centralizes configuration management, supporting hierarchical and environment-specific properties. It simplifies dynamic configuration changes with support for hot-reloading and centralized property storage.

### **6. doodler-common-data**
The backbone of all other components, providing utilities for entity management, data type conversions, and common operations. This module ensures consistency and reusability across different layers of the application.

### **7. doodler-common-elasticsearch**
Enables seamless integration with Elasticsearch for indexing and querying data. It simplifies creating indices, handling search queries, and managing mappings. Ideal for applications requiring powerful search and analytics capabilities.

### **8. doodler-common-feign**
Extends OpenFeign with added features such as load balancing, request tracing, and retry mechanisms. It supports declarative REST client definitions and integrates easily with Spring Boot configurations.

### **9. doodler-common-gateway**
An abstraction over Spring Cloud Gateway, providing tools for API routing, load balancing, and custom filters. It supports advanced configurations such as rate limiting and token-based authentication for secure and efficient traffic management.

### **10. doodler-common-grpc**
A gRPC integration module for building and consuming highly efficient remote procedure calls. It supports bidirectional streaming, service discovery, and secure communication, making it ideal for microservices architectures.

### **11. doodler-common-hazelcast**
Provides integration with Hazelcast for in-memory data grids and distributed caching. It includes utilities for managing shared states, distributed locks, and real-time data processing in clustered environments.

### **12. doodler-common-i18n**
Simplifies internationalization (i18n) with support for multiple locales, dynamic message loading, and annotation-driven translations. Ideal for building multilingual applications with minimal overhead.

### **13. doodler-common-id**
Offers utilities for generating unique identifiers using algorithms like UUID, Snowflake, and database sequences. It ensures scalability and uniqueness in distributed systems.

### **14. doodler-common-ip**
Includes tools for IP address validation, geolocation, and subnet management. It is useful for applications requiring network security, access control, or location-based services.

### **15. doodler-common-jdbc**
Provides enhanced JDBC utilities for database connectivity, query execution, and result mapping. It simplifies common database operations with an emphasis on performance and error handling.

### **16. doodler-common-jpa**
Extends JPA with additional tools for entity management, auditing, and query optimization. It provides support for advanced mapping and bulk operations, ensuring efficient database interactions.

### **17. doodler-common-jta**
Facilitates distributed transactions with JTA support, ensuring consistency across multiple transactional resources. Ideal for systems requiring high reliability and complex transactional workflows.

### **18. doodler-common-messager**
A comprehensive messaging module supporting email, SMS, and push notifications. It includes prebuilt templates, delivery tracking, and retry mechanisms for reliable communication.

### **19. doodler-common-mybatis**
Enhances MyBatis integration with custom mappers, query templates, and transaction management. It simplifies database interactions for developers preferring MyBatis over JPA.

### **20. doodler-common-oauth**
Provides tools for implementing social logins with popular platforms like Google, Facebook, and GitHub. It simplifies user authentication workflows and integrates seamlessly with Spring Security.

### **21. doodler-common-openfeign**
An enhanced OpenFeign module with built-in error handling, retry policies, and request interceptors. It is optimized for declarative HTTP client creation in microservices environments.

### **22. doodler-common-quartz**
Integrates Quartz for robust task scheduling, supporting cron expressions, trigger management, and job persistence. Ideal for applications requiring complex, time-based workflows.

### **23. doodler-common-redis**
Simplifies Redis integration with support for pub-sub, distributed locks, and data caching. It includes utilities for managing Redis connections and handling serialization.

### **24. doodler-common-scheduler**
Provides a simplified interface for scheduling tasks in Spring applications. It supports both fixed-rate and cron-based schedules with runtime configuration options.

## Getting Started

### Prerequisites
- **Java 17+**
- **Maven 3.8+**

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/paganini2008/doodler.git
   cd doodler
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Import the required modules into your Spring Boot project and start developing!

---

## Contributing
Contributions are welcome! Refer to the [Contributing Guide](CONTRIBUTING.md) for guidelines on how to get involved.

---

## License
Doodler is licensed under the MIT License. See the [LICENSE](LICENSE] file for details.

---

Doodler is the ultimate scaffolding solution for Spring Boot and Spring Cloud, empowering developers to focus on business logic while accelerating development with its modular, reusable, and robust components.

