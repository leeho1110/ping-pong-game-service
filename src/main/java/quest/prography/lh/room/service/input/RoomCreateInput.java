package quest.prography.lh.room.service.input;

import quest.prography.lh.room.domain.RoomType;

public record RoomCreateInput(
        long userId,
        RoomType roomType,
        String title
) {

}
