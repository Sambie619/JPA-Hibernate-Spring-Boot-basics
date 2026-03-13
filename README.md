 🎯 What I Built - Complete Spring Boot Syllabus
I built 6 production-grade projects covering 100% of Spring Boot + JPA + Security syllabus for technical interviews.

#	Project	Topics Covered
| # | Project             | Topics Covered                         | Tech Stack                      | Live Demo |
| - | ------------------- | -------------------------------------- | ------------------------------- | --------- |
| 1 | Notes REST API      | Spring Boot basics, REST, Postman      | @RestController, DI, @Autowired | Postman   |
| 2 | Student JPA CRUD    | JPA entities, Spring Data JPA, Queries | @Entity, JpaRepository, JPQL    | Postman   |
| 3 | Course Enrollment   | JPA Relations, Thymeleaf MVC           | @OneToMany, @ManyToOne, Forms   |           |
| 4 | Security Form Login | Spring Security, Roles                 | hasRole(), Form Login           |           |
| 5 | Task Manager JWT    | JWT Auth, Filters                      | JWT Filter, @PreAuthorize       | Postman   |
| 6 | Microservices       | Service Communication                  | RestTemplate, DTOs              | Postman   |

🛠 Skills Demonstrated
🔥 Spring Boot 3.2 • JPA/Hibernate • Spring Security • JWT Authentication
🔥 Thymeleaf MVC • Microservices • MySQL • REST APIs • Postman • STS
🔥 Layered Architecture • DTO Pattern • Bean Validation • @JsonIgnore

🚀 Quick Start - Run Any Project
# 1. Clone repo
git clone https://github.com/sabinshah/spring-boot-portfolio.git
cd spring-boot-portfolio

# 2. Create MySQL DBs (see database/ folder)
# 3. Update application.properties (passwords)
# 4. Run any project
cd notes-api && mvn spring-boot:run
# OR in STS: Right-click → Run As → Spring Boot App
Default ports:
Project 1: http://localhost:8080
Project 2: http://localhost:8081  
Project 3: http://localhost:8082
Project 4: http://localhost:8083
Project 5: http://localhost:8084
Project 6: http://localhost:8085 (product), 8086 (order)

📁 Project Details
Project 1: Notes REST API

✅ First Spring Boot REST API
✅ @SpringBootApplication, @RestController, @GetMapping/@PostMapping
✅ Dependency Injection (@Autowired), Layered Architecture
✅ Postman CRUD collection included

Project 2: Student JPA CRUD
✅ JPA Entities (@Entity, @Id, @Column, @GeneratedValue)
✅ Spring Data JPA (JpaRepository, derived queries, @Query JPQL)
✅ Full CRUD with MySQL + Hibernate DDL auto-update

Project 3: Course Enrollment
✅ JPA Relationships (@OneToMany, @ManyToOne, @JoinColumn)
✅ Thymeleaf MVC with forms and validation (@Valid, @NotBlank)
✅ Bidirectional mapping with cascade

Project 4: Spring Security
✅ Form-based login with in-memory users
✅ Role-based access (hasRole("ADMIN"), hasRole("USER"))
✅ Custom login page + SecurityFilterChain

Project 5: Task Manager JWT
✅ JWT Authentication with custom filter
✅ Stateless REST API (SessionCreationPolicy.STATELESS)
✅ @PreAuthorize method-level security
✅ User entity + CustomUserDetailsService

Project 6: Microservices
✅ 2 independent services (product-service ↔ order-service)
✅ Service-to-service HTTP calls with RestTemplate
✅ Separate MySQL DBs per service
✅ DTO pattern to avoid serialization cycles

