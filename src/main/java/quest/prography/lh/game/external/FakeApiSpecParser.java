package quest.prography.lh.game.external;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;
import quest.prography.lh.user.domain.User;

@Component
public class FakeApiSpecParser implements ExternalUserDataParser<FakeUserSpec> {

    @Override
    public List<User> parse(List<FakeUserSpec> specification) {
        return specification.stream()
                .map(fakeUserSpec -> new User(
                        fakeUserSpec.id(),
                        fakeUserSpec.username(),
                        fakeUserSpec.email()))
                .sorted(Comparator.comparingLong(User::fakerId))
                .toList();
    }

}
