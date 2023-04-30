package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewUserRequest {

    @NotBlank(message = "Field: email. Error: must not be blank.")
    @Email(message = "Field: email. Error: wrong format. Value: ${validatedValue}")
    private String email;

    @NotBlank(message = "Field: name. Error: must not be blank.")
    private String name;

}
