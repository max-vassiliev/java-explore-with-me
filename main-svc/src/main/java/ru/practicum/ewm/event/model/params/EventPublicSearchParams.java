package ru.practicum.ewm.event.model.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.common.CustomPageRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EventPublicSearchParams {

    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable;
    private EventSearchSort sort;
    private int from;
    private int size;
    private CustomPageRequest page;

    public EventPublicSearchParams(String text, List<Long> categories, Boolean paid,
                                   String rangeStart, String rangeEnd,
                                   Boolean onlyAvailable, String sort,
                                   int from, int size) {
        this.text = text;
        this.categories = categories;
        this.paid = paid;
        this.onlyAvailable = onlyAvailable;
        this.sort = EventSearchSort.fromString(sort);
        this.from = from;
        this.size = size;
        if (rangeStart != null) {
            this.rangeStart = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        }
        if (rangeEnd != null) {
            this.rangeEnd = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT));
        }
        if (EventSearchSort.EVENT_DATE.equals(this.sort)) {
            this.page = new CustomPageRequest(from, size, Sort.by("event_date"));
        }
        if (EventSearchSort.VIEWS.equals(this.sort)) {
            this.page = new CustomPageRequest(from, size, Sort.by(Sort.Direction.DESC, "views"));
        }
    }


    public boolean hasText(EventPublicSearchParams params) {
        return params.getText() != null;
    }

    public boolean hasCategories(EventPublicSearchParams params) {
        return params.getCategories() != null;
    }

    public boolean hasPaid(EventPublicSearchParams params) {
        return params.getPaid() != null;
    }

    public boolean hasRangeStart(EventPublicSearchParams params) {
        return params.getRangeStart() != null;
    }

    public boolean hasRangeEnd(EventPublicSearchParams params) {
        return params.getRangeEnd() != null;
    }

}
