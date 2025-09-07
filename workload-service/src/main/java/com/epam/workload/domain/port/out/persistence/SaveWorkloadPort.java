package com.epam.workload.domain.port.out.persistence;

import com.epam.workload.domain.model.entity.TrainerWorkload;

public interface SaveWorkloadPort {
    TrainerWorkload save(TrainerWorkload aggregate);
}
