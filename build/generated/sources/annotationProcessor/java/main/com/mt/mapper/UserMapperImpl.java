package com.mt.mapper;

import com.mt.data.domain.User;
import com.mt.dto.UserDTO;
import com.mt.request.user.UserRequest;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-04-16T10:36:41+0700",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_251 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User source) {
        if ( source == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setUserId( source.getUserId() );
        userDTO.setUsername( source.getUsername() );
        userDTO.setPassword( source.getPassword() );
        userDTO.setFullName( source.getFullName() );
        userDTO.setEmail( source.getEmail() );
        userDTO.setPhoneNumber( source.getPhoneNumber() );

        return userDTO;
    }

    @Override
    public List<UserDTO> toDTOList(List<User> source) {
        if ( source == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( source.size() );
        for ( User user : source ) {
            list.add( toDTO( user ) );
        }

        return list;
    }

    @Override
    public User toDomain(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( request.getUsername() );
        user.setFullName( request.getFullName() );
        user.setEmail( request.getEmail() );
        user.setPhoneNumber( request.getPhoneNumber() );
        user.setStatus( request.getStatus() );

        return user;
    }
}
