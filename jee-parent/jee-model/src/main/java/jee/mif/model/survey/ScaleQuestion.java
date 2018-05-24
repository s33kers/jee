package jee.mif.model.survey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by Tadas.
 */

@Entity
@Table(name = "SCALE_QUESTION")
public class ScaleQuestion extends BaseQuestion {
    private static final long serialVersionUID = 1133718082166840445L;

    @Column(name = "MIN_VALUE")
    private BigDecimal minValue;

    @Column(name = "MAX_VALUE")
    private BigDecimal maxValue;

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }
}
