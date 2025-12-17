package rate.project.ratelimiter.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.services.RuleService;

import java.net.URI;

@RestController
public class RuleController {

  private final RuleService ruleService;

  public RuleController(RuleService ruleService) {
    this.ruleService = ruleService;
  }

  @PostMapping("/rule")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> addRule(@RequestBody RuleDTO rule) {
    boolean success = ruleService.createRule(rule);

    if (!success) {
      // If the rule already exists, return HTTP 400 Bad Request
      ApiResponse<RuleDTO> response
              = new ApiResponse<>("This rule has already been created. Use a new key, or edit this one with PUT.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    // Return HTTP 201 Created with the rule in the body
    ApiResponse<RuleDTO> response = new ApiResponse<>(rule);
    return ResponseEntity
            .created(URI.create("/rule/" + rule.key()))
            .body(response);
  }

}
