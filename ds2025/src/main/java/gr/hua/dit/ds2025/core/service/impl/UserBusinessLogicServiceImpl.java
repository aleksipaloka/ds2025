package gr.hua.dit.ds2025.core.service.impl;

import gr.hua.dit.ds2025.core.model.Role;
import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.repositories.UserRepository;
import gr.hua.dit.ds2025.core.service.UserBusinessLogicService;
import gr.hua.dit.ds2025.core.service.mapper.UserMapper;
import gr.hua.dit.ds2025.core.service.model.CreateUserRequest;
import gr.hua.dit.ds2025.core.service.model.CreateUserResult;

import gr.hua.dit.ds2025.core.service.model.UserView;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserBusinessLogicServiceImpl implements UserBusinessLogicService {

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserBusinessLogicServiceImpl(final Validator validator,
                                          final PasswordEncoder passwordEncoder,
                                          final UserRepository userRepository,
                                          final UserMapper userMapper) {
        if (validator == null) throw new NullPointerException();
        if (passwordEncoder == null) throw new NullPointerException();
        if (userRepository == null) throw new NullPointerException();
        if (userMapper == null) throw new NullPointerException();

        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public CreateUserResult createUser(final CreateUserRequest createUserRequest, final boolean notify) {
        if (createUserRequest == null) throw new NullPointerException();

        final Set<ConstraintViolation<CreateUserRequest>> requestViolations
                = this.validator.validate(createUserRequest);
        if (!requestViolations.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final ConstraintViolation<CreateUserRequest> violation : requestViolations) {
                sb
                        .append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("\n");
            }
            return CreateUserResult.fail(sb.toString());
        }

        final String username = createUserRequest.username().strip();
        final String name = createUserRequest.name().strip();
        final String lastName = createUserRequest.lastName().strip();
        final String email = createUserRequest.email().strip();
        final String password = createUserRequest.password();

        if (this.userRepository.existsByUsernameIgnoreCase(username)) {
            return CreateUserResult.fail("Username already registered");
        }

        if (this.userRepository.existsByEmailIgnoreCase(email)) {
            return CreateUserResult.fail("Email Address already registered");
        }

        final String Password = this.passwordEncoder.encode(password);

        User user = new User();
        user.setId(null);
        user.setUsername(username);
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(Password);
        user.setRole(Role.USER);

        final Set<ConstraintViolation<User>> userViolations = this.validator.validate(user);
        if (!userViolations.isEmpty()) {
            throw new RuntimeException("invalid User instance");
        }

        user = this.userRepository.save(user);

        final UserView userView = this.userMapper.convertUserToUserView(user);

        return CreateUserResult.success(userView);
    }

    @Transactional
    @Override
    public CreateUserResult createAdmin(final String name, final String lastName, final String username,
                                        final String email, final String rawPassword) {
        if (this.userRepository.existsByUsernameIgnoreCase(username)) {
            return CreateUserResult.fail("Admin username already exists");
        }

        if (this.userRepository.existsByEmailIgnoreCase(email)) {
            return CreateUserResult.fail("Admin email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(Role.ADMIN);

        user = userRepository.save(user);

        return CreateUserResult.success(userMapper.convertUserToUserView(user));
    }

}
