# CanteenQ - College Canteen Pre-Order & Token System

Students and staff pre-order food from the college canteen, pay from a prepaid wallet and get a token number. The kitchen serves tokens in order, and the token board shows which orders are ready to collect.

**Team:** Akash Kumar, Sumit Raj, Nitin Raj
**Subject:** Project Based Learning in Java (24CSH-301)

## Tech Used
Java 17, Spring Boot 3, Spring Data JPA (Hibernate), H2 database, JDBC, Servlet, HTML/JS

## How to Run
1. Open the `CanteenQ` folder in IntelliJ IDEA (or Eclipse/VS Code) as a Maven project.
2. Run `CanteenQApplication.java`.
3. Open http://localhost:8080 in the browser.

Other pages:
- http://localhost:8080/board - live token board (Servlet)
- http://localhost:8080/h2 - database console (JDBC URL `jdbc:h2:mem:canteenq`, user `sa`, no password)

For the console billing counter, keep the server running and run `client/CounterApp.java`.

## Modules
| Module | What it does |
|---|---|
| Menu | Add, update, delete and list dishes by category |
| Members | Student / Staff wallets, recharge, favourite dishes |
| Orders | Place order, apply an offer, pay from wallet, get a token |
| Kitchen | Order queue, serve next token, lunch rush simulation |
| Notice Board | Posts automatically when a dish is added or its price changes |
| Reports | Sales summary, two-week sales matrix, feedback analysis |
| Archive | Saves bills to a file, reprints duplicate bills, takes backups |
| Ledger | Day closing done with plain JDBC |

## Syllabus Coverage
| Topic | Where |
|---|---|
| Data types, access modifiers, String analysis | `FeedbackAnalyzer` (vowels, consonants, digits, special chars) |
| Matrix operations | `SalesMatrix` (add, subtract, multiply) used in fortnight report |
| Banking (deposit / withdraw) | `Member.deposit()`, `Member.withdraw()` |
| Class, constructor, encapsulation | All model classes |
| Abstraction, inheritance, overriding | `Member` (abstract) -> `Student`, `Staff` |
| Overloading | `MenuService.find(Long)` and `find(String)` |
| Polymorphism | `getRole()`, `getPriority()`, `getDetails()`, offer policies |
| Custom exceptions, try/catch/finally, throw/throws | `exception` package, `CounterApp`, `ArchiveService`, `JdbcLedger` |
| Checked vs unchecked | `InvalidInputException` (checked) vs `CanteenException` (unchecked) |
| ArrayList, LinkedList, HashMap, TreeMap, HashSet | `ReportService`, `KitchenService`, `NoticeBoard`, `OfferService` |
| Multithreading, synchronization, priority, life cycle | `RushHourSimulator`, `PlateCounter` |
| Wrapper classes, autoboxing / unboxing | `CounterApp` (`Integer.valueOf`, `Double.parseDouble`, `Long.parseLong`, `Character`) |
| Byte stream, character stream | `ArchiveService.backup()`, `writeDaySheet()`, `readDaySheet()` |
| Serialization, transient, cloning | `Bill`, `ArchiveService` |
| Lambdas, method references, streams | `ReportService` (filter, map, sort, groupingBy, reduce) |
| Generics with wildcards | `ReportService.topN()` |
| Records | `dto` package |
| JDBC, DriverManager, PreparedStatement, transactions | `JdbcLedger` |
| Sequence, DUAL table, Date handling | `JdbcLedger` (`closing_seq`, `SELECT CURRENT_DATE FROM DUAL`) |
| MVC | Controller -> Service -> Repository -> Model |
| Servlet | `TokenBoardServlet` |
| IoC, @Component, @Service, @Repository, @Configuration, @Bean | Throughout, `AppConfig` |
| Constructor / setter / field injection | Services / `KitchenService` / `DataSeeder` |
| Bean life cycle | `@PostConstruct`, `@PreDestroy` in `KitchenService` |
| JPA: @Entity, @Table, @Id, @GeneratedValue | model package |
| @OneToMany, @ManyToOne, @ManyToMany | `FoodOrder`, `OrderLine`, `Member.favourites` |
| @Transactional with rollback | `OrderService.place()` (if the wallet is short, the stock change is rolled back) |
| @ControllerAdvice, @ExceptionHandler | `CanteenErrorHandler` |
| REST: GET, POST, PUT, DELETE, @PathVariable, @RequestBody | controller package |
| RestTemplate, WebClient | `CounterApp` |
| Singleton | `TokenMachine` |
| Factory | `MemberFactory` |
| Observer | `MenuListener` -> `KitchenDisplay`, `NoticeBoard` |
| Strategy (beans injected as a Map) | `OfferPolicy` -> `NoOffer`, `StudentOffer`, `FestOffer`, `StaffMealOffer` |

## API List
| Method | URL | Use |
|---|---|---|
| GET | /api/menu?category=VEG | Menu (optional filter) |
| GET | /api/menu/{id} | One dish |
| GET | /api/menu/search?name=samosa | Find by name |
| POST | /api/menu | Add dish |
| PUT | /api/menu/{id} | Update dish |
| DELETE | /api/menu/{id} | Remove dish |
| GET | /api/menu/notices | Notice board |
| GET | /api/members | All members |
| POST | /api/members | Register member |
| POST | /api/members/{id}/recharge | Recharge wallet |
| POST | /api/members/{id}/favourites/{itemId} | Add favourite |
| GET | /api/members/{id}/orders | Order history |
| POST | /api/orders | Place order |
| GET | /api/orders/{id} | Order details |
| PUT | /api/orders/{id}/collect | Mark collected |
| GET | /api/offers | Offer codes |
| GET | /api/kitchen/queue | Pending orders |
| POST | /api/kitchen/serve | Serve next token |
| POST | /api/kitchen/rush?dish=Samosa&plates=3 | Thread simulation |
| GET | /api/reports/summary | Sales summary |
| GET | /api/reports/fortnight | Sales matrix |
| POST | /api/feedback | Analyse feedback text |
| POST | /api/archive/export | Save bills to file |
| GET | /api/archive/bills | Read saved bills |
| GET | /api/archive/reprint/{orderId} | Duplicate bill |
| POST | /api/archive/backup | Byte stream backup |
| GET | /api/archive/sheet | Day sheet text |
| POST | /api/ledger/close?date=2026-10-31 | Close day (JDBC) |
| GET | /api/ledger/closings | All closings |
| GET | /api/ledger/low-stock?below=10 | Low stock (JDBC) |

Sample order body:
```json
{
  "memberId": 101,
  "offer": "STUDENT10",
  "items": [ { "itemId": 3, "quantity": 1 }, { "itemId": 9, "quantity": 2 } ]
}
```
