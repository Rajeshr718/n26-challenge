 
# Number26 Java code challenge
 
 This project is my solution to the code challenge published by Number26.
 
## Get & run it
 
 - Clone the project from `master` branch.
 - With Maven installed, just run command `mvn spring-boot:run` at the root of the project. 
 
## Project structure
 
 Project is a Maven project, inheriting from the Spring Boot parent POM in the latest version (1.4.1). 
 It was built using Intellij IDEA and their Spring Initializer tool.
 Spring dependencies/modules used are:
 
 - spring-boot-starter-web to configure the web stack
 - spring-boot-starter-cache to get a configured synchronized in-memory cache.
  
 Other dependencies include:
 
 - lombok to avoid cluttering code with getter/setters
 - spring-boot-devtools to reload web server when code changes
 - spring-boot-configuration-processor to let IntelliJ help us with configuration properties
 - spring-boot-starter-test to help testing
 
## The language
 
 Since Java 8 is accepted, we will use the lambda constructs. 
 For instance, I heavily used the Optional.ofNullable.map.orElse whenever dealing with inputs.
 When iterating over collections, I'll use Streams instead of classic loops.
 
## The web architecture
 
 All web MVC standard, we'll let Spring take care of auto-configuring properties, beans, config files. etc.
 We'll just merely add a few customization properties in the Spring `application.properties` file.
 
 The web front and will listen port 8080 and will be accessible through the following mappings (quoted from log file):
 
 ```
 Mapped "{[/transactionservice/transaction/{transaction_id}],methods=[GET]}"
 Mapped "{[/transactionservice/transaction/{transaction_id}],methods=[PUT]}"
 Mapped "{[/transactionservice/types/{type}],methods=[GET]}"
 Mapped "{[/transactionservice/sum/{transaction_id}],methods=[GET]}"
 ```
 
 It will serve REST clients and will answer with JSON objects serialized from data objects created in the controller.
 
## The model
 
 The REST service deals only with transactions, so the model is limited to a single data class
 
```java
@Data
public class Transaction {
    private final List<Transaction> children = new ArrayList<>();
    private Long id;
    private Double amount;
    private String type;
    private Transaction parent;

    public Transaction(Long id, Double amount, String type, Transaction parent) {
        //...
    }
}
```
 
## The persistence
 
 By requesting no SQL explicitly, the challenge takes us to an in-memory solution like a cache.
 In my case I chose to use Spring cache directly injecting a ConcurrentMapCache as a Bean:
 
 ```java
    @Bean
    public ConcurrentMapCache transactionsCache(
            @Value("${spring.cache.allow-null-values: false}") boolean nullValuesAllowed) {
        return new ConcurrentMapCache("transaction", nullValuesAllowed);
    }
 ```
 This gives me a simple synchronized store for transactions.
 
 As such, TransactionService acts as a limited CRUD service. See javadoc for details.
 
## The MVC controller
 
 Since we can benefit from the syntactic sugar provided by Spring MVC, controller maps automatically
 paths to service calls:
 
 ```java
 @RestController
 @RequestMapping("/transactionservice")
 public class TransactionController {
    
    private final TransactionService transactionService;
   
    //...
    
    @RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.PUT)
    public SaveData putTransaction(@PathVariable("transaction_id") Long transactionId,
                                   @RequestBody TransactionData transactionData) {
        Transaction saved = transactionService
                .save(transactionId,
                      Optional.ofNullable(transactionData.getAmount())
                              .map(BigDecimal::doubleValue)
                              .orElse(null),
                      transactionData.getType(),
                      transactionData.getParentId());
        return SaveData.of(Optional.ofNullable(saved)
                                   .map(transaction -> "ok")
                                   .orElse("ko"));
    }
 ```
 
 And the objects returned by the methods are automatically serialized to JSON.
 
 
 ## The JSON mapping
 
 Since Spring produces JSON out of the box, I haven't done much except wrapping controller responses into data objects.
 This because of two things:
 
 1. Spring would not format the response as desired by the specification; 
 for instance, Double values are serialized using a floating point notation. 
 The specification requires that values be represented like plain BigDecimal numbers,
 so I've decided to use BigDecimal numbers as value wrappers for amounts.
 2. Spring would not send {"status":"ok"} unless we would define specific classes for mapping objects.
 So instead of going through the hassle of subclassing mappers and serializers, declaring them
 in Spring and injecting them, I decided to use data wrappers that map directly to the desired JSON
 outputs.
 
 
## Testing
 
### TransactionService tests
 
 Spring Boot 1.4.1 now comes with an additional set of tools:
 
 - We can use `@SpringBootTest` annotation to spring-boot our Junit4 tests.
 - we can use Assertions to add readability to JUnit tests through its fluent API.
 
 This leads to nice readable tests:
 
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    private final Transaction transaction10 = new Transaction(10L, 5000.0, "cars", null);

    private final Transaction transaction11 = new Transaction(11L, 10000.0, "shopping", transaction10);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ConcurrentMapCache transactionsCache;

    @Before
    public void setup() {
        transactionsCache.put(10L, transaction10);
        transactionsCache.put(11L, transaction11);
    }

    @Test
    public void find10() throws Exception {
        Assertions.assertThat(transactionService.find(10L))
                  .isEqualTo(transaction10);
    }
    
    //...

  ```

### TransactionController tests
 
 Spring Boot 1.4.1 provides a `@WebMvcTest` annotation that offers us the ability to inject a mock service
 and test just the controller using a MVC context:

```java
@RunWith(SpringRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    private final Transaction transaction10 = new Transaction(10L, 5000.0, "cars", null);

    private final Transaction transaction11 = new Transaction(11L, 10000.0, "shopping", transaction10);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void GET_transaction_10() throws Exception {
        given(transactionService.find(10L))
                .willReturn(transaction10);

        this.mvc.perform(get("/transactionservice/transaction/10")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"amount\":5000,\"type\":\"cars\"}", true));
    }

     //...
 
   ```

### Integration testing

 Under normal circumstances, a deployable web stack would expose integration tests using a TestRestTemplate and testing 
 the web server but the code is said not to be deployable so integration tests are just overkill.
  
 I've decided not to implement them.


 
 
