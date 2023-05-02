package ru.practicum.ewm.event.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.enums.EventActionUser;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventUserRequest extends UpdateEventRequest {

    private EventActionUser stateAction;

}
