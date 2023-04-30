package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {

    List<UserDto> getAll(List<Long> ids, Pageable pageable);

    UserDto create(NewUserRequest userDto);

    void delete(Long userId);

}
