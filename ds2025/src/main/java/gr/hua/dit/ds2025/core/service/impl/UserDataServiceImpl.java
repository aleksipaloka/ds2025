package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.UserDataService;
import gr.hua.dit.ds2025.core.service.mapper.UserMapper;
import gr.hua.dit.ds2025.core.service.model.UserView;

import java.util.List;

public class UserDataServiceImpl implements UserDataService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDataServiceImpl(final UserRepository userRepository,
                                 final UserMapper userMapper) {
        if (userRepository == null) throw new NullPointerException();
        if (userMapper == null) throw new NullPointerException();
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserView> getAllUsers() {
        final List<User> userList = this.userRepository.findAll();
        final List<UserView> userViewList = userList
                .stream()
                .map(this.userMapper::convertUserToUserView)
                .toList();
        return userViewList;
    }
}
