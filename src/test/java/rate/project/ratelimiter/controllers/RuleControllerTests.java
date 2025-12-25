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
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.dtos.parameters.TokenBucketParameters;
import rate.project.ratelimiter.enums.RateLimiterAlgorithm;
import rate.project.ratelimiter.services.RuleService;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RuleController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RuleControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private RuleService service;

  private String projectId;
  private RuleDTO rule;
  private String ruleJson;
  private String responseJson;

  @BeforeAll
  void setup() {
    projectId = "example";
    rule = new RuleDTO(
            "login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );
    ruleJson = objectMapper.writeValueAsString(rule);
    responseJson = objectMapper.writeValueAsString(new ApiResponse<>(rule));
  }


  @Test
  void whenRuleDoesNotExists_thenPostRuleShouldAcceptAndReturnRule() throws Exception {
    when(service.createRule(projectId, rule)).thenReturn(true);

    mockMvc.perform(
                    post("/projects/{projectId}/rules", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isCreated())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleAlreadyExists_thenPostRuleShouldReturnBadRequest() throws Exception {
    when(service.createRule(projectId, rule)).thenReturn(false);

    mockMvc.perform(
                    post("/projects/{projectId}/rules", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenGetRuleShouldReturnRule() throws Exception {
    when(service.getRule(projectId, rule.ruleKey())).thenReturn(rule);

    mockMvc.perform(
                    get("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleNotFound_thenGetRuleShouldReturnBadRequest() throws Exception {
    when(service.getRule(projectId, rule.ruleKey())).thenReturn(null);

    mockMvc.perform(
                    get("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenUpdateRuleShouldReturnRule() throws Exception {
    when(service.updateRule(projectId, rule.ruleKey(), rule)).thenReturn(true);

    mockMvc.perform(
                    put("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleNotFound_thenUpdateRuleShouldReturnBadRequest() throws Exception {
    when(service.updateRule(projectId, rule.ruleKey(), rule)).thenReturn(false);

    mockMvc.perform(
                    put("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenKeysAreInconsistent_thenUpdateRuleShouldReturnBadRequest() throws Exception {
    when(service.updateRule("incorrect", "key", rule)).thenReturn(false);

    mockMvc.perform(
                    put("/projects/{projectId}/rules/{ruleKey}", "incorrect", "key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleNotFound_thenDeleteRuleShouldReturnBadRequest() throws Exception {
    when(service.deleteRule(projectId, rule.ruleKey())).thenReturn(null);

    mockMvc.perform(
                    delete("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenDeleteRuleShouldReturnDeletedRule() throws Exception {
    when(service.deleteRule(projectId, rule.ruleKey())).thenReturn(rule);

    mockMvc.perform(
                    delete("/projects/{projectId}/rules/{ruleKey}", projectId, rule.ruleKey())
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

}
