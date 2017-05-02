package dv.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dv.config.WebSecurityConfig;
import dv.dao.TempLogRepository;
import dv.dao.UserRepository;
import dv.dto.TempLogDTO;
import dv.dto.TempLogProjection;
import dv.model.TempLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TempLogController.class,
        includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class))
public class TempLogControllerTest {

    private static final Logger log = LoggerFactory.getLogger(TempLogControllerTest.class);
    private static final String PARAM_FROM_DATE = "fromDate";
    private static final String PARAM_TO_DATE = "toDate";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TempLogRepository tempLogRepo;
    @MockBean
    private UserRepository userRepo;

    private String fromDateStr;
    private String toDateStr;

    @Before
    public void setUp() {
        OffsetDateTime odtTo = OffsetDateTime.now();
        OffsetDateTime odtFrom = odtTo.minusWeeks(1);

        fromDateStr = odtFrom.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        toDateStr = odtTo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Test
    @WithMockUser(roles = {"WRITER"})
    public void submitLog() throws Exception {
        // given
        BigDecimal temperature = new BigDecimal("36.6");
        OffsetDateTime takenAt = OffsetDateTime.now().minusDays(1);

        HashMap<String, String> contentMap = new HashMap<>();
        contentMap.put("temperature", temperature.toString());
        contentMap.put("takenAt", takenAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        // when
        RequestBuilder request = post("/templog/submit")
                .content(objectMapper.writeValueAsString(contentMap))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mvc.perform(request);

        // then
        resultActions.andExpect(status().isOk());

        ArgumentCaptor<TempLog> tempLogCaptor = ArgumentCaptor.forClass(TempLog.class);
        verify(tempLogRepo).save(tempLogCaptor.capture());
        assertThat(tempLogCaptor.getValue().getTemperature()).isEqualByComparingTo(temperature);
        assertThat(tempLogCaptor.getValue().getTakenAt()).isEqualTo(Date.from(takenAt.toInstant()));
    }

    @Test
    public void getLogs() throws Exception {
        // when
        String uri = format("/templog/logs/%s/%s", fromDateStr, toDateStr);

        RequestBuilder request = get(uri).accept(MediaType.APPLICATION_JSON);

        // then
        ResultActions resultActions = mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].temperature").value(new BigDecimal("36.6")));

        logResponseContent(resultActions);
    }

    @Test
    public void getLogs1() throws Exception {
        // given
        ArrayList<TempLog> tempLogs = new ArrayList<>();
        tempLogs.add(new TempLog(new BigDecimal("37.5"), new Date()));
        tempLogs.add(new TempLog(new BigDecimal("36.5"), new Date()));

        when(tempLogRepo.findByTakenAtBetweenOrderByTakenAtAsc(any(Date.class), any(Date.class)))
                .thenReturn(tempLogs);

        // when
        RequestBuilder requestBuilder = get("/templog/logs1")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);

        // then
        ResultActions resultActions = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].temperature").value(new BigDecimal("37.5")))
                .andExpect(jsonPath("$[1].temperature").value(new BigDecimal("36.5")));

        logResponseContent(resultActions);
    }

    @Test
    public void getLogs2() throws Exception {
        // given
        ArrayList<TempLogProjection> projections = new ArrayList<>();
        projections.add(new TempLogProjection() {
            @Override
            public BigDecimal getTemperature() {
                return new BigDecimal("38.5");
            }
            @Override
            public Date getTakenAt() {
                return new Date();
            }
        });

        when(tempLogRepo.findProjectionByTakenAtBetweenOrderByTakenAtAsc(any(Date.class), any(Date.class)))
                .thenReturn(projections);

        // when
        RequestBuilder requestBuilder = get("/templog/logs2")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);

        // then
        ResultActions resultActions = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].temperature").value(new BigDecimal("38.5")));

        logResponseContent(resultActions);
    }

    @Test
    public void getLogs3() throws Exception {
        // given
        ArrayList<TempLogDTO> dtos = new ArrayList<>();
        dtos.add(new TempLogDTO(new BigDecimal("35.9"), new Date()));
        dtos.add(new TempLogDTO(new BigDecimal("37.3"), new Date()));
        dtos.add(new TempLogDTO(new BigDecimal("36.6"), new Date()));

        when(tempLogRepo.findDtoByTakenAtBetweenOrderByTakenAtAsc(any(Date.class), any(Date.class)))
                .thenReturn(dtos);

        // when
        RequestBuilder requestBuilder = get("/templog/logs3")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);

        // then
        ResultActions resultActions = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[2].temperature").value(new BigDecimal("36.6")));

        logResponseContent(resultActions);
    }

    private void logResponseContent(ResultActions resultActions) throws UnsupportedEncodingException {
        String responseContent = resultActions
                .andReturn()
                .getResponse()
                .getContentAsString();

        log.info("Response content: \n {}", responseContent);
    }
}