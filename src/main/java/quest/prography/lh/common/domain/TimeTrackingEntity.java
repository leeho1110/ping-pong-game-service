package quest.prography.lh.common.domain;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeTrackingEntity {

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column
    protected Instant updatedAt = Instant.now();

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
