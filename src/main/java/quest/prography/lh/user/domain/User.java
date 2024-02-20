package quest.prography.lh.user.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import quest.prography.lh.common.domain.Email;
import quest.prography.lh.common.domain.TimeTrackingEntity;
import quest.prography.lh.user.domain.exception.UnavailableUserStatusExceptionGame;

@Entity
public class User extends TimeTrackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, updatable = false)
    private long fakerId;

    @Column(nullable = false, updatable = false)
    private String name;

    @Embedded
    @Column(nullable = false, updatable = false)
    private Email email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    protected User() {
    }

    public User(long fakerId, String name, String emailText) {
        this.fakerId = fakerId;
        this.name = name;
        this.email = new Email(emailText);
        this.status = UserStatus.findStatusByRule(fakerId);
    }

    public long id() {
        return id;
    }

    public long fakerId() {
        return fakerId;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public UserStatus status() {
        return status;
    }

    public boolean isActive() {
        return status.isActive();
    }

    public void updateToWait() {
        updateStatus(UserStatus.WAIT);
    }

    public void validateIsActive() {
        if(!isActive()) {
            throw new UnavailableUserStatusExceptionGame();
        }
    }

    private void updateStatus(UserStatus status) {
        this.status = status;
    }
}
