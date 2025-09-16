package com.epam.gymcrm.infrastructure.integration.workload;

import com.epam.gymcrm.infrastructure.integration.workload.client.WorkloadClient;
import com.epam.gymcrm.infrastructure.integration.workload.dto.RecordTrainingCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadPublisherTest {

    @Mock
    private WorkloadClient client;

    @InjectMocks
    private WorkloadPublisher publisher;

    @Test
    void shouldCallClientWithAddAction_whenTrainingAdded_thenDelegatesToRecord() {
        // given
        String username = "trainer1";
        String first = "Ada";
        String last = "Lovelace";
        boolean active = true;
        LocalDate date = LocalDate.of(2025, 9, 1);
        int minutes = 45;

        ArgumentCaptor<RecordTrainingCommand> captor = ArgumentCaptor.forClass(RecordTrainingCommand.class);

        // when
        publisher.trainingAdded(username, first, last, active, date, minutes);

        // then
        verify(client, times(1)).record(captor.capture());
        RecordTrainingCommand cmd = captor.getValue();

        assertThat(cmd.trainerUsername()).isEqualTo(username);
        assertThat(cmd.trainerFirstName()).isEqualTo(first);
        assertThat(cmd.trainerLastName()).isEqualTo(last);
        assertThat(cmd.isActive()).isEqualTo(active);
        assertThat(cmd.trainingDate()).isEqualTo(date);
        assertThat(cmd.trainingDurationMinutes()).isEqualTo(minutes);
        assertThat(cmd.actionType()).isEqualTo(RecordTrainingCommand.ActionType.ADD);
    }

    @Test
    void shouldCallClientWithDeleteAction_whenTrainingCancelled_thenDelegatesToRecord() {
        // given
        String username = "trainer2";
        String first = "Grace";
        String last = "Hopper";
        boolean active = false;
        LocalDate date = LocalDate.of(2025, 9, 2);
        int minutes = 30;

        ArgumentCaptor<RecordTrainingCommand> captor = ArgumentCaptor.forClass(RecordTrainingCommand.class);

        // when
        publisher.trainingCancelled(username, first, last, active, date, minutes);

        // then
        verify(client, times(1)).record(captor.capture());
        RecordTrainingCommand cmd = captor.getValue();

        assertThat(cmd.trainerUsername()).isEqualTo(username);
        assertThat(cmd.trainerFirstName()).isEqualTo(first);
        assertThat(cmd.trainerLastName()).isEqualTo(last);
        assertThat(cmd.isActive()).isEqualTo(active);
        assertThat(cmd.trainingDate()).isEqualTo(date);
        assertThat(cmd.trainingDurationMinutes()).isEqualTo(minutes);
        assertThat(cmd.actionType()).isEqualTo(RecordTrainingCommand.ActionType.DELETE);
    }

    @Test
    void shouldNotThrow_whenFallbackCalled_thenLogsAndReturns() {
        String username = "trainerX";
        String first = "Alan";
        String last = "Turing";
        boolean active = true;
        LocalDate date = LocalDate.of(2025, 9, 3);
        int minutes = 60;
        Throwable t = new RuntimeException("boom");

        publisher.fallback(username, first, last, active, date, minutes, t);

        verifyNoInteractions(client);
    }

}