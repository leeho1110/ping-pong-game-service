package quest.prography.lh.user.resource.response;

import java.util.List;
import org.springframework.data.domain.Page;
import quest.prography.lh.user.service.output.UserFindOutput;

public record UserPageableResponse(
        long totalElements,
        long totalPages,
        List<UserDetailResponse> userList
) {

    public static UserPageableResponse from(Page<UserFindOutput> userPage) {
        return new UserPageableResponse(
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.stream()
                        .map(UserDetailResponse::from)
                        .toList()
        );
    }
}
