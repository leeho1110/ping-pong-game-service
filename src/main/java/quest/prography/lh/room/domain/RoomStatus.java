package quest.prography.lh.room.domain;

public enum RoomStatus {
    WAIT,
    PROGRESS,
    FINISH;

    public boolean isUnavailableToQuit(){
        return this == PROGRESS || this == FINISH;
    }
}
