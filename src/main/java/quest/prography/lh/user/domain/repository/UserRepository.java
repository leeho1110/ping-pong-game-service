package quest.prography.lh.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quest.prography.lh.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
