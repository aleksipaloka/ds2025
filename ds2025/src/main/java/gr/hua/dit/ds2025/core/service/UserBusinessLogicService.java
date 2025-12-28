package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.CreateUserRequest;
import gr.hua.dit.ds2025.core.service.model.CreateUserResult;
import org.springframework.stereotype.Service;

public interface UserBusinessLogicService {

    CreateUserResult createUser(final CreateUserRequest createUserRequest, final boolean notify);

    default CreateUserResult createUser(final CreateUserRequest createUserRequest) {
        return this.createUser(createUserRequest, true);
    }
}
