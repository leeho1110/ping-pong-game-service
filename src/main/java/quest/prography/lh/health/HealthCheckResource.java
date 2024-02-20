package quest.prography.lh.health;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import quest.prography.lh.common.response.ApiResponse;

@RestController
public class HealthCheckResource {

    /**
     * 헬스체크 API
     * @author 이호
     */
    @ApiOperation(value = "헬스체크 API")
    @GetMapping("/health")
    public ApiResponse healthCheck() {
        return ApiResponse.success();
    }
}