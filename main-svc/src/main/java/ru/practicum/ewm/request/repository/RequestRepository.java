package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getAllByRequester_Id(Long requesterId);

    List<Request> getAllByEvent_Id(Long eventId);

    List<Request> getAllByIdIn(List<Long> requestIds);

    Optional<Request> getByRequester_IdAndEvent_Id(Long requesterId, Long eventId);

}
