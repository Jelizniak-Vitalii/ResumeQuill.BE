package com.resumequill.app.modules.metrics.controllers;

import com.resumequill.app.common.annotations.NoApiPrefix;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoApiPrefix
public class MetricsController {

    private final PrometheusMeterRegistry registry;

    public MetricsController(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping(value = "/metrics")
    public ResponseEntity<String> metrics() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain; version=0.0.4; charset=utf-8");
        return ResponseEntity.ok().headers(headers).body(registry.scrape());
    }
}