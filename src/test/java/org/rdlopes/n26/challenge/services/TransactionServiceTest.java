package org.rdlopes.n26.challenge.services;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rdlopes.n26.challenge.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by rui on 08/10/2016.
 */
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

    @Test
    public void find11() throws Exception {
        Assertions.assertThat(transactionService.find(10L))
                  .isEqualTo(transaction10);
    }

    @Test
    public void find15() throws Exception {
        Assertions.assertThat(transactionService.find(15L))
                  .isNull();
    }

    @Test
    public void save15() throws Exception {
        Assertions.assertThat(transactionService.save(15L, 1000.0, "test", null))
                  .isNotNull()
                  .hasFieldOrPropertyWithValue("id", 15L)
                  .hasFieldOrPropertyWithValue("amount", 1000.0)
                  .hasFieldOrPropertyWithValue("type", "test")
                  .hasFieldOrPropertyWithValue("parent", null);
    }

    @Test
    public void findByTypeCars() throws Exception {
        Assertions.assertThat(transactionService.findByType("cars"))
                  .isNotNull()
                  .hasSize(1)
                  .contains(transaction10);
    }

    @Test
    public void findByTypeShopping() throws Exception {
        Assertions.assertThat(transactionService.findByType("shopping"))
                  .isNotNull()
                  .hasSize(1)
                  .contains(transaction11);
    }

    @Test
    public void sumAmountsFrom10() throws Exception {
        Assertions.assertThat(transactionService.sumAmountsFrom(10L))
                  .isEqualTo(15000.0);
    }

    @Test
    public void sumAmountsFrom11() throws Exception {
        Assertions.assertThat(transactionService.sumAmountsFrom(11L))
                  .isEqualTo(10000.0);
    }

}