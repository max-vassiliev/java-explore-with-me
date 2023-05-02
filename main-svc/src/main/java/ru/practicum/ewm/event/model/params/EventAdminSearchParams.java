package ru.practicum.ewm.event.model.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EventAdminSearchParams {

    private List<Long> users;
    private List<EventState> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
    private CustomPageRequest page;


    public EventAdminSearchParams(List<Long> users, List<EventState> states, List<Long> categories,
                                  String rangeStart, String rangeEnd, int from, int size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        if (rangeStart != null) {
            this.rangeStart = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        }
        if (rangeEnd != null) {
            this.rangeEnd = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        }
        this.from = from;
        this.size = size;
        this.page = new CustomPageRequest(from, size, Sort.by("id"));
    }


    public boolean hasUsers(EventAdminSearchParams params) {
        return params.getUsers() != null;
    }

    public boolean hasStates(EventAdminSearchParams params) {
        return params.getStates() != null;
    }

    public boolean hasCategories(EventAdminSearchParams params) {
        return params.getCategories() != null;
    }

    public boolean hasRangeStart(EventAdminSearchParams params) {
        return params.getRangeStart() != null;
    }

    public boolean hasRangeEnd(EventAdminSearchParams params) {
        return params.getRangeEnd() != null;
    }
}
