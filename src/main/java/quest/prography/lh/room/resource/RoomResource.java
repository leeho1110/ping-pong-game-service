package quest.prography.lh.room.resource;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import quest.prography.lh.common.response.ApiResponse;
import quest.prography.lh.room.resource.request.RoomCreateRequest;
import quest.prography.lh.room.resource.request.RoomQuitRequest;
import quest.prography.lh.room.resource.response.RoomDetailResponse;
import quest.prography.lh.room.resource.response.RoomPageableResponse;
import quest.prography.lh.room.service.RoomCommandService;
import quest.prography.lh.room.service.RoomQueryService;
import quest.prography.lh.room.service.input.RoomAttendInput;
import quest.prography.lh.room.service.input.RoomQuitInput;

@RestController
public class RoomResource {

    private final RoomCommandService roomCommandService;
    private final RoomQueryService roomQueryService;

    public RoomResource(RoomCommandService roomCommandService, RoomQueryService roomQueryService) {
        this.roomCommandService = roomCommandService;
        this.roomQueryService = roomQueryService;
    }

    /**
     * 방 생성 API
     *
     * @author 이호
     */
    @ApiOperation(value = "방 생성 API")
    @PostMapping(value = "/room", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse create(@RequestBody RoomCreateRequest request) {
        roomCommandService.createNewRoom(request.toInput());
        return ApiResponse.success();
    }

    /**
     * 방 전체 조회 API
     *
     * @author 이호
     */
    @ApiOperation(value = "방 전체 조회 API")
    @GetMapping("/room")
    public ApiResponse<RoomPageableResponse> findAll(@PageableDefault(size = 20, sort = "id", direction = Direction.ASC) Pageable pageable) {
        var result = RoomPageableResponse.from(roomQueryService.findAllOfRoomBy(pageable));
        return ApiResponse.success(result);
    }

    /**
     * 방 상세 조회 API
     *
     * @author 이호
     */
    @ApiOperation(value = "방 상세 조회 API")
    @GetMapping("/room/{roomId}")
    public ApiResponse<RoomDetailResponse> findRoom(@PathVariable long roomId) {
        var result = RoomDetailResponse.fromWithTimeTracking(roomQueryService.findRoomBy(roomId));
        return ApiResponse.success(result);
    }

    /**
     * 방 참가 API
     *
     * @author 이호
     */
    @ApiOperation(value = "방 참가 API")
    @PostMapping(value = "/room/attention/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse attendRoom(@PathVariable long roomId, @RequestBody RoomAttendInput request) {
        roomCommandService.attendWaitingRoom(new RoomAttendInput(roomId, request.userId()));
        return ApiResponse.success();
    }

    /**
     * 방 나가기 API
     *
     * @author 이호
     */
    @ApiOperation(value = "방 나가기 API")
    @PostMapping(value = "/room/out/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse quitRoom(@PathVariable long roomId, @RequestBody RoomQuitRequest request) {
        roomCommandService.quitRoom(new RoomQuitInput(roomId, request.userId()));
        return ApiResponse.success();
    }
}
