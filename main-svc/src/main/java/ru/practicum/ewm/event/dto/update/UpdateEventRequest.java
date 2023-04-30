package ru.practicum.ewm.event.dto.update;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class UpdateEventRequest {

    @Size(min = 3, max = 120, message = "Field: title. Size must be between {min} and {max}.")
    private String title;

    @Size(min = 20, max = 2000, message = "Field: annotation. Size must be between {min} and {max}.")
    private String annotation;

    private Long category;

    private Boolean paid;

    private String eventDate;

    @Size(min = 20, max = 7000, message = "Field: description. Size must be between {min} and {max}.")
    private String description;

    private Integer participantLimit;

    @Valid
    private LocationDto location;

    private Boolean requestModeration;

}
