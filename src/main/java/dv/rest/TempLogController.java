package dv.rest;

import dv.dao.TempLogRepository;
import dv.dto.TempLogDTO;
import dv.model.TempLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/templog")
public class TempLogController {

    private static final Logger log = LoggerFactory.getLogger(TempLogController.class);

    private final TempLogRepository tempLogRepository;

    @Autowired
    public TempLogController(TempLogRepository tempLogRepository) {
        this.tempLogRepository = tempLogRepository;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public void submitLog(@RequestBody TempLogDTO dto) {
        log.info("Temperature log submitted: temperature: {} takenAt: {}", dto.getTemperature(), dto.getTakenAt());

        TempLog log = dto.toTempLog();

        tempLogRepository.save(log);
    }

    @RequestMapping(value = "/logs/{fromDate}/{toDate}", method = RequestMethod.GET)
    public List<TempLogDTO> getLogs(@PathVariable("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                    @PathVariable("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        ArrayList<TempLogDTO> logs = new ArrayList<>();

        logs.add(new TempLogDTO(new BigDecimal("36.6"), Date.from(OffsetDateTime.parse("2016-10-30T01:18:00+03:00").toInstant())));
        logs.add(new TempLogDTO(new BigDecimal("36.8"), Date.from(OffsetDateTime.parse("2016-10-30T01:30:00+03:00").toInstant())));
        logs.add(new TempLogDTO(new BigDecimal("37.1"), Date.from(OffsetDateTime.parse("2016-10-30T01:45:00+03:00").toInstant())));

        return logs;
    }

    @RequestMapping(value = "/logs1", method = RequestMethod.GET)
    public List<TempLogDTO> getLogs1(@RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") Date fromDate,
                                    @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") Date toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        ArrayList<TempLogDTO> logsDto = new ArrayList<>();
        this.tempLogRepository.findByTakenAtBetweenOrderByTakenAtAsc(fromDate, toDate)
                .forEach(tempLog -> logsDto.add(TempLogDTO.fromTempLog(tempLog)));

        return logsDto;
    }
}
