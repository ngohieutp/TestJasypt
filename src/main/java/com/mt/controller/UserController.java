package com.mt.controller;

import com.mt.dto.UserDTO;
import com.mt.request.user.UserRequest;
import com.mt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<UserDTO> getAll() throws Exception {
        return userService.getAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDTO create(@RequestBody @Valid UserRequest request) {
        return userService.create(request);
    }
}
