package org.rdlopes.n26.challenge.services.impl;

import org.rdlopes.n26.challenge.model.Transaction;
import org.rdlopes.n26.challenge.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by rui on 08/10/2016.
 * <p>
 * Implementation of the {@link TransactionService}.
 * <p>
 * Since it's a basic approach with no persistence, we don't mark it as a @{@link Repository} but rather as a {@link
 * Service}.
 *
 * Storage is handled by a Spring concurrent cache to avoid concurrency basic issues.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    private final ConcurrentMapCache transactionsCache;

    @Autowired
    public TransactionServiceImpl(ConcurrentMapCache transactionsCache) {
        this.transactionsCache = transactionsCache;
    }

    @Override
    public Transaction find(Long id) {
        return Optional.ofNullable(id)
                       .map(notNullId -> transactionsCache.get(notNullId, Transaction.class))
                       .orElse(null);
    }

    @Override
    public Transaction save(Long id, Double amount, String type, Long parentId) {
        Optional.ofNullable(id)
                .ifPresent(notNullId -> {
                    Transaction parent = find(parentId);
                    Transaction newTransaction = new Transaction(id, amount, type, parent);
                    transactionsCache.put(id, newTransaction);
                });
        return find(id);
    }

    @Override
    public List<Transaction> findByType(String type) {
        return transactionsCache.getNativeCache().values().parallelStream()
                                .map(o -> (Transaction) o)
                                .filter(transaction -> transaction.getType().equals(type))
                                .collect(Collectors.toList());
    }

    @Override
    public Double sumAmountsFrom(@NotNull Long parentId) {
        return Optional.ofNullable(find(parentId))
                       .map(parent -> parent.getAmount()
                                      + parent.getChildren().parallelStream()
                                              .mapToDouble(childTransaction -> sumAmountsFrom(childTransaction.getId()))
                                              .sum())
                       .orElse(0.0);
    }
}
