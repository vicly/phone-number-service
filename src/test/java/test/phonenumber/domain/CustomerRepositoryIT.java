package test.phonenumber.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ensure Spring JPA based DB access is good.
 */
@DataJpaTest
@AutoConfigureTestDatabase
public class CustomerRepositoryIT {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    public void createAndUpdate() {
        Customer customer = new Customer("Vic");
        customerRepository.saveAndFlush(customer);
        assertThat(customer.getId()).isNotNull();
        assertThat(customerRepository.findById(customer.getId())).isPresent();
        assertThat(customer.getCreatedAt()).isNotNull();
        assertThat(customer.getUpdatedAt()).isNull();

        customer.setName("Vic L");
        customerRepository.saveAndFlush(customer);
        assertThat(customer.getCreatedAt()).isNotNull();
        assertThat(customer.getUpdatedAt()).isNotNull();
    }
}
