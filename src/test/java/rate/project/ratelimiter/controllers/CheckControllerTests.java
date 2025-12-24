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

  private String key;
  private CheckResponse check;
  private String responseJson;

  @BeforeAll
  void setup() {
    key = "user:potassiumlover33:post";
    check = new CheckResponse(true, 9, 0);
    responseJson = objectMapper.writeValueAsString(new ApiResponse<>(check));
  }

  @Test
  void whenKeyNotInDB_thenCheckAndUpdateReturnsNull() throws Exception {
    when(checkService.checkAndUpdate(key)).thenReturn(null);

    mockMvc.perform(
                    post("/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(key)
            )
            .andExpect(status().isBadRequest());
  }

  @Test
  void whenKeyInDB_thenCheckAndUpdateReturnsCheck() throws Exception {
    when(checkService.checkAndUpdate(key)).thenReturn(check);

    mockMvc.perform(
                    post("/check")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(key)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(responseJson));
  }

}
