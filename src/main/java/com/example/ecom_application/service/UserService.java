package com.example.ecom_application.service;

import com.example.ecom_application.dto.UserRequest;
import com.example.ecom_application.dto.UserResponse;
import com.example.ecom_application.model.User;
import com.example.ecom_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            return modelMapper.map(user, UserResponse.class);
        }).collect(Collectors.toList());
    }

    public void addUser(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        userRepository.save(user);
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id).map(user -> {
            return modelMapper.map(user, UserResponse.class);
        });
    }

    public boolean updateUser(Long id, UserRequest userRequest) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setFirstName(userRequest.getFirstName());
                    existingUser.setLastName(userRequest.getLastName());
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }
}
