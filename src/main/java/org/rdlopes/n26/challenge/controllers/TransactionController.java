package org.rdlopes.n26.challenge.controllers;

import org.rdlopes.n26.challenge.controllers.json.SaveData;
import org.rdlopes.n26.challenge.controllers.json.SumData;
import org.rdlopes.n26.challenge.controllers.json.TransactionData;
import org.rdlopes.n26.challenge.model.Transaction;
import org.rdlopes.n26.challenge.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by rui on 08/10/2016.
 */
@RestController
@RequestMapping("/transactionservice")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "/transaction/{transaction_id}", method = RequestMethod.GET)
    public TransactionData getTransaction(@PathVariable("transaction_id") Long transactionId) {
        return TransactionData.of(transactionService.find(transactionId));
    }

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

    @RequestMapping(value = "/types/{type}", method = RequestMethod.GET)
    public Long[] getTransactionsByType(@PathVariable("type") String type) {
        return transactionService.findByType(type).stream()
                                 .map(Transaction::getId)
                                 .toArray(Long[]::new);
    }

    @RequestMapping(value = "/sum/{transaction_id}", method = RequestMethod.GET)
    public SumData getAmountsSummedFrom(@PathVariable("transaction_id") Long parentId) {
        return SumData.of(transactionService.sumAmountsFrom(parentId));
    }


}
