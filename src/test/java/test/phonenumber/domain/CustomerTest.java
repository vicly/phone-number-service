package test.phonenumber.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerTest {

    @Test
    public void objectEqual_whenIdSame() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("Ada");

        Customer c2 = new Customer();
        c2.setId(1L);
        c2.setName("Tim");

        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1).isEqualTo(c2);
    }

    @Test
    public void objectNotEqual_whenIdDifferent() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("Ada");

        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Ada");

        assertThat(c1.hashCode()).isNotEqualTo(c2.hashCode());
        assertThat(c1).isNotEqualTo(c2);
    }
}
