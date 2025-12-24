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

  private RuleDTO rule;
  private String ruleJson;
  private String responseJson;

  @BeforeAll
  void setup() {
    rule = new RuleDTO(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );
    ruleJson = objectMapper.writeValueAsString(rule);
    responseJson = objectMapper.writeValueAsString(new ApiResponse<>(rule));
  }


  @Test
  void whenRuleDoesNotExists_thenPostRuleShouldAcceptAndReturnRule() throws Exception {
    when(service.createRule(rule)).thenReturn(true);

    mockMvc.perform(
                    post("/rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isCreated())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleAlreadyExists_thenPostRuleShouldReturnBadRequest() throws Exception {
    when(service.createRule(rule)).thenReturn(false);

    mockMvc.perform(
                    post("/rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenGetRuleShouldReturnRule() throws Exception {
    when(service.getRule(rule.key())).thenReturn(rule);

    mockMvc.perform(
                    get("/rules/{key}", rule.key())
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleNotFound_thenGetRuleShouldReturnBadRequest() throws Exception {
    when(service.getRule(rule.key())).thenReturn(null);

    mockMvc.perform(
                    get("/rules/{key}", rule.key())
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenUpdateRuleShouldReturnRule() throws Exception {
    when(service.updateRule(rule.key(), rule)).thenReturn(true);

    mockMvc.perform(
                    put("/rules/{key}", rule.key())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

  @Test
  void whenRuleNotFound_thenUpdateRuleShouldReturnBadRequest() throws Exception {
    when(service.updateRule(rule.key(), rule)).thenReturn(false);

    mockMvc.perform(
                    put("/rules/{key}", rule.key())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenKeysAreInconsistent_thenUpdateRuleShouldReturnBadRequest() throws Exception {
    when(service.updateRule("incorrect:key", rule)).thenReturn(false);

    mockMvc.perform(
                    put("/rules/{key}", "incorrect:key")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleNotFound_thenDeleteRuleShouldReturnBadRequest() throws Exception {
    when(service.deleteRule(rule.key())).thenReturn(null);

    mockMvc.perform(
                    delete("/rules/{key}", rule.key())
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenRuleFound_thenDeleteRuleShouldReturnDeletedRule() throws Exception {
    when(service.deleteRule(rule.key())).thenReturn(rule);

    mockMvc.perform(
                    delete("/rules/{key}", rule.key())
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

}
