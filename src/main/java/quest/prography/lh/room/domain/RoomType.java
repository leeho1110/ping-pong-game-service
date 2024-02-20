package quest.prography.lh.room.domain;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import quest.prography.lh.player.domain.Team;

public enum RoomType {
    SINGLE(
            playerCount -> playerCount >= AttendanceLimit.SINGLE_MAX,
            playerCountOfPriority -> playerCountOfPriority < AttendanceLimit.SINGLE_HALF ? Team.priority() : Team.BLUE
    ),
    DOUBLE(
            user -> user >= AttendanceLimit.DOUBLE_MAX,
            playerCountOfPriority -> playerCountOfPriority < AttendanceLimit.DOUBLE_HALF ? Team.priority() : Team.BLUE
    );

    private final Predicate<Long> isRoomFull;
    private final Function<Long, Team> teamAssignRule;

    RoomType(Predicate<Long> isRoomFull, Function<Long, Team> teamAssignRule) {
        this.isRoomFull = isRoomFull;
        this.teamAssignRule = teamAssignRule;
    }

    public static RoomType findBy(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + value));
    }

    public boolean checkRoomIsFull(long attendedUserCount) {
        return this.isRoomFull.test(attendedUserCount);
    }

    public Team assignedTeam(long priorityTeamCount) {
        return this.teamAssignRule.apply(priorityTeamCount);
    }

    public long halfCount() {
        return switch (this) {
            case SINGLE -> 1;
            case DOUBLE -> 2;
        };
    }

    private static class AttendanceLimit {

        private static final long SINGLE_MAX = 2;
        private static final long DOUBLE_MAX = 4;

        private static final long SINGLE_HALF = 1;
        private static final long DOUBLE_HALF = 2;
    }
}
