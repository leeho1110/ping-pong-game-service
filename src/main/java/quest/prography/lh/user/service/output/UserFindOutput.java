package quest.prography.lh.user.service.output;

import java.time.Instant;
import quest.prography.lh.common.domain.Email;
import quest.prography.lh.user.domain.UserStatus;
import quest.prography.lh.user.domain.User;

public record UserFindOutput(
        long id,
        long fakerId,
        String name,
        Email email,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserFindOutput from(User user) {
        return new UserFindOutput(
                user.id(),
                user.fakerId(),
                user.name(),
                user.email(),
                user.status(),
                user.createdAt(),
                user.updatedAt()
        );
    }
}
