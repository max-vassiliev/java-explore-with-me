package ru.practicum.ewm.event.dto.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.event.dto.enums.EventActionAdmin;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private EventActionAdmin stateAction;

}
