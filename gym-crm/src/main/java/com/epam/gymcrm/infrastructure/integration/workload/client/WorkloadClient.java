package com.epam.gymcrm.infrastructure.integration.workload.client;

import com.epam.gymcrm.infrastructure.integration.workload.dto.RecordTrainingCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "workload-service",          // Eureka app name
        path = "/api/v1/workloads"          // remote base path
)
public interface WorkloadClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    void record(@RequestBody RecordTrainingCommand cmd);

    @DeleteMapping
    void cancel(@RequestBody RecordTrainingCommand cmd);
}
