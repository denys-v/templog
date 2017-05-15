package dv.rest;

import dv.dao.TempLogRepository;
import dv.dto.TempLogDTO;
import dv.dto.TempLogProjection;
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

/**
 * RestController with endpoints for submitting temperature logs and obtaining list of temperature logs
 * within date interval. <br>
 * Logic for obtaining list of logs is implemented via 3 different techniques (for demo purposes): <br>
 * - standard entity loading; <br>
 * - projection loading (Spring Data projections); <br>
 * - DTO object loading (JPA constructor expressions). <br>
 * (See {@link TempLogRepository} for details).
 */
@RestController
@RequestMapping("/templog")
public class TempLogController {

    private static final Logger log = LoggerFactory.getLogger(TempLogController.class);

    private final TempLogRepository tempLogRepository;

    @Autowired
    public TempLogController(TempLogRepository tempLogRepository) {
        this.tempLogRepository = tempLogRepository;
    }

    /**
     * Endpoint for adding new temperature log.
     *
     * @param dto representation of temperature log entry.
     */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public void submitLog(@RequestBody TempLogDTO dto) {
        log.info("Temperature log submitted: temperature: {} takenAt: {}", dto.getTemperature(), dto.getTakenAt());

        TempLog log = dto.toTempLog();

        tempLogRepository.save(log);
    }

    /**
     * Stub method for testing purposes (to be removed soon).
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(value = "/logs/{fromDate}/{toDate}", method = RequestMethod.GET)
    public List<TempLogDTO> getLogs(@PathVariable("fromDate")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            OffsetDateTime fromDate,
                                    @PathVariable("toDate")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                            OffsetDateTime toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        ArrayList<TempLogDTO> logs = new ArrayList<>();

        logs.add(new TempLogDTO(new BigDecimal("36.6"), Date.from(OffsetDateTime.parse("2016-10-30T01:18:00+03:00").toInstant())));
        logs.add(new TempLogDTO(new BigDecimal("36.8"), Date.from(OffsetDateTime.parse("2016-10-30T01:30:00+03:00").toInstant())));
        logs.add(new TempLogDTO(new BigDecimal("37.1"), Date.from(OffsetDateTime.parse("2016-10-30T01:45:00+03:00").toInstant())));

        return logs;
    }

    /**
     * Endpoint to load temperature logs via standard entity loading technique.
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(value = "/logs1", method = RequestMethod.GET)
    public List<TempLogDTO> getLogs1(@RequestParam("fromDate")
                                     @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                             Date fromDate,
                                     @RequestParam("toDate")
                                     @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                             Date toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        ArrayList<TempLogDTO> logsDto = new ArrayList<>();
        this.tempLogRepository.findByTakenAtBetweenOrderByTakenAtAsc(fromDate, toDate)
                .forEach(tempLog -> logsDto.add(TempLogDTO.fromTempLog(tempLog)));

        return logsDto;
    }

    /**
     * Endpoint to load temperature logs via Spring Data projections.
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(value = "/logs2", method = RequestMethod.GET)
    public List<TempLogProjection> getLogs2(@RequestParam("fromDate")
                                            @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                                    Date fromDate,
                                            @RequestParam("toDate")
                                            @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                                    Date toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        List<TempLogProjection> logs = this.tempLogRepository.findProjectionByTakenAtBetweenOrderByTakenAtAsc(fromDate, toDate);

        return logs;
    }

    /**
     * Endpoint to load temperature logs as DTO objects via JPA constructor expressions.
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    @RequestMapping(value = "/logs3", method = RequestMethod.GET)
    public List<TempLogDTO> getLogs3(@RequestParam("fromDate")
                                     @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                             Date fromDate,
                                     @RequestParam("toDate")
                                     @DateTimeFormat(pattern = TempLogDTO.DATE_TIME_FORMAT)
                                             Date toDate) {
        log.info("Temperature logs requested (parsed): from: {} to: {}", fromDate, toDate);

        List<TempLogDTO> logs = this.tempLogRepository.findDtoByTakenAtBetweenOrderByTakenAtAsc(fromDate, toDate);

        return logs;
    }
}
