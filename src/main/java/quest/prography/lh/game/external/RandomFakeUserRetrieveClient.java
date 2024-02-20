package quest.prography.lh.game.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "userInitiateClient", url = "https://fakerapi.it")
public interface RandomFakeUserRetrieveClient {

    @GetMapping("/api/v1/users")
    FakeApiRetrieveUserResponse retrieveRandomFakedUsers(
            @RequestParam("_seed") int seed,
            @RequestParam("_quantity") int quantity,
            @RequestParam("_locale") String locale
    );
}
