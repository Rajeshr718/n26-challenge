package org.rdlopes.n26.challenge.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by rui on 08/10/2016.
 * <p>
 * Stands for the main and only domain object.
 * <p>
 * {@link #id} cannot be {@code null}, as it serves for referencing transactions, all other properties are nullable.
 */
@Data
public class Transaction {

    // added to simplify sum operation
    private final List<Transaction> children = new ArrayList<>();

    private Long id;

    private Double amount;

    private String type;

    private Transaction parent;

    public Transaction(Long id, Double amount, String type, Transaction parent) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.parent = parent;

        // the transaction registers itself as a child of its parent
        Optional.ofNullable(parent).ifPresent(transaction -> transaction.getChildren().add(this));
    }
}

