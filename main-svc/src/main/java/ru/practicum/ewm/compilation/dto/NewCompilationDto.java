package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewCompilationDto {

    private List<Long> events = new ArrayList<>();

    private Boolean pinned = false;

    @NotBlank(message = "Field: title. Error: must not be blank.")
    private String title;

}
