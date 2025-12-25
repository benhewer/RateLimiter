package rate.project.ratelimiter.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rate.project.ratelimiter.dtos.ApiResponse;
import rate.project.ratelimiter.dtos.RuleDTO;
import rate.project.ratelimiter.services.RuleService;

import java.net.URI;

/**
 * Handles HTTP requests for creating rate limit rules.
 */
@RestController
public class RuleController {

  private final RuleService ruleService;

  public RuleController(RuleService ruleService) {
    this.ruleService = ruleService;
  }

  @PostMapping("/projects/{projectId}/rules")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> addRule(
          @PathVariable String projectId,
          @RequestBody RuleDTO rule
  ) {
    boolean success = ruleService.createRule(projectId, rule);

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
            .created(URI.create("/projects/" + projectId + "/rules/" + rule.ruleKey()))
            .body(response);
  }

  @GetMapping("projects/{projectId}/rules/{ruleKey}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> getRule(
          @PathVariable String projectId,
          @PathVariable String ruleKey
  ) {
    RuleDTO rule = ruleService.getRule(projectId, ruleKey);

    // If the rule is not in the DB, return HTTP 400 Bad Request
    if (rule == null) {
      ApiResponse<RuleDTO> response = new ApiResponse<>("Rule not found. Create it with POST.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    // If the rule is in the DB, return it
    ApiResponse<RuleDTO> response = new ApiResponse<>(rule);
    return ResponseEntity
            .ok(response);
  }

  @PutMapping("/projects/{projectId}/rules/{ruleKey}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> updateRule(
          @PathVariable String projectId,
          @PathVariable String ruleKey,
          @RequestBody RuleDTO rule) {
    boolean success = ruleService.updateRule(projectId, ruleKey, rule);

    // If rule is not in the DB, return HTTP 400 Bad Request
    if (!success) {
      ApiResponse<RuleDTO> response = new ApiResponse<>("Check keys are consistent and that rule exists.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    // If the rule is in the DB, return the newly updated rule
    ApiResponse<RuleDTO> response = new ApiResponse<>(rule);
    return ResponseEntity
            .ok(response);
  }

  @DeleteMapping("/projects/{projectId}/rules/{ruleKey}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> deleteRule(
          @PathVariable String projectId,
          @PathVariable String ruleKey
  ) {
    RuleDTO rule = ruleService.deleteRule(projectId, ruleKey);

    // If rule is not in the DB, return HTTP 400 Bad Request
    if (rule == null) {
      ApiResponse<RuleDTO> response = new ApiResponse<>("Rule does not exist.");
      return ResponseEntity
              .badRequest()
              .body(response);
    }

    // If the rule is in the DB, return it (after having deleted it)
    ApiResponse<RuleDTO> response = new ApiResponse<>(rule);
    return ResponseEntity
            .ok(response);
  }

}
