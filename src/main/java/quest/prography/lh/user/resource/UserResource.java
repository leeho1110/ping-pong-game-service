package quest.prography.lh.user.resource;

import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import quest.prography.lh.common.response.ApiResponse;
import quest.prography.lh.user.resource.response.UserPageableResponse;
import quest.prography.lh.user.service.UserQueryService;

@RestController
public class UserResource {

    private final UserQueryService userQueryService;

    public UserResource(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * 유저 전체 조회 API
     * @author 이호
     */
    @ApiOperation(value = "유저 전체 조회 API")
    @GetMapping("/user")
    public ApiResponse<UserPageableResponse> findAll(@PageableDefault(size = 20, sort = "id", direction = Direction.ASC) Pageable pageable) {
        var result = UserPageableResponse.from(userQueryService.findUser(pageable));
        return ApiResponse.success(result);
    }

}
