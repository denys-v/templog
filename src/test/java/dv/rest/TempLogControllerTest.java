package dv.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dv.dao.TempLogRepository;
import dv.model.TempLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
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

        ResultActions result = mvc.perform(request);

        // then
        result.andExpect(status().isOk());

        ArgumentCaptor<TempLog> tempLogCaptor = ArgumentCaptor.forClass(TempLog.class);
        verify(tempLogRepo).save(tempLogCaptor.capture());
        assertThat(tempLogCaptor.getValue().getTemperature()).isEqualByComparingTo(temperature);
        assertThat(tempLogCaptor.getValue().getTakenAt()).isEqualTo(Date.from(takenAt.toInstant()));
    }

    @Test
    public void getLogs() throws Exception {
        String uri = format("/templog/logs/%s/%s", fromDateStr, toDateStr);

        MvcResult mvcResult = mvc.perform(get(uri).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        logResponseContent(mvcResult);
    }

    @Test
    public void getLogs1() throws Exception {
        RequestBuilder requestBuilder = get("/templog/logs1")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        logResponseContent(mvcResult);
    }

    @Test
    public void getLogs2() throws Exception {
        RequestBuilder requestBuilder = get("/templog/logs2")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        logResponseContent(mvcResult);
    }

    @Test
    public void getLogs3() throws Exception {
        RequestBuilder requestBuilder = get("/templog/logs3")
                .param(PARAM_FROM_DATE, fromDateStr).param(PARAM_TO_DATE, toDateStr)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        logResponseContent(mvcResult);
    }

    private void logResponseContent(MvcResult mvcResult) throws UnsupportedEncodingException {
        String responseContent = mvcResult
                .getResponse()
                .getContentAsString();

        log.info("Response content: \n {}", responseContent);
    }
}