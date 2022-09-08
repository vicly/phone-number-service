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
 * Phone number domain model.
 */
@Entity
@Table(name = "phonenumbers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PhoneNumber extends BaseEntity {

    @Column(name = "phone_number", unique = true, nullable = false)
    private String number;

    @Column(name = "customer_id")
    private Long customerId;

    /**
     * Create a phone number not activated.
     */
    public PhoneNumber(String number) {
        this.number = number;
    }

    /**
     * Create a phone number associated with a customer.
     */
    public PhoneNumber(String number, Long customerId) {
        this.number = number;
        this.customerId = customerId;
    }

    /**
     * Determine whether the phone number is activated.
     *
     * Just to demo a business logic based on state.
     */
    public boolean isActivated() {
        return this.customerId != null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("number", number)
                .add("customerId", customerId)
                .add("createdAt", getCreatedAt())
                .add("updatedAt", this.getUpdatedAt())
                .toString();
    }
}
