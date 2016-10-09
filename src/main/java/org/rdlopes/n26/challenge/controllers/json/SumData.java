package org.rdlopes.n26.challenge.controllers.json;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by rui on 08/10/2016.
 */
@Data
@AllArgsConstructor
public class SumData {

    public static SumData of(Double sum) {
        return new SumData(new BigDecimal(sum));
    }

    @JsonFormat(shape = Shape.NUMBER)
    private final BigDecimal sum;
}
