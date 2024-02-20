package quest.prography.lh.player.domain;

public enum Team {
    RED, BLUE;

    public Team opposite() {
        return this == RED ? BLUE : RED;
    }

    public static Team priority() {
        return RED;
    }
}
