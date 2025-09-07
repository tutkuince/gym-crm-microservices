package com.epam.workload.application.commandhandler;

import com.epam.workload.application.dto.command.RecordTrainingCommand;
import com.epam.workload.domain.model.entity.TrainerWorkload;
import com.epam.workload.domain.model.valueobject.TrainerId;
import com.epam.workload.domain.model.valueobject.TrainingMonth;
import com.epam.workload.domain.port.in.command.RecordTrainingCommandPort;
import com.epam.workload.domain.port.out.persistence.LoadWorkloadPort;
import com.epam.workload.domain.port.out.persistence.SaveWorkloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@RequiredArgsConstructor
@Service
public class RecordTrainingCommandHandler implements RecordTrainingCommandPort {
    private final LoadWorkloadPort loadPort;
    private final SaveWorkloadPort savePort;

    @Transactional
    @Override
    public void handle(RecordTrainingCommand command) {
        TrainerId trainerId = new TrainerId(command.trainerUsername());

        TrainerWorkload aggregate = loadPort.loadByUsername(trainerId.value())
                .orElseGet(() -> new TrainerWorkload(trainerId, command.trainerFirstName(), command.trainerLastName(), command.isActive()));

        YearMonth yearMonth = YearMonth.from(command.trainingDate());
        TrainingMonth trainingMonth = new TrainingMonth(yearMonth);

        switch (command.actionType()) {
            case ADD -> aggregate.record(trainingMonth, command.trainingDurationMinutes());
            case DELETE -> aggregate.delete(trainingMonth, command.trainingDurationMinutes());
        }

        savePort.save(aggregate);
    }
}
