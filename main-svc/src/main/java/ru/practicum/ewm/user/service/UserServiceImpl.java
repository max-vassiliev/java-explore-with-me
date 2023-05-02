package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.model.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(NewUserRequest userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        List<User> foundUsers = getUsers(ids, pageable);
        if (foundUsers.isEmpty()) return Collections.emptyList();

        return foundUsers.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        getUser(userId);
        userRepository.deleteById(userId);
    }

    // -------------------------
    // Вспомогательные методы
    // -------------------------

    private void getUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with id=%d was not found", id),
                        User.class,
                        LocalDateTime.now())
                );
    }

    private List<User> getUsers(List<Long> ids, Pageable pageable) {
        if (!ids.isEmpty()) {
            return userRepository.findAllByIdIn(ids, pageable);
        } else {
            return userRepository.findAll(pageable).toList();
        }
    }
}
