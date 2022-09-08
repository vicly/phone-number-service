package test.phonenumber.domain;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Ensure Spring JPA based DB access is good.
 */
@DataJpaTest
@AutoConfigureTestDatabase
public class PhoneNumberRepositoryIT {

    @Autowired
    PhoneNumberRepository phoneNumberRepository;

    @Test
    public void saveAndGet() {
        PhoneNumber phoneNumber = new PhoneNumber("+61423950361");
        this.phoneNumberRepository.saveAndFlush(phoneNumber);

        Long id = phoneNumber.getId();
        assertThat(id).isNotNull();
        assertThat(this.phoneNumberRepository.findById(id)).hasValue(phoneNumber);
    }

    @Test
    public void save_error_whenDuplicateNumber() {
        PhoneNumber n1 = new PhoneNumber("+61423950362", 1L);
        PhoneNumber n2 = new PhoneNumber("+61423950362", 2L);
        this.phoneNumberRepository.saveAndFlush(n1);

        assertThatThrownBy(() -> this.phoneNumberRepository.saveAndFlush(n2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void save_error_whenNumberNull() {
        PhoneNumber n1 = new PhoneNumber("+61423950362", 2L);
        n1.setNumber(null);
        assertThatThrownBy(() -> this.phoneNumberRepository.saveAndFlush(n1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void findByCustomerId() {
        PhoneNumber n1 = new PhoneNumber("+61423950361", 1L);
        PhoneNumber n2 = new PhoneNumber("+61423950362", 1L);
        PhoneNumber n3 = new PhoneNumber("+61423950363", 2L);
        this.phoneNumberRepository.saveAllAndFlush(Lists.list(n1, n2, n3));

        assertThat(this.phoneNumberRepository.findByCustomerId(1L)).containsExactlyInAnyOrder(n1, n2);
        assertThat(this.phoneNumberRepository.findByCustomerId(2L)).containsExactlyInAnyOrder(n3);
        assertThat(this.phoneNumberRepository.findByCustomerId(9L)).isEmpty();
    }
}
