package gr.hua.dit.ds2025.core.service.mapper;

import gr.hua.dit.ds2025.core.model.User;
import gr.hua.dit.ds2025.core.service.model.UserView;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    private final ReviewMapper reviewMapper;

    public UserMapper(final ReviewMapper reviewMapper) {
        if (reviewMapper == null) throw new NullPointerException();
        this.reviewMapper = reviewMapper;
    }

    public UserView convertUserToUserView(final User user){
        if(user == null){
            return null;
        }

        final UserView userView = new UserView(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                this.reviewMapper.convertReviewToReviewView(user.getReviewsWritten()),
                this.reviewMapper.convertReviewToReviewView(user.getReviewsConcerning())
        );
        return userView;

    }
}
