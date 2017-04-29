package dv.dto;

import java.math.BigDecimal;
import java.util.Date;

public interface TempLogProjection {
    BigDecimal getTemperature();
    Date getTakenAt();
}
