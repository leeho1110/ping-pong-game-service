package quest.prography.lh.game.external;

import java.util.List;

public record FakeApiRetrieveUserResponse(
        String status,
        int code,
        int total,
        List<FakeUserSpec> data
) {

}
