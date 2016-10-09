package org.rdlopes.n26.challenge.services;

import org.rdlopes.n26.challenge.controllers.TransactionController;
import org.rdlopes.n26.challenge.model.Transaction;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by rui on 08/10/2016.
 * <p>
 * The facade for managing {@link Transaction}s from the {@link TransactionController}.
 */
public interface TransactionService {


    /**
     * Finds a transaction given its id.
     *
     * @param id
     *         the id of the searched transaction.
     *
     * @return the {@link Transaction} object which id is the one provided, or {@code null} if none has been found
     * matching this id.
     */
    Transaction find(Long id);

    /**
     * Saves a transaction with the passed properties.
     *
     * @param id
     *         transaction id. Must not be {@code null}.
     * @param amount
     *         transaction amount. Must not be {@code null}.
     * @param type
     *         transaction type (case sensitive). Must not be {@code null}.
     * @param parentId
     *         parent id, in case this transaction is bound to a parent. If the parent id is {@code null}, then the
     *         parent stored for this transaction will be {@code null}.
     *
     * @return the transaction instance that is saved.
     */
    Transaction save(@NotNull Long id, @NotNull Double amount, @NotNull String type, Long parentId);

    /**
     * Searches for all saved transactions which type equals the type provided.
     * This method is case sensitive.
     *
     * @param type
     *         the type searched for.
     *
     * @return a {@link List} of transactions matching the given type. The list is never {@code null}, rather empty is
     * case no transactions match that type.
     */
    List<Transaction> findByType(String type);

    /**
     * Computes the sum of the amounts found in a parent transaction and in all of its children, if any.
     *
     * @param parentId
     *         the transaction id of the parent we want to sum from.
     *
     * @return the sum of all amounts starting from the parent transaction, or 0.0 if: <ul> <li>the id passed is {@code
     * null}</li> <li>the id corresponds to no known transaction</li> </ul>
     */
    Double sumAmountsFrom(@NotNull Long parentId);

}
