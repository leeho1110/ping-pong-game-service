package quest.prography.lh.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import quest.prography.lh.player.domain.Team;
import quest.prography.lh.player.domain.Player;
import quest.prography.lh.player.domain.exception.UnavailableOperationExceptionGame;

class RoomTest {

    @Nested
    @DisplayName("SINGLE 게임의 팀 배정 시, ")
    class TeamAssignInSingleTypeGame {

        @DisplayName("레드팀이 가득 찼다면 블루팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenSingleGameAndRedTeamIsFull_thenReturnBlueTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.SINGLE);

            room.fillPlayer(new Player(hostId, 1L, Team.RED));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.BLUE);
        }

        @DisplayName("블루팀이 가득 찼다면 레드팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenSingleGameAndBlueTeamIsFull_thenReturnReadTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.SINGLE);

            room.fillPlayer(new Player(hostId, 1L, Team.BLUE));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.RED);
        }
    }

    @Nested
    @DisplayName("DOUBLE 게임의 팀 배정 시, ")
    class TeamAssignInDoubleTypeGame {

        @DisplayName("레드팀이 가득 찼다면 참가자는 블루가 된다.")
        @Test
        public void teamToBeAssigned_whenDoubleGameAndRedTeamIsFull_thenReturnBlueTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(new Player(hostId, 1L, Team.RED));
            room.fillPlayer(new Player(hostId, 2L, Team.RED));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.BLUE);
        }

        @DisplayName("블루팀이 가득 찼다면 레드팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenDoubleGameAndBlueTeamIsFull_thenReturnRedTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(new Player(hostId, 1L, Team.BLUE));
            room.fillPlayer(new Player(hostId, 2L, Team.BLUE));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.RED);
        }

        @DisplayName("각 팀이 절반 이상 찼다면 우선순위가 높은 레드팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenDoubleGameAndEachTeamHasOne_thenReturnBlueTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(new Player(hostId, 1L, Team.RED));
            room.fillPlayer(new Player(hostId, 2L, Team.BLUE));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.RED);
        }

        @DisplayName("레드팀이 가득 차고 블루팀만 한자리만 남았다면, 블루팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenDoubleGameAndRedTeamIsFullAndBlueIsHalf_thenReturnBlueTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(new Player(hostId, 1L, Team.RED));
            room.fillPlayer(new Player(hostId, 2L, Team.RED));
            room.fillPlayer(new Player(hostId, 3L, Team.BLUE));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.BLUE);
        }

        @DisplayName("블루팀이 가득 차고 레드팀만 한자리 남았다면, 레드팀을 반환한다.")
        @Test
        public void teamToBeAssigned_whenDoubleGameAndBlueTeamIsFullAndRedIsHalf_thenReturnRedTeam() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(new Player(hostId, 1L, Team.BLUE));
            room.fillPlayer(new Player(hostId, 2L, Team.BLUE));
            room.fillPlayer(new Player(hostId, 3L, Team.RED));

            // when
            Team team = room.teamToBeAssigned();

            // then
            assertThat(team).isEqualTo(Team.RED);
        }
    }

    @Nested
    @DisplayName("SINGLE 게임의 팀 변경 시, ")
    class TeamChangeInSingleTypeGame {

        @DisplayName("반대 팀이 비어있고 변경을 원하는 경우, 정상적으로 처리됩니다.")
        @Test
        void changeTeamToOpposite_whenOppositeTeamIsEmpty_TeamChangingIsWorkWell() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.SINGLE);
            Player playerWannaTeamChange = new Player(hostId, 1L, Team.RED);

            room.fillPlayer(playerWannaTeamChange);

            // when
            room.changeTeamToOpposite(playerWannaTeamChange);

            // then
            assertThat(playerWannaTeamChange.team()).isEqualTo(Team.BLUE);
        }

        @DisplayName("반대 팀이 가득 차있을 때 변경을 원하는 경우, 예외를 반환합니다.")
        @Test
        void changeTeamToOpposite_whenOppositeTeamIsFull_throwUnavailableOperationException() {
            // given
            long hostId = 1L;
            Room room = new Room("test", hostId, RoomType.SINGLE);

            Player playerWannaTeamChange = new Player(hostId, 1L, Team.RED);
            room.fillPlayer(playerWannaTeamChange);

            Player playerInOppositeTeam = new Player(hostId, 1L, Team.BLUE);
            room.fillPlayer(playerInOppositeTeam);

            // when & then
            assertThatThrownBy(() -> room.changeTeamToOpposite(playerWannaTeamChange))
                    .isInstanceOf(UnavailableOperationExceptionGame.class);
        }
    }

    @Nested
    @DisplayName("DOUBLE 게임의 팀 변경 시, ")
    class TeamChangeInDoubleTypeGame {

        @DisplayName("반대 팀 비어있고 변경을 원하는 경우, 정상적으로 처리됩니다.")
        @Test
        void changeTeamToOpposite_whenOppositeTeamIsEmpty_TeamChangingIsWorkWell() {
            // given
            long hostId = 1L;
            long roomId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);
            Player host_RED = player(roomId, Team.RED);

            room.fillPlayer(host_RED);
            room.fillPlayer(player(roomId, Team.RED));
            room.fillPlayer(player(roomId, Team.BLUE));

            // when
            room.changeTeamToOpposite(host_RED);

            // then
            assertThat(host_RED.team()).isEqualTo(Team.BLUE);
        }

        @DisplayName("반대 팀 가득 차있을 때 변경을 원하는 경우, 예외를 반환합니다.")
        @Test
        void changeTeamToOpposite_whenOppositeTeamIsFull_throwUnavailableOperationException() {
            // given
            long hostId = 1L;
            long roomId = 1L;
            Room room = new Room("test", hostId, RoomType.DOUBLE);

            room.fillPlayer(player(roomId, Team.RED));
            room.fillPlayer(player(roomId, Team.BLUE));
            room.fillPlayer(player(roomId, Team.BLUE));

            // when & then
            assertThatThrownBy(() -> room.changeTeamToOpposite(new Player(hostId, roomId, Team.RED)))
                    .isInstanceOf(UnavailableOperationExceptionGame.class);
        }

        private Player player(long roomId, Team team) {
            return new Player(1L, roomId, team);
        }
    }
}