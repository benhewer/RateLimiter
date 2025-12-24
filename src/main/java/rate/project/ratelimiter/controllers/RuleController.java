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

  @PostMapping("/rules")
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

  @GetMapping("/rules/{key}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> getRule(@PathVariable String key) {
    RuleDTO rule = ruleService.getRule(key);

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

  @PutMapping("/rules/{key}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> updateRule(@PathVariable String key, @RequestBody RuleDTO rule) {
    boolean success = ruleService.updateRule(key, rule);

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

  @DeleteMapping("/rules/{key}")
  public ResponseEntity<@NotNull ApiResponse<RuleDTO>> deleteRule(@PathVariable String key) {
    RuleDTO rule = ruleService.deleteRule(key);

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
