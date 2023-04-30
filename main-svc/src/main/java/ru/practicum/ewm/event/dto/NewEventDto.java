package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewEventDto {

    @NotBlank(message = "Field: title. Error: must not be blank.")
    @Size(min = 3, max = 120, message = "Field: title. Size must be between {min} and {max}.")
    private String title;

    @NotBlank(message = "Field: annotation. Error: must not be blank.")
    @Size(min = 20, max = 2000, message = "Field: annotation. Size must be between {min} and {max}.")
    private String annotation;

    @NotNull(message = "Field: category. Error: must not be null.")
    private Long category;

    private boolean paid = false;

    @NotBlank(message = "Field: eventDate. Error: must not be blank.")
    private String eventDate;

    @NotBlank(message = "Field: description. Error: must not be blank.")
    @Size(min = 20, max = 7000, message = "Field: description. Size must be between {min} and {max}.")
    private String description;

    @PositiveOrZero(message = "Field: participantLimit. Error: must be positive or zero. Value: ${validatedValue}")
    private int participantLimit = 0;

    @Valid
    @NotNull(message = "Field: location. Error: must not be null.")
    private LocationDto location;

    private boolean requestModeration = true;

}
