package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest userRequest);

    @Mapping(target = "eventsRating", qualifiedByName = "roundToOneDecimal")
    UserDto toUserDto(User user);

    @Mapping(target = "eventsRating", qualifiedByName = "roundToOneDecimal")
    UserShortDto toUserShortDto(User user);

    @Named("roundToOneDecimal")
    default Float roundRating(Float rating) {
        if (rating == null) {
            return null;
        }
        return Math.round(rating * 10.0f) / 10.0f;
    }
}
