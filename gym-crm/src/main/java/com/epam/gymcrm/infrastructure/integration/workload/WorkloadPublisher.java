package com.epam.gymcrm.infrastructure.integration.workload;

import com.epam.gymcrm.infrastructure.integration.workload.client.WorkloadClient;
import com.epam.gymcrm.infrastructure.integration.workload.dto.RecordTrainingCommand;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WorkloadPublisher {

    private final WorkloadClient client;

    private static final Logger logger = LoggerFactory.getLogger(WorkloadPublisher.class);

    public WorkloadPublisher(WorkloadClient client) {
        this.client = client;
    }

    @CircuitBreaker(name = "workload", fallbackMethod = "fallback")
    @Retry(name = "workload")
    public void trainingAdded(String username, String firstName, String lastName,
                              boolean active, LocalDate date, int minutes) {
        client.record(new RecordTrainingCommand(
                username, firstName, lastName, active, date, minutes, RecordTrainingCommand.ActionType.ADD));
    }

    @CircuitBreaker(name = "workload", fallbackMethod = "fallback")
    @Retry(name = "workload")
    public void trainingCancelled(String username, String firstName, String lastName,
                                  boolean active, LocalDate date, int minutes) {
        client.cancel(new RecordTrainingCommand(
                username, firstName, lastName, active, date, minutes, RecordTrainingCommand.ActionType.DELETE));
    }

    private void fallback(Throwable t) {
        logger.warn("workload-service call failed: {}", t.toString());
    }
}
