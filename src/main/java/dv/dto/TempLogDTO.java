package dv.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dv.model.TempLog;

import java.math.BigDecimal;
import java.util.Date;

public class TempLogDTO {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private BigDecimal temperature;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private Date takenAt;

    public TempLogDTO() {
    }

    public TempLogDTO(BigDecimal temperature, Date takenAt) {
        this.temperature = temperature;
        this.takenAt = takenAt;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date takenAt) {
        this.takenAt = takenAt;
    }

    public TempLog toTempLog() {
        TempLog log = new TempLog();
        log.setTemperature(this.getTemperature());
        log.setTakenAt(this.getTakenAt());

        return log;
    }

    public static TempLogDTO fromTempLog(TempLog log) {
        TempLogDTO dto = new TempLogDTO();
        dto.setTemperature(log.getTemperature());
        dto.setTakenAt(log.getTakenAt());

        return dto;
    }
}
