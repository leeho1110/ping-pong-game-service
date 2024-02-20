package quest.prography.lh.game.external;

import java.util.Collection;
import java.util.List;
import quest.prography.lh.user.domain.User;

public interface ExternalUserDataParser<T> {

    Collection<User> parse(List<T> specifications);
}
