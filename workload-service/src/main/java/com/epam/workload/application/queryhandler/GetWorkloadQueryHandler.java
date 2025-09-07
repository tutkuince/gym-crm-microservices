package com.epam.workload.application.queryhandler;

import com.epam.workload.application.dto.query.MonthlySummaryDto;
import com.epam.workload.application.dto.query.TrainerWorkloadResponseDto;
import com.epam.workload.domain.model.entity.TrainerWorkload;
import com.epam.workload.domain.port.in.query.GetWorkloadQueryPort;
import com.epam.workload.domain.port.out.persistence.LoadWorkloadPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class GetWorkloadQueryHandler implements GetWorkloadQueryPort {

    private final LoadWorkloadPort loadPort;

    @Override
    public TrainerWorkloadResponseDto getByUsername(String username) {
        TrainerWorkload trainerWorkload = loadPort.loadByUsername(username).orElseThrow(() -> new NoSuchElementException("trainer not found: " + username));
        List<MonthlySummaryDto> months = new ArrayList<>();
        trainerWorkload.getMinutesByMonth().forEach(((yearMonth, total) -> months.add(new MonthlySummaryDto(yearMonth.getYear(), yearMonth.getMonthValue(), total))));

        months.sort(Comparator.comparingInt(MonthlySummaryDto::year).thenComparing(MonthlySummaryDto::month));

        return new TrainerWorkloadResponseDto(trainerWorkload.getId().value(), trainerWorkload.getFirstName(), trainerWorkload.getLastName(), trainerWorkload.isActive(), months);
    }

    @Override
    public int getMonthlyMinutes(String username, int year, int month) {
        return loadPort.loadMonthlyMinutes(username, year, month).orElse(0);
    }
}
