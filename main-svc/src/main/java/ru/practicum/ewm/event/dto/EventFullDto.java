package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventFullDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private String description;

    private int participantLimit;

    private EventState state;

    private String createdOn;

    private String publishedOn;

    private LocationDto location;

    private boolean requestModeration;

    private Long views;

    private Long confirmedRequests;

    private Long likes;

    private Long dislikes;

    private Float rating;

}
