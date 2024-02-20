package quest.prography.lh.room.resource.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import org.springframework.data.domain.Page;
import quest.prography.lh.room.service.output.RoomFindOutput;

@JsonInclude(Include.NON_NULL)
public record RoomPageableResponse(
        long totalElements,
        long totalPages,
        List<RoomDetailResponse> roomList
) {

    public static RoomPageableResponse from(Page<RoomFindOutput> roomPage) {
        return new RoomPageableResponse(
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.stream()
                        .map(RoomDetailResponse::fromWithoutTimeTracking)
                        .toList()
        );
    }
}
