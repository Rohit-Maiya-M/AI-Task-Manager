package com.rohit.aitaskmanager.service;

import com.rohit.aitaskmanager.dto.AiTaskDTO;
import com.rohit.aitaskmanager.dto.SummarizeResponse;
import com.rohit.aitaskmanager.dto.SummarizeSingleRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiIntegrationService {

    // @Value("${webclient.url}")
    // private String BASE_URL;

    private final WebClient webClient;

    public AiIntegrationService(){
        this.webClient = WebClient.create("http://127.0.0.1:8000");
    }

    public SummarizeResponse summarizeTask(AiTaskDTO task){
        SummarizeSingleRequest request = new SummarizeSingleRequest();
        request.setTask(task);

        return webClient.post()
                .uri("/api/ai/summarize/individual")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SummarizeResponse.class)
                .block();
    }
}
