package quest.prography.lh.player.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long roomId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Team team;

    protected Player() {
    }

    public Player(long userId, long roomId, Team team) {
        this.userId = userId;
        this.roomId = roomId;
        this.team = team;
    }

    public static Player host(long userId, long roomId) {
        return new Player(userId, roomId, Team.RED);
    }

    public long id() {
        return id;
    }

    public long roomId() {
        return roomId;
    }

    public long userId() {
        return userId;
    }

    public Team team() {
        return team;
    }

    public boolean isBelongToPriorityTeam() {
        return team == Team.priority();
    }

    public Team oppositeTeam() {
        return team.opposite();
    }

    public void changeTeamToOpposite() {
        this.team = team.opposite();
    }
}
