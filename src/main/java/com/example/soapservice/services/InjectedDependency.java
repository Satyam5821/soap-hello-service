package com.example.soapservice.services;

import org.springframework.stereotype.Component;

/**
 * Intentional injectable bean used to trigger Sonar rule java:S6813 (field injection)
 * in a way that still allows the Spring context to start.
 */
@Component
public class InjectedDependency {
  public String ping() {
    return "ok";
  }
}

