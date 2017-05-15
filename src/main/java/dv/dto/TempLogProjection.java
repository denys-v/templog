package dv.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Projection view of temperature log
 * (contains only business data - temperature and timestamp - and no entity ID).
 * <br>
 * To be loaded by corresponding method of {@link dv.dao.TempLogRepository}.
 * <br>
 * (Implemented for demo purposes only).
 */
public interface TempLogProjection {
    BigDecimal getTemperature();
    Date getTakenAt();
}
