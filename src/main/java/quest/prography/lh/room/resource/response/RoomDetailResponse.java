package quest.prography.lh.room.resource.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.Instant;
import quest.prography.lh.room.service.output.RoomFindOutput;

@JsonInclude(Include.NON_NULL)
public record RoomDetailResponse(
        long id,
        String title,
        long hostId,
        String roomType,
        String status,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        Instant createdAt,
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        Instant updatedAt
) {

    public static RoomDetailResponse fromWithoutTimeTracking(RoomFindOutput output) {
        return new RoomDetailResponse(
                output.id(),
                output.title(),
                output.hostId(),
                output.roomType().name(),
                output.gameStatus().name(),
                null,
                null
        );
    }

    public static RoomDetailResponse fromWithTimeTracking(RoomFindOutput output) {
        return new RoomDetailResponse(
                output.id(),
                output.title(),
                output.hostId(),
                output.roomType().name(),
                output.gameStatus().name(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
