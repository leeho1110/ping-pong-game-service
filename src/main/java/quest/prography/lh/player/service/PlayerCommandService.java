package quest.prography.lh.player.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.exception.RoomNotFoundExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.user.domain.User;
import quest.prography.lh.user.domain.exception.UserNotFoundExceptionGame;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.exception.PlayerNotFoundExceptionGame;
import quest.prography.lh.player.domain.repository.PlayerRepository;
import quest.prography.lh.player.service.input.TeamChangeInput;

@Transactional
@Service
public class PlayerCommandService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    public PlayerCommandService(UserRepository userRepository, RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    public void changeTeam(final TeamChangeInput teamChangeInput) {
        Room room = getRoomBy(teamChangeInput.roomId());
        room.validateStatusIsAbleToTeamChange();

        User userWannaTeamChange = getUserBy(teamChangeInput.userId());
        var players = getAllPlayerByRoom(room.id());
        room.fillPlayer(players);
        room.validateUserIsAttended(userWannaTeamChange.id());

        Player player = findMatchedPlayerIn(userWannaTeamChange.id(), players);
        room.changeTeamToOpposite(player);
    }

    private Player findMatchedPlayerIn(long userWannaTeamChangeId, List<Player> players) {
        return players.stream()
                .filter(player -> player.userId() == userWannaTeamChangeId)
                .findFirst()
                .orElseThrow(PlayerNotFoundExceptionGame::new);
    }

    private Room getRoomBy(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundExceptionGame::new);
    }

    private User getUserBy(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundExceptionGame::new);
    }

    private List<Player> getAllPlayerByRoom(long roomId) {
        return playerRepository.findAllByRoomId(roomId);
    }
}
