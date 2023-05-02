package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventShortDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private Long views;

    private Long confirmedRequests;

}
