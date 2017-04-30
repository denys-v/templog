package dv.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dv.dao.TempLogRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static java.lang.String.format;
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
        HashMap<String, String> contentMap = new HashMap<>();
        contentMap.put("temperature", "36.6");
        contentMap.put("takenAt", OffsetDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        RequestBuilder requestBuilder = post("/templog/submit")
                .content(objectMapper.writeValueAsString(contentMap))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
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