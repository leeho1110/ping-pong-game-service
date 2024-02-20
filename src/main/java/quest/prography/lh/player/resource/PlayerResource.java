package quest.prography.lh.player.resource;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import quest.prography.lh.common.response.ApiResponse;
import quest.prography.lh.player.resource.request.TeamChangeRequest;
import quest.prography.lh.player.service.PlayerCommandService;
import quest.prography.lh.player.service.input.TeamChangeInput;

@RestController
public class PlayerResource {

    private final PlayerCommandService playerCommandService;

    public PlayerResource(PlayerCommandService playerCommandService) {
        this.playerCommandService = playerCommandService;
    }

    /**
     * 팀 변경 API
     *
     * @author 이호
     */
    @ApiOperation(value = "팀 변경 API")
    @PutMapping(value = "/team/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse changeTeam(@PathVariable long roomId, @RequestBody TeamChangeRequest request) {
        playerCommandService.changeTeam(new TeamChangeInput(roomId, request.userId()));
        return ApiResponse.success();
    }
}
