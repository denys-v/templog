package dv.model;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "temp_log")
public class TempLog {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "temperature", nullable = false, precision = 3, scale = 1)
    private BigDecimal temperature;

    @Column(name = "taken_at", nullable = false)
    private Date takenAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
