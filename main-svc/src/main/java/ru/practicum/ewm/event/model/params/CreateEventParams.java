package ru.practicum.ewm.event.model.params;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateEventParams {
    private LocalDateTime eventDate;
    private LocalDateTime createdOn;
    private User initiator;
    private Category category;
    private Location location;
}
