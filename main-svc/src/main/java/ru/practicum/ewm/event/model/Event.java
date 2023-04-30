package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "description")
    private String description;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "confirmed_requests", nullable = false)
    private Long confirmedRequests = 0L;

    @Column(name = "views", nullable = false)
    private Long views;

    public static boolean isParticipantLimitZeroAndNoModerationRequired(Event event) {
        return event.getParticipantLimit() == 0 && !event.isRequestModeration();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "title = " + title + ", " +
                "annotation = " + annotation + ", " +
                "paid = " + paid + ", " +
                "eventDate = " + eventDate + ", " +
                "initiator = " + initiator + ", " +
                "description = " + description + ", " +
                "participantLimit = " + participantLimit + ", " +
                "state = " + state + ", " +
                "createdOn = " + createdOn + ", " +
                "publishedOn = " + publishedOn + ", " +
                "location = " + location + ", " +
                "requestModeration = " + requestModeration + ")";
    }
}
