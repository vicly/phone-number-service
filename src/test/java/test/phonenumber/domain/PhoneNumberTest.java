package test.phonenumber.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberTest {
    private Long customerId = 1L;

    @Test
    public void isActivated_true_whenCustomerIdIsAssociated() {
        assertThat(new PhoneNumber("+61423950361", customerId).isActivated()).isTrue();
    }

    @Test
    public void isActivated_false_whenCustomerIdIsAssociated() {
        assertThat(new PhoneNumber("+61423950361").isActivated()).isFalse();
    }

    @Test
    public void objectEqual_whenIdSame() {
        PhoneNumber p1 = new PhoneNumber();
        p1.setId(1L);
        p1.setNumber("+61423950361");

        PhoneNumber p2 = new PhoneNumber();
        p2.setId(1L);
        p2.setNumber("+61423950362");

        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isEqualTo(p2);
    }

    @Test
    public void objectNotEqual_whenIdDifferent() {
        String number = "+61423950361";
        PhoneNumber p1 = new PhoneNumber();
        p1.setId(1L);
        p1.setNumber(number);

        PhoneNumber p2 = new PhoneNumber();
        p2.setId(2L);
        p2.setNumber(number);

        assertThat(p1.hashCode()).isNotEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p2);
    }
}
