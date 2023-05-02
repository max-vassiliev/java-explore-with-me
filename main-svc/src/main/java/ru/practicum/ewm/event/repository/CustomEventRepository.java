package ru.practicum.ewm.event.repository;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.params.EventAdminSearchParams;
import ru.practicum.ewm.event.model.params.EventPublicSearchParams;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CustomEventRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> getAllAdmin(EventAdminSearchParams params) {
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        QEvent event = QEvent.event;

        query.from(event);

        if (params.hasUsers(params)) {
            query.where(event.initiator.id.in(params.getUsers()));
        }
        if (params.hasStates(params)) {
            query.where(event.state.in(params.getStates()));
        }
        if (params.hasCategories(params)) {
            query.where(event.category.id.in(params.getCategories()));
        }
        if (params.hasRangeStart(params)) {
            query.where(event.eventDate.after(params.getRangeStart()));
        }
        if (params.hasRangeEnd(params)) {
            query.where(event.eventDate.before(params.getRangeEnd()));
        }

        return query.offset(params.getPage().getOffset()).fetch();
    }

    public List<Event> getAllPublic(EventPublicSearchParams params) {
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        QEvent event = QEvent.event;

        query.from(event).where(event.state.eq(EventState.PUBLISHED));

        if (params.hasText(params)) {
            query.where(event.annotation.likeIgnoreCase("%" + params.getText() + "%")
                    .or(event.description.likeIgnoreCase("%" + params.getText() + "%")));
        }
        if (params.hasCategories(params)) {
            query.where(event.category.id.in(params.getCategories()));
        }
        if (params.hasPaid(params)) {
            query.where(event.paid.eq(params.getPaid()));
        }
        if (params.hasRangeStart(params)) {
            query.where(event.eventDate.after(params.getRangeStart()));
        } else if (!params.hasRangeStart(params)) {
            query.where(event.eventDate.after(LocalDateTime.now()));
        }
        if (params.hasRangeEnd(params)) {
            query.where(event.eventDate.before(params.getRangeEnd()));
        }
        if (params.isOnlyAvailable()) {
            query.where(event.confirmedRequests.lt(event.participantLimit));
        }

        return query.offset(params.getPage().getOffset()).fetch();
    }
}
