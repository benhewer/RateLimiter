package rate.project.ratelimiter.controllers;

import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RuleController.class)
public class RuleControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private RuleService service;

  @Test
  void postRuleShouldAcceptAndReturnRule() throws Exception {
    RuleDTO rule = new RuleDTO(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    when(service.createRule(rule)).thenReturn(true);

    String ruleJson = objectMapper.writeValueAsString(rule);
    String expectedJson = objectMapper.writeValueAsString(new ApiResponse<>(rule));

    mockMvc.perform(
            post("/rule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ruleJson)
            )
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedJson));
  }

  @Test
  void postRuleShouldReturnErrorWhenRuleAlreadyExists() throws Exception {
    RuleDTO rule = new RuleDTO(
            "user:potassiumlover33:login",
            RateLimiterAlgorithm.TOKEN_BUCKET,
            new TokenBucketParameters(10, 1)
    );

    when(service.createRule(rule)).thenReturn(false);

    String ruleJson = objectMapper.writeValueAsString(rule);

    mockMvc.perform(
            post("/rule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ruleJson)
            )
            .andExpect(status().isBadRequest());
  }

}
