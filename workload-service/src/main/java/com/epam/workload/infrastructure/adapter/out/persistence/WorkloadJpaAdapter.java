package com.epam.workload.infrastructure.adapter.out.persistence;

import com.epam.workload.domain.model.entity.TrainerWorkload;
import com.epam.workload.domain.model.valueobject.TrainerId;
import com.epam.workload.domain.model.valueobject.TrainingMonth;
import com.epam.workload.domain.port.out.persistence.LoadWorkloadPort;
import com.epam.workload.domain.port.out.persistence.SaveWorkloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkloadJpaAdapter implements LoadWorkloadPort, SaveWorkloadPort {

    private final WorkloadRepository workloadRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainerWorkload> loadByUsername(String username) {
        var rows = workloadRepository.findAllByUsername(username);
        if (rows.isEmpty()) return Optional.empty();

        var first = rows.getFirst();
        var agg = new TrainerWorkload(new TrainerId(username),
                first.getFirstName(), first.getLastName(), first.isActive());

        rows.forEach(r -> {
            var ym = YearMonth.of(r.getWorkYear(), r.getWorkMonth());
            agg.record(new TrainingMonth(ym), r.getTotalMinutes());
        });
        return Optional.of(agg);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Integer> loadMonthlyMinutes(String username, int year, int month) {
        return workloadRepository.findByUsernameAndWorkYearAndWorkMonth(username, year, month)
                .map(TrainerMonthlyWorkloadEntity::getTotalMinutes);
    }

    @Override
    @Transactional
    public void upsertMonth(String username, int year, int month,
                            String firstName, String lastName, boolean active,
                            int totalMinutes) {

        var existing = workloadRepository.findByUsernameAndWorkYearAndWorkMonth(username, year, month);
        if (existing.isPresent()) {
            var e = existing.get();
            e.setFirstName(firstName);
            e.setLastName(lastName);
            e.setActive(active);
            e.setTotalMinutes(totalMinutes);
        } else {
            var e = new TrainerMonthlyWorkloadEntity(username, year, month, firstName, lastName, active, totalMinutes);
            workloadRepository.save(e);
        }
    }

    @Transactional
    public void deleteMonth(String username, int year, int month) {
        workloadRepository.deleteByUsernameAndWorkYearAndWorkMonth(username, year, month);
    }
}
