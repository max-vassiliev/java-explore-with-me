package ru.practicum.ewm.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class EndpointHitDto {

    private Long id;

    @NotBlank(message = "Отсутствует параметр app — идентификатор сервиса, для которого записывается информация")
    private String app;

    @NotBlank(message = "Отсутствует параметр  uri — URI, для которого был осуществлен запрос")
    private String uri;

    @NotBlank(message = "Отсутствует IP-адрес пользователя, осуществившего запрос")
    private String ip;

    @NotBlank(message = "Отсутствует параметр  timestamp — дата и время, когда был совершен запрос к эндпоинту")
    private String timestamp;

}
