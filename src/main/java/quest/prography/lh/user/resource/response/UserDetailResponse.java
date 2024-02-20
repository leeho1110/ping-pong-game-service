package quest.prography.lh.user.resource.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.Instant;
import quest.prography.lh.user.domain.UserStatus;
import quest.prography.lh.user.service.output.UserFindOutput;

public record UserDetailResponse(
        long id,
        long fakerId,
        String name,
        String email,
        UserStatus status,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        Instant createdAt,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        Instant updatedAt
) {

    public static UserDetailResponse from(UserFindOutput output) {
        return new UserDetailResponse(
                output.id(),
                output.fakerId(),
                output.name(),
                output.email().value(),
                output.status(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
