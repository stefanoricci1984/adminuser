package com.neotech.adminuser.service;

import com.neotech.adminuser.dto.UserDto;
import com.neotech.adminuser.model.User;

public interface UserService {
    User save (UserDto userDto);
}
