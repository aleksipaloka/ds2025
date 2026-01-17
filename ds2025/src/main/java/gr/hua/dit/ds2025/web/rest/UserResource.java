package gr.hua.dit.ds2025.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.hua.dit.ds2025.core.service.UserDataService;
import gr.hua.dit.ds2025.core.service.model.UserView;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserDataService userDataService;

    public UserResource(final UserDataService userDataService) {
        if (userDataService == null) throw new NullPointerException();
        this.userDataService = userDataService;
    }

    @PreAuthorize("hasRole('INTEGRATION_READ')")
    @GetMapping("")
    public List<UserView> users() {
        final List<UserView> personViewList = this.userDataService.getAllUsers();
        return personViewList;
    }
}
