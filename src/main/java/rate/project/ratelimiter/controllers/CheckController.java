package rate.project.ratelimiter.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.CheckRequest;
import rate.project.ratelimiter.dtos.CheckResponse;
import rate.project.ratelimiter.services.CheckService;

@RestController
public class CheckController {

  private final CheckService checkService;

  public CheckController(CheckService checkService) {
    this.checkService = checkService;
  }

  @PostMapping("/check")
  public ResponseEntity<@NotNull ApiResponse<CheckResponse>> checkAndUpdate(@RequestBody CheckRequest checkRequest) {
    CheckResponse check = checkService.checkAndUpdate(checkRequest.key());

    if (check == null) {
      ApiResponse<CheckResponse> response = new ApiResponse<>("No rule found for given key.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    ApiResponse<CheckResponse> response = new ApiResponse<>(check);
    return ResponseEntity.ok(response);
  }

}
