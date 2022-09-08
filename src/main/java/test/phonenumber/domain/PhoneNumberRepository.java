package test.phonenumber.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    /**
     * Find all phone number by customer ID.
     */
    List<PhoneNumber> findByCustomerId(Long customerId);
}
