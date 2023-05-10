package ru.practicum.ewm.event.model.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.exception.model.ValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;
import static ru.practicum.ewm.common.StatsConstants.EARLIEST_DATE;

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
        validateRange();
        if (EventSearchSort.EVENT_DATE.equals(this.sort)) {
            this.page = new CustomPageRequest(from, size, Sort.unsorted());
        }
        if (EventSearchSort.VIEWS.equals(this.sort)) {
            this.page = new CustomPageRequest(from, size, Sort.unsorted());
        }
        if (EventSearchSort.EVENT_RATING.equals(this.sort)) {
            validateRangeIsPast();
            this.page = new CustomPageRequest(from, size, Sort.unsorted());
        }
        if (EventSearchSort.INITIATOR_RATING.equals(this.sort)) {
            this.page = new CustomPageRequest(from, size, Sort.unsorted());
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

    private boolean isRangeStartAfterRangeEnd() {
        return this.rangeStart != null && this.rangeEnd != null &&  this.rangeStart.isAfter(this.rangeEnd);
    }

    private void validateRange() {
        if (this.rangeStart == null && this.rangeEnd == null) {
            return;
        }
        if (isRangeStartAfterRangeEnd()) {
            throw new ValidationException(
                    String.format("Range start is after range end. Range start: %s. Range end: %s",
                            rangeStart.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT)),
                            rangeEnd.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))),
                    LocalDateTime.now()
            );
        }
    }

    private void validateRangeIsPast() {
        LocalDateTime now = LocalDateTime.now();
        if (this.rangeStart == null) {
            this.rangeStart = EARLIEST_DATE;
        }
        if (this.rangeEnd == null) {
            this.rangeEnd = now;
        }
        if (rangeStart.isAfter(now)) {
            throw new ValidationException(
                    String.format("Range must start in the past. Range start: %s",
                    rangeStart.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))),
                    now);
        }
        if (rangeEnd.isAfter(now)) {
            throw new ValidationException(
                    String.format("Range must end in the past. Range end: %s",
                    rangeEnd.format(DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT))),
                    now);
        }
    }
}
