package quest.prography.lh.room.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import quest.prography.lh.common.RecyclableSpringBeanContext;
import quest.prography.lh.room.domain.RoomStatus;
import quest.prography.lh.room.domain.RoomType;
import quest.prography.lh.room.domain.Room;
import quest.prography.lh.room.domain.exception.RoomNotFoundExceptionGame;
import quest.prography.lh.room.domain.exception.UnavailableRoomStatusExceptionGame;
import quest.prography.lh.room.domain.exception.UserAlreadyAttendedInAnyRoomExceptionGame;
import quest.prography.lh.room.domain.repository.RoomRepository;
import quest.prography.lh.room.service.input.RoomAttendInput;
import quest.prography.lh.room.service.input.RoomCreateInput;
import quest.prography.lh.room.service.input.RoomQuitInput;
import quest.prography.lh.user.domain.User;
import quest.prography.lh.user.domain.exception.UnavailableUserStatusExceptionGame;
import quest.prography.lh.user.domain.exception.UserNotFoundExceptionGame;
import quest.prography.lh.user.domain.repository.UserRepository;
import quest.prography.lh.player.domain.Team;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.exception.PlayerNotFoundExceptionGame;
import quest.prography.lh.player.domain.repository.PlayerRepository;

class RoomCommandServiceTest extends RecyclableSpringBeanContext {

    @Autowired
    RoomCommandService sut;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    PlayerRepository playerRepository;

    @DisplayName("방을 생성하려 할 때, ")
    @Nested
    class CreateNewRoom {

        @DisplayName("정상 입력에 대해 방을 생성합니다.")
        @Test
        void createNewRoom_whenNormalCase() {
            // given
            User user = userRepository.save(userFixture());
            long userId = user.id();

            // when
            Room newRoom = sut.createNewRoom(new RoomCreateInput(userId, RoomType.DOUBLE, "title"));

            // then
            List<Player> players = playerRepository.findAllByRoomId(newRoom.id());

            assertThat(newRoom.status()).isEqualTo(RoomStatus.WAIT);
            assertThat(newRoom.hostId()).isEqualTo(user.id());
            assertThat(players.get(0).userId()).isEqualTo(user.id());
            assertThat(players.get(0).isBelongToPriorityTeam()).isTrue();
        }

        @DisplayName("방을 생성하려는 호스트가 활성 상태가 아닐 경우, 예외를 반환합니다.")
        @Test
        void createNewRoom_whenHostStatusIsNotActive_throwUnavailableHostStatusException() {
            // given
            User user = userRepository.save(userFixture());
            user.updateToWait();

            // when & then
            assertThatThrownBy(() -> sut.createNewRoom(new RoomCreateInput(user.id(), RoomType.DOUBLE, "title")))
                .isInstanceOf(UnavailableUserStatusExceptionGame.class);
        }

        @DisplayName("방을 생성하려는 호스트가 이미 참여한 방이 있는 경우, 예외를 반환합니다.")
        @Test
        void createNewRoom_whenUserAlreadyAttended_throwUserAlreadyAttendedException() {
            // given
            User user = userRepository.save(userFixture());
            Room room = roomRepository.save(roomFixture(user));
            attend(user, room, Team.RED);

            // when & then
            assertThatThrownBy(() -> sut.createNewRoom(new RoomCreateInput(user.id(), RoomType.DOUBLE, "title")))
                .isInstanceOf(UserAlreadyAttendedInAnyRoomExceptionGame.class);
        }
    }

    @DisplayName("방에 참가하려 할 때, ")
    @Nested
    class AttendWaitingRoom {

        @DisplayName("대기 상태가 아닌 방인 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenRoomIsNotWaiting_throwUnavailableRoomStatusException() {
            // given
            User user = userRepository.save(userFixture());
            Room room = roomRepository.save(roomFixture(user));
            room.start();

            // when & then
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(room.id(), user.id())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("활성 상태가 아닌 참가자인 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenUserStatusIsNotActive_throwUnavailableHostStatusException() {
            // given
            User user = userRepository.save(userFixture());
            user.updateToWait();
            Room room = roomRepository.save(roomFixture(user));

            // when & then
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(room.id(), user.id())))
                    .isInstanceOf(UnavailableUserStatusExceptionGame.class);
        }

        @DisplayName("참가자가 이미 다른 방에 참여한 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenUserAlreadyAttended_throwUserAlreadyAttendedException() {
            // given
            User user = userRepository.save(userFixture());
            Room room = roomRepository.save(roomFixture(user));
            attend(user, room, Team.RED);

            // when & then
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(room.id(), user.id())))
                    .isInstanceOf(UserAlreadyAttendedInAnyRoomExceptionGame.class);
        }

        @DisplayName("정원이 이미 가득 찬 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenRoomIsFull_throwUnavailableRoomStatusException() {
            // given
            User hostUser = userRepository.save(userFixture());
            User anotherUser = userRepository.save(userFixture());
            Room singleRoom = roomRepository.save(roomFixture(hostUser));

            attend(hostUser, singleRoom, Team.RED);
            attend(anotherUser, singleRoom, Team.BLUE);

            User willRefusedPlayer = userRepository.save(userFixture());

            // when & then
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(singleRoom.id(), willRefusedPlayer.id())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("존재하지 않는 방인 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenRoomNotFound_throw() {
            // given
            long unknownRoomId = 1L;

            // when
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(unknownRoomId, 1L)))
                    .isInstanceOf(RoomNotFoundExceptionGame.class);
        }

        @DisplayName("존재하지 않는 사용자인 경우, 예외를 반환합니다.")
        @Test
        void attendWaitingRoom_whenUserNotFound_throw() {
            // given
            User hostUser = userRepository.save(userFixture());
            Room room = roomRepository.save(roomFixture(hostUser));
            long unknownUserId = 9999L;

            // when
            assertThatThrownBy(() -> sut.attendWaitingRoom(new RoomAttendInput(room.id(), unknownUserId)))
                    .isInstanceOf(UserNotFoundExceptionGame.class);
        }
    }


    @DisplayName("방에서 나가려 할 때, ")
    @Nested
    class QuitRoom {

        @DisplayName("사용자가 방에 참가하지 않은 경우, 예외를 반환합니다.")
        @Test
        void quitRoom_whenUserIsNotAttendRequestedRoom_throwUserNotAttendedException(){
            // given
            User user = userRepository.save(userFixture());
            Room roomWannaQuit = roomRepository.save(roomFixture(user));

            User anotherUser = userRepository.save(userFixture());
            Room anotherRoom = roomRepository.save(roomFixture(user));

            attend(user, roomWannaQuit, Team.RED);
            attend(anotherUser, anotherRoom, Team.RED);

            // when & then
            assertThatThrownBy(() -> sut.quitRoom(new RoomQuitInput(roomWannaQuit.id(), anotherUser.id())))
                    .isInstanceOf(PlayerNotFoundExceptionGame.class);
        }

        @DisplayName("방이 진행중인 경우, 예외를 반환합니다.")
        @Test
        void quitRoom_whenRoomIsAlreadyProgress_throwUnavailableRoomStatusException(){
            // given
            User hostUser = userRepository.save(userFixture());
            Room roomWannaQuit = roomRepository.save(roomFixture(hostUser));
            Player player = attend(hostUser, roomWannaQuit, Team.RED);

            roomWannaQuit.start();

            // when & then
            assertThatThrownBy(() -> sut.quitRoom(new RoomQuitInput(roomWannaQuit.id(), player.userId())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("방이 종료된 경우, 예외를 반환합니다.")
        @Test
        void quitRoom_whenRoomIsAlreadyFinished_throwUnavailableRoomStatusException(){
            // given
            User hostUser = userRepository.save(userFixture());
            Room roomWannaQuit = roomRepository.save(roomFixture(hostUser));
            attend(hostUser, roomWannaQuit, Team.RED);

            roomWannaQuit.finish();

            // when & then
            assertThatThrownBy(() -> sut.quitRoom(new RoomQuitInput(roomWannaQuit.id(), hostUser.id())))
                    .isInstanceOf(UnavailableRoomStatusExceptionGame.class);
        }

        @DisplayName("호스트가 방을 나가는 경우, 방 내 모든 인원이 나가지며 완료 상태로 방 상태가 변경됩니다.")
        @Test
        void quitRoom_whenHostQuitRoom_allPlayersQuitRoom(){
            // given
            User hostUser = userRepository.save(userFixture());
            User anotherUser = userRepository.save(userFixture());
            Room roomWannaQuit = roomRepository.save(roomFixture(hostUser));

            attend(hostUser, roomWannaQuit, Team.RED);
            attend(anotherUser, roomWannaQuit, Team.BLUE);

            // when
            sut.quitRoom(new RoomQuitInput(roomWannaQuit.id(), hostUser.id()));

            // then
            assertThat(playerRepository.findAllByRoomId(roomWannaQuit.id())).isEmpty();
            assertThat(roomRepository.findById(roomWannaQuit.id()).get().status()).isEqualTo(RoomStatus.FINISH);
        }

        @DisplayName("존재하지 않는 방에 대한 요청이라면, 예외를 반환합니다.")
        @Test
        void quitRoom_whenRoomNotFound_throwRoomNotFoundException(){
            // given
            long unknownRoomId = 1L;
            User user = userRepository.save(userFixture());

            // when & then
            assertThatThrownBy(() -> sut.quitRoom(new RoomQuitInput(unknownRoomId, user.id())))
                    .isInstanceOf(RoomNotFoundExceptionGame.class);
        }

        @DisplayName("존재하지 않는 사용자에 대한 요청이라면, 예외를 반환합니다.")
        @Test
        void quitRoom_whenUserNotFound_throwUserNotFoundException(){
            // given
            User hostUser = userRepository.save(userFixture());
            Room roomWannaQuit = roomRepository.save(roomFixture(hostUser));
            long unknownUserId = 9999L;

            // when & then
            assertThatThrownBy(() -> sut.quitRoom(new RoomQuitInput(roomWannaQuit.id(), unknownUserId)))
                    .isInstanceOf(UserNotFoundExceptionGame.class);
        }
    }

    private Player attend(User user, Room room, Team team) {
        return playerRepository.save(new Player(user.id(), room.id(), team));
    }

    private User userFixture() {
        return new User(1L, "tester", "ho.sol.lee@gmail.com");
    }

    private Room roomFixture(User user) {
        return new Room("title", user.id(), RoomType.SINGLE);
    }
}