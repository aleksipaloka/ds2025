package gr.hua.dit.ds2025.core.service;

import gr.hua.dit.ds2025.core.service.model.UserView;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserDataService {

    List<UserView> getAllUsers();
}
