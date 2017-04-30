package dv.dto;

import dv.model.TempLog;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class TempLogDTOTest {

    @Test
    public void shouldCreateTempLogFromDto() throws Exception {
        // given
        TempLogDTO dto = new TempLogDTO();
        dto.setTemperature(BigDecimal.valueOf(366L, 1));
        dto.setTakenAt(new Date());
        // when
        TempLog tl = dto.toTempLog();
        // then
        assertThat(tl.getTemperature()).isEqualByComparingTo(dto.getTemperature());
        assertThat(tl.getTakenAt()).isEqualTo(dto.getTakenAt());
    }

    @Test
    public void shouldCreateDtoFromTempLog() throws Exception {
        // given
        TempLog tl = new TempLog();
        tl.setTemperature(BigDecimal.valueOf(370L, 1));
        tl.setTakenAt(new Date());
        // when
        TempLogDTO dto = TempLogDTO.fromTempLog(tl);
        // then
        assertThat(dto.getTemperature()).isEqualByComparingTo(tl.getTemperature());
        assertThat(dto.getTakenAt()).isEqualTo(tl.getTakenAt());
    }

}