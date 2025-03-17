package com.app.AddressBook_Workshop.service;

import com.app.AddressBook_Workshop.dto.UserDTO;

public interface IUserService {
    String registerUser(UserDTO userDTO);
    String loginUser(String email, String password);
}