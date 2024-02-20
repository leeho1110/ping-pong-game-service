package quest.prography.lh.game.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import quest.prography.lh.common.RecyclableSpringBeanContext;
import quest.prography.lh.game.domain.exception.PermissionDeniedExceptionGame;
import quest.prography.lh.game.service.input.GameStartInput;
import quest.prography.lh.room.domain.RoomStatus;
import quest.prography.lh.room.domain.RoomType;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.exception.UnavailableRoomStatusExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.user.domain.User;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.player.domain.Team;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.repository.PlayerRepository;

class GameCommandServiceTest extends RecyclableSpringBeanContext {

    @Autowired
    GameCommandService sut;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    TaskScheduler taskScheduler;

    @Nested
    @DisplayName("게임을 시작하려할 때, ")
    class GameStart {

        @DisplayName("사용자가 호스트가 아닌 경우, 예외를 반환합니다.")
        @Test
        void gameStart_whenUserIsNotHost_throwPermissionDeniedException() {
            // given
            User host = userRepository.save(userFixture());
            User userDoNotHosting = userRepository.save(userFixture());

            Room room = roomRepository.save(singleRoomFixture(host));
            attend(host, room, Team.RED);
            attend(host, room, Team.BLUE);

            // when & then
            assertThatThrownBy(() -> sut.startGame(new GameStartInput(room.id(), userDoNotHosting.id())))
                    .isInstanceOf(PermissionDeniedExceptionGame.class);
        }

        @DisplayName("방의 인원이 가득차지 않은 경우, 예외를 반환합니다.")
        @Test
        void gameStart_whenRoomIsNotFull_throwPermissionDeniedException() {
            // given
            User host = userRepository.save(userFixture());
            Room singleTypeRoom = roomRepository.save(singleRoomFixture(host));
            attend(host, singleTypeRoom, Team.RED);

            // when & then
            assertThatThrownBy(() -> sut.startGame(new GameStartInput(singleTypeRoom.id(), host.id())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("방이 대기 상태가 아닌 경우, 예외를 반환합니다.")
        @Test
        void gameStart_whenRoomIsNotWait_throwPermissionDeniedException() {
            // given
            User host = userRepository.save(userFixture());
            Room room = roomRepository.save(singleRoomFixture(host));
            attend(host, room, Team.RED);
            attend(host, room, Team.BLUE);

            room.start();

            // when & then
            assertThatThrownBy(() -> sut.startGame(new GameStartInput(room.id(), host.id())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("정상적인 경우, 진행중 상태로 변경됩니다.")
        @Test
        void gameStart_whenRoomIsStartWorkingWell_gameStatusIsUpdatedToProgress() {
            // given
            User host = userRepository.save(userFixture());
            Room room = roomRepository.save(singleRoomFixture(host));
            attend(host, room, Team.RED);
            attend(host, room, Team.BLUE);

            // when
            sut.startGame(new GameStartInput(room.id(), host.id()));

            // then
            assertThat(roomRepository.findById(room.id()).get().status()).isEqualTo(RoomStatus.PROGRESS);
        }
    }

    private User userFixture() {
        return new User(1L, "tester", "ho.sol.lee@gmail.com");
    }

    private Room singleRoomFixture(User user) {
        return new Room("title", user.id(), RoomType.SINGLE);
    }

    private Player attend(User user, Room room, Team team) {
        return playerRepository.save(new Player(user.id(), room.id(), team));
    }

}