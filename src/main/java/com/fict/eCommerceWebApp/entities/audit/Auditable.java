package com.fict.eCommerceWebApp.entities.audit;

import com.fict.eCommerceWebApp.entities.UserEntity;
import com.fict.eCommerceWebApp.services.util.BeanUtil;
import com.fict.eCommerceWebApp.utils.DateTimeUtil;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import static com.fict.eCommerceWebApp.utils.DateTimeUtil.getFormattedTimeStamp;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
    @CreatedBy
    @Column(length = 2048, updatable = false)
    private UserEntity createdBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedBy
    @Column(length = 2048)
    private UserEntity lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserEntity getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UserEntity lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreationDatePrettyTime() {
        PrettyTime pt = BeanUtil.getBean(PrettyTime.class);
        return getFormattedTimeStamp(getCreationDate());
    }

    public String getLastModifiedPrettyTime() {
        PrettyTime pt = BeanUtil.getBean(PrettyTime.class);
        return getFormattedTimeStamp(getLastModifiedDate()) + " (" + pt.format(DateTimeUtil.convertToDateViaInstant(getLastModifiedDate())) + ")";
    }
}