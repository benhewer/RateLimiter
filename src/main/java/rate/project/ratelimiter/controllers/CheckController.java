package rate.project.ratelimiter.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.CheckDTO;
import rate.project.ratelimiter.services.CheckService;

@RestController
public class CheckController {

  private final CheckService checkService;

  public CheckController(CheckService checkService) {
    this.checkService = checkService;
  }

  @PostMapping("/check")
  public ResponseEntity<@NotNull ApiResponse<CheckDTO>> checkAndUpdate(@RequestBody String key) {
    CheckDTO check = checkService.checkAndUpdate(key);

    if (check == null) {
      ApiResponse<CheckDTO> response = new ApiResponse<>("No rule found for given key.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    ApiResponse<CheckDTO> response = new ApiResponse<>(check);
    return ResponseEntity.ok(response);
  }

}
