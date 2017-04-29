package dv.dto;

import dv.model.TempLog;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

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
        assertThat(tl.getTemperature(), equalTo(dto.getTemperature()));
        assertThat(tl.getTakenAt(), equalTo(dto.getTakenAt()));
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
        assertThat(dto.getTemperature(), equalTo(tl.getTemperature()));
        assertThat(dto.getTakenAt(), equalTo(tl.getTakenAt()));
    }

}