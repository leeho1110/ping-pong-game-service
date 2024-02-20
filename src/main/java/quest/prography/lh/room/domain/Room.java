package quest.prography.lh.room.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import quest.prography.lh.common.domain.TimeTrackingEntity;
import quest.prography.lh.game.domain.exception.PermissionDeniedExceptionGame;
import quest.prography.lh.room.domain.exception.UnavailableRoomStatusExceptionGame;
import quest.prography.lh.player.domain.Team;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.exception.PlayerNotFoundExceptionGame;
import quest.prography.lh.player.domain.exception.UnavailableOperationExceptionGame;

@Entity
public class Room extends TimeTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false)
    private String title;

    @Column(nullable = false, updatable = false)
    private long hostId;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Transient
    private List<Player> players;

    protected Room() {
    }

    public Room(String title, long hostId, RoomType roomType) {
        this.title = title;
        this.hostId = hostId;
        this.roomType = roomType;
        this.status = RoomStatus.WAIT;
    }

    public long id() {
        return id;
    }

    public String title() {
        return title;
    }

    public long hostId() {
        return hostId;
    }

    public RoomType roomType() {
        return roomType;
    }

    public RoomStatus status() {
        return status;
    }

    public void fillPlayer(List<Player> players) {
        if (hasNotFilledYet()) {
            this.players = players;
            return;
        }
        this.players.addAll(players);
    }

    public void fillPlayer(Player player) {
        if (hasNotFilledYet()) {
            players = new ArrayList<>();
        }
        players.add(player);
    }

    public void validateStatusIsAbleToAttend() {
        if (!isWaiting()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public void validateUserIsHost(long userId) {
        if (hostId != userId) {
            throw new PermissionDeniedExceptionGame();
        }
    }

    public void validateUserIsAttended(long targetUserId) {
        if (players.stream().noneMatch(player -> player.userId() == targetUserId)) {
            throw new PlayerNotFoundExceptionGame();
        }
    }

    public void validateStatusIsAbleToQuit() {
        if (isUnAvailableStatusToQuit()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public boolean isHostUser(long userId) {
        return hostId == userId;
    }

    public void validateStatusAbleToStart() {
        if (!isWaiting()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public void validatePlayerCountIsAbleToAttend() {
        if (playerCountIsFull()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public void validatePlayerCountIsAbleToStart() {
        if (!playerCountIsFull()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public void validateStatusIsAbleToTeamChange() {
        if (!isWaiting()) {
            throw new UnavailableRoomStatusExceptionGame();
        }
    }

    public void start() {
        this.status = RoomStatus.PROGRESS;
    }

    public void finish() {
        this.status = RoomStatus.FINISH;
    }

    public Team teamToBeAssigned() {
        return roomType.assignedTeam(countPlayerOfPriorityTeam());
    }

    public void changeTeamToOpposite(Player player) {
        if (isTeamPlayerCountIsFull(player)) {
            throw new UnavailableOperationExceptionGame();
        }
        player.changeTeamToOpposite();
    }

    public boolean isNotFinished() {
        return status != RoomStatus.FINISH;
    }

    public void startWithFinishAction(Consumer<Long> gameFinishAction) {
        start();
        gameFinishAction.accept(id);
    }

    private boolean isTeamPlayerCountIsFull(Player playerWannaTeamChange) {
        return roomType.halfCount() == countPlayerOfTeam(playerWannaTeamChange.oppositeTeam());
    }

    private long countPlayerOfTeam(Team team) {
        return players.stream()
                .filter(player -> player.team() == team)
                .count();
    }

    private boolean hasNotFilledYet() {
        return Objects.isNull(players);
    }

    private boolean isWaiting() {
        return status == RoomStatus.WAIT;
    }

    private long countPlayerOfPriorityTeam() {
        return players.stream()
                .filter(Player::isBelongToPriorityTeam)
                .count();
    }

    private boolean isUnAvailableStatusToQuit() {
        return status.isUnavailableToQuit();
    }

    private boolean playerCountIsFull() {
        return roomType.checkRoomIsFull(players.size());
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hostId=" + hostId +
                ", roomType=" + roomType +
                ", status=" + status +
                ", players=" + players +
                '}';
    }
}