package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LocationDto {

    @NotNull(message = "Field: lat. Error: must not be null.")
    private Float lat;

    @NotNull(message = "Field: lon. Error: must not be null.")
    private Float lon;

}
