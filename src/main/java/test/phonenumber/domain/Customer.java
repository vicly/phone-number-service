package test.phonenumber.domain;

import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Customer domain model.
 */
@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Customer extends BaseEntity {
    @Column(name = "name")
    private String name;

    public Customer(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("name", getName())
                .add("createdAt", getCreatedAt())
                .add("updatedAt", this.getUpdatedAt())
                .toString();
    }
}
