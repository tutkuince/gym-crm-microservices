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

    @Override
    @Transactional
    public void handle(RecordTrainingCommand c) {
        var id = new TrainerId(c.trainerUsername());
        var ym = YearMonth.from(c.trainingDate());
        var month = new TrainingMonth(ym);

        int currentTotal = loadPort.loadMonthlyMinutes(id.value(), ym.getYear(), ym.getMonthValue())
                .orElse(0);

        var agg = new TrainerWorkload(id, c.trainerFirstName(), c.trainerLastName(), c.isActive());
        if (currentTotal > 0) {
            agg.record(month, currentTotal);
        }

        switch (c.actionType()) {
            case ADD    -> agg.record(month, c.trainingDurationMinutes());
            case DELETE -> agg.delete(month, c.trainingDurationMinutes());
        }

        var newTotal = agg.getMinutesByMonth().getOrDefault(ym, 0);
        if (newTotal <= 0) {
            savePort.deleteMonth(id.value(), ym.getYear(), ym.getMonthValue());
        } else {
            savePort.upsertMonth(id.value(), ym.getYear(), ym.getMonthValue(),
                    c.trainerFirstName(), c.trainerLastName(), c.isActive(), newTotal);
        }
    }
}
