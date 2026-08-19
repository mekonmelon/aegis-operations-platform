package com.aegis.operations.controller;

import com.aegis.operations.integration.fema.FemaIngestionResult;
import com.aegis.operations.integration.fema.FemaIngestionService;
import com.aegis.operations.integration.fema.FemaIngestionStatus;
import com.aegis.operations.integration.nws.NwsAlertIngestionService;
import com.aegis.operations.integration.nws.NwsIngestionResult;
import com.aegis.operations.integration.nws.NwsIngestionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
public class IngestionController {
    private final NwsAlertIngestionService ingestionService;
    private final FemaIngestionService femaIngestionService;

    public IngestionController(NwsAlertIngestionService ingestionService, FemaIngestionService femaIngestionService) {
        this.ingestionService = ingestionService;
        this.femaIngestionService = femaIngestionService;
    }

    @PostMapping("/nws/refresh")
    public NwsIngestionResult refresh() {
        return ingestionService.refresh();
    }

    @GetMapping("/nws/status")
    public NwsIngestionStatus status() {
        return ingestionService.status();
    }

    @PostMapping("/fema/refresh")
    public FemaIngestionResult refreshFema() {
        return femaIngestionService.refresh();
    }

    @GetMapping("/fema/status")
    public FemaIngestionStatus femaStatus() {
        return femaIngestionService.status();
    }
}
