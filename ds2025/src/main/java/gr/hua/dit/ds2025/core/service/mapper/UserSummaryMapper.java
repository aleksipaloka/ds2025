package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.service.model.UserSummaryView;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserSummaryMapper {

    public UserSummaryView convertUserToUserSummaryView(final User user) {
        if (user == null) {
            return null;
        }

        return new UserSummaryView(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getUsername()
        );
    }

}
