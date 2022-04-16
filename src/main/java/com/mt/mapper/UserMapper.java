package com.mt.mapper;

import com.mt.data.domain.User;
import com.mt.dto.UserDTO;
import com.mt.request.user.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {})
public interface UserMapper {

    UserDTO toDTO(User source);

    List<UserDTO> toDTOList(List<User> source);

    User toDomain(UserRequest request);
}
