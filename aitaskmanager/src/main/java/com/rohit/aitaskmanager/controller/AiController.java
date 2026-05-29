package com.rohit.aitaskmanager.controller;

import com.rohit.aitaskmanager.dto.AiTaskDTO;
import com.rohit.aitaskmanager.dto.SummarizeResponse;
import com.rohit.aitaskmanager.service.AiIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aitaskmanager/ai")
public class AiController {

    private final AiIntegrationService aiIntegrationService;

    public AiController(AiIntegrationService aiIntegrationService){
        this.aiIntegrationService = aiIntegrationService;
    }

    @PostMapping("/summarize")
    public ResponseEntity<SummarizeResponse> summarize(@RequestBody AiTaskDTO task){
        return ResponseEntity.ok(aiIntegrationService.summarizeTask(task));
    }
}
