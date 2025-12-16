package rate.project.ratelimiter.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
  public ResponseEntity<@NotNull RuleDTO> addRule(@RequestBody RuleDTO rule) {
    ruleService.createRule(rule);

    // Return HTTP 201 Created with the rule in the body
    return ResponseEntity
            .created(URI.create("/rule/" + rule.key()))
            .body(rule);
  }

}
