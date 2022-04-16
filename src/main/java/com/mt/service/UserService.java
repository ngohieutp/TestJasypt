package com.mt.service;

import com.mt.data.domain.User;
import com.mt.data.repository.UserRepository;
import com.mt.dto.UserDTO;
import com.mt.mapper.UserMapper;
import com.mt.request.user.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserDTO> getAll() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    public UserDTO create(UserRequest request) {
        User source = userMapper.toDomain(request);
        source.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        userRepository.create(source);
        return userMapper.toDTO(source);
    }

}