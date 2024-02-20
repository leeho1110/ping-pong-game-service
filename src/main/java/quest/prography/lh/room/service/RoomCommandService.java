package quest.prography.lh.room.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.exception.RoomNotFoundExceptionGame;
import quest.prography.lh.room.domain.exception.UserAlreadyAttendedInAnyRoomExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.room.service.input.RoomAttendInput;
import quest.prography.lh.room.service.input.RoomCreateInput;
import quest.prography.lh.room.service.input.RoomQuitInput;
import quest.prography.lh.user.domain.User;
import quest.prography.lh.user.domain.exception.UserNotFoundExceptionGame;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.player.domain.Team;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.repository.PlayerRepository;

@Transactional
@Service
public class RoomCommandService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    public RoomCommandService(UserRepository userRepository, RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    public Room createNewRoom(final RoomCreateInput input) {
        User hostUser = getUserBy(input.userId());
        hostUser.validateIsActive();
        validateUserIsAlreadyAttended(hostUser);

        Room newRoom = saveRoom(new Room(input.title(), hostUser.id(), input.roomType()));
        savePlayer(Player.host(hostUser.id(), newRoom.id()));
        return newRoom;
    }

    public void attendWaitingRoom(final RoomAttendInput input) {
        Room roomWannaAttend = getRoomBy(input.roomId());
        roomWannaAttend.validateStatusIsAbleToAttend();

        User userWannaAttend = getUserBy(input.userId());
        userWannaAttend.validateIsActive();
        validateUserIsAlreadyAttended(userWannaAttend);

        roomWannaAttend.fillPlayer(getAllPlayerByRoom(roomWannaAttend.id()));
        roomWannaAttend.validatePlayerCountIsAbleToAttend();

        Team assignedTeam = roomWannaAttend.teamToBeAssigned();
        savePlayer(new Player(userWannaAttend.id(), roomWannaAttend.id(), assignedTeam));
    }

    public void quitRoom(final RoomQuitInput roomQuitInput) {
        User userWannaQuit = getUserBy(roomQuitInput.userId());
        Room roomWannaQuit = getRoomBy(roomQuitInput.roomId());
        roomWannaQuit.validateStatusIsAbleToQuit();

        roomWannaQuit.fillPlayer(getAllPlayerByRoom(roomWannaQuit.id()));
        roomWannaQuit.validateUserIsAttended(userWannaQuit.id());

        if(roomWannaQuit.isHostUser(roomQuitInput.userId())){
            quitAllPlayersInRoom(roomWannaQuit.id());
            roomWannaQuit.finish();
            return;
        }

        quitSpecificPlayer(roomWannaQuit.id(), userWannaQuit.id());
    }

    private List<Player> getAllPlayerByRoom(long roomId) {
        return playerRepository.findAllByRoomId(roomId);
    }

    private void validateUserIsAlreadyAttended(User user) {
        if(isAlreadyAttendedInAnyRoom(user)) {
            throw new UserAlreadyAttendedInAnyRoomExceptionGame();
        }
    }

    private boolean isAlreadyAttendedInAnyRoom(User hostUser) {
        List<Player> attendanceHistory = playerRepository.findAllByUserId(hostUser.id());
        return attendanceHistory.stream()
                .map(Player::roomId)
                .map(this::getRoomBy)
                .anyMatch(Room::isNotFinished);
    }

    private Room getRoomBy(long roomId) {
        return roomRepository.findById(roomId).orElseThrow(RoomNotFoundExceptionGame::new);
    }

    private Room saveRoom(Room room){
        return roomRepository.save(room);
    }

    private Player savePlayer(Player player){
        return playerRepository.save(player);
    }

    private User getUserBy(long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundExceptionGame::new);
    }

    private void quitSpecificPlayer(long roomId, long userId){
        playerRepository.deleteByRoomIdAndUserId(roomId, userId);
    }

    private void quitAllPlayersInRoom(long roomId){
        playerRepository.deleteAllByRoomId(roomId);
    }
}
