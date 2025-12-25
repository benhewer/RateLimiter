package rate.project.ratelimiter.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.CheckRequest;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.services.CheckService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CheckService checkService;

  private final String projectId = "example";
  private final String ruleKey = "post";
  private final String userKey = "user";
  private final CheckResponse check = new CheckResponse(true, 9, 0);
  private String userKeyJson;
  private String responseJson;

  @BeforeAll
  void setUp() {
    userKeyJson = objectMapper.writeValueAsString(new CheckRequest(userKey));
    responseJson = objectMapper.writeValueAsString(new ApiResponse<>(check));
  }

  @Test
  void whenKeyNotInDB_thenCheckAndUpdateReturnsNull() throws Exception {
    when(checkService.checkAndUpdate(projectId, ruleKey, userKey)).thenReturn(null);

    mockMvc.perform(
                    post("/projects/{projectId}/rules/{ruleKey}/check", projectId, ruleKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userKeyJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenKeyInDB_thenCheckAndUpdateReturnsCheck() throws Exception {
    when(checkService.checkAndUpdate(projectId, ruleKey, userKey)).thenReturn(check);

    mockMvc.perform(
                    post("/projects/{projectId}/rules/{ruleKey}/check", projectId, ruleKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(userKeyJson)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

}
