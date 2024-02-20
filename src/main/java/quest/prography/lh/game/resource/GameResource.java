package quest.prography.lh.game.resource;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import quest.prography.lh.common.response.ApiResponse;
import quest.prography.lh.game.resource.request.GameInitiateRequest;
import quest.prography.lh.game.resource.request.GameStartRequest;
import quest.prography.lh.game.service.GameCommandService;
import quest.prography.lh.game.service.input.GameStartInput;

@RestController
public class GameResource {

    private final GameCommandService gameCommandService;

    public GameResource(GameCommandService gameCommandService) {
        this.gameCommandService = gameCommandService;
    }

    /**
     * 초기화 API
     *
     * @author 이호
     */
    @ApiOperation(value = "초기화 API")
    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse initiate(@RequestBody GameInitiateRequest request) {
        gameCommandService.initiateUsers(request.toInitiateInput());
        return ApiResponse.success();
    }

    /**
     * 게임시작 API
     *
     * @author 이호
     */
    @ApiOperation(value = "게임시작 API")
    @PutMapping(value = "/room/start/{roomId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse start(@PathVariable long roomId, @RequestBody GameStartRequest request) {
        gameCommandService.startGame(new GameStartInput(roomId, request.userId()));
        return ApiResponse.success();
    }
}
