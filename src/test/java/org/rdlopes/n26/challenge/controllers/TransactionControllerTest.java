package org.rdlopes.n26.challenge.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rdlopes.n26.challenge.model.Transaction;
import org.rdlopes.n26.challenge.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by rui on 08/10/2016.
 */
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

    @Test
    public void GET_transaction_11() throws Exception {
        given(transactionService.find(11L))
                .willReturn(transaction11);

        this.mvc.perform(get("/transactionservice/transaction/11")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"amount\":10000,\"type\":\"shopping\",\"parent_id\":10}", true));
    }

    @Test
    public void GET_transaction_15() throws Exception {
        given(transactionService.find(15L))
                .willReturn(null);

        this.mvc.perform(get("/transactionservice/transaction/15")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void PUT_transaction_10() throws Exception {
        given(transactionService.save(10L, 5000.0, "cars", null))
                .willReturn(transaction10);

        this.mvc.perform(put("/transactionservice/transaction/10")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content("{ \"amount\":5000,\"type\":\"cars\" }")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"ok\"}", true));
    }

    @Test
    public void PUT_transaction_11() throws Exception {
        given(transactionService.save(11L, 10000.0, "shopping", 10L))
                .willReturn(transaction11);

        this.mvc.perform(put("/transactionservice/transaction/11")
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content("{ \"amount\":10000,\"type\":\"shopping\",\"parent_id\":10 }")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"ok\"}", true));
    }

    @Test
    public void GET_types_cars() throws Exception {
        given(transactionService.findByType("cars"))
                .willReturn(Collections.singletonList(transaction10));

        this.mvc.perform(get("/transactionservice/types/cars"))
                .andExpect(status().isOk())
                .andExpect(content().json("[10]", true));
    }

    @Test
    public void GET_types_shopping() throws Exception {
        given(transactionService.findByType("shopping"))
                .willReturn(Collections.singletonList(transaction11));

        this.mvc.perform(get("/transactionservice/types/shopping")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[11]", true));
    }

    @Test
    public void GET_sum_10() throws Exception {
        given(transactionService.sumAmountsFrom(10L))
                .willReturn(15000.0);

        this.mvc.perform(get("/transactionservice/sum/10")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sum\":15000}", true));
    }

    @Test
    public void GET_sum_11() throws Exception {
        given(transactionService.sumAmountsFrom(11L))
                .willReturn(10000.0);

        this.mvc.perform(get("/transactionservice/sum/11")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sum\":10000}"));
    }

    @Test
    public void GET_sum_15() throws Exception {
        given(transactionService.sumAmountsFrom(15L))
                .willReturn(0.0);

        this.mvc.perform(get("/transactionservice/sum/15")
                                 .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sum\":0}", true));
    }

}