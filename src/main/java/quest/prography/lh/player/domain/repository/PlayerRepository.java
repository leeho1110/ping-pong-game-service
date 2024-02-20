package quest.prography.lh.player.domain.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import quest.prography.lh.player.domain.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAllByUserId(long userId);

    List<Player> findAllByRoomId(long roomId);

    @Transactional
    void deleteAllByRoomId(long roomId);

    void deleteByRoomIdAndUserId(long roomId, long userId);
}
