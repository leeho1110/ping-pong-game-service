package quest.prography.lh.room.resource.request;

import quest.prography.lh.room.domain.RoomType;
import quest.prography.lh.room.service.input.RoomCreateInput;

public record RoomCreateRequest(
        long userId,
        String roomType,
        String title
) {

    public RoomCreateInput toInput() {
        return new RoomCreateInput(userId, RoomType.findBy(roomType), title);
    }
}
