package quest.prography.lh.room.service.output;

import java.time.Instant;
import quest.prography.lh.room.domain.RoomStatus;
import quest.prography.lh.room.domain.RoomType;
import quest.prography.lh.room.domain.Room;

public record RoomFindOutput(
        long id,
        String title,
        long hostId,
        RoomType roomType,
        RoomStatus gameStatus,
        Instant createdAt,
        Instant updatedAt
) {

    public static RoomFindOutput from(Room room) {
        return new RoomFindOutput(
                room.id(),
                room.title(),
                room.hostId(),
                room.roomType(),
                room.status(),
                room.createdAt(),
                room.updatedAt());
    }
}
