package org.rdlopes.n26.challenge.controllers.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.rdlopes.n26.challenge.model.Transaction;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by rui on 08/10/2016.
 */
@Data
public class TransactionData {

    private BigDecimal amount;

    private String type;

    @JsonProperty("parent_id")
    private Long parentId;

    public static TransactionData of(Transaction transaction) {
        return Optional.ofNullable(transaction)
                       .map(existing -> {
                           TransactionData transactionData = new TransactionData();
                           transactionData.setAmount(new BigDecimal(existing.getAmount()));
                           transactionData.setType(existing.getType());
                           transactionData.setParentId(Optional.ofNullable(existing.getParent())
                                                               .map(Transaction::getId)
                                                               .orElse(null));
                           return transactionData;
                       })
                       .orElse(null);
    }
}
