package test.phonenumber.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import test.phonenumber.domain.CustomerRepository;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.domain.PhoneNumberRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;


/**
 * Application service consumed by controller.
 */
@Service
public class PhoneNumberService {

    private static final int MAX_SIZE = 500;
    private static final int DEFAULT_SIZE = 50;

    private CustomerRepository customerRepository;
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    public PhoneNumberService(CustomerRepository customerRepository, PhoneNumberRepository phoneNumberRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.phoneNumberRepository = Objects.requireNonNull(phoneNumberRepository);
    }

    /**
     * Find phone numbers.
     */
    public Page<PhoneNumber> findPhoneNumber(int page, int size) {
        Sort sortByIdAsc = Sort.by(Sort.Order.asc("id"));
        Pageable pageable = PageRequest.of(getPage(page), getSize(size), sortByIdAsc);
        return this.phoneNumberRepository.findAll(pageable);
    }

    private int getPage(int rawPage) {
        return rawPage < 0 ? 0 : rawPage;
    }

    private int getSize(int rawSize) {
        if (rawSize <= 0) {
            return DEFAULT_SIZE;
        } else if (rawSize > MAX_SIZE) {
            return MAX_SIZE;
        } else {
            return rawSize;
        }
    }

    /**
     * Find all mobile numbers for a customer.
     *
     * @throws CustomerNotFoundException if customer not exist
     */
    public List<PhoneNumber> findAllPhoneNumberByCustomerId(Long customerId) {
        Objects.requireNonNull(customerId);
        checkCustomerExists(customerId);
        return this.phoneNumberRepository.findByCustomerId(customerId);
    }

    /**
     * Activate a phone number for specific customer.
     *
     * @throws CustomerNotFoundException if customer not exist
     * @throws PhoneNumberNotFoundException if phone number not exist
     * @throws PhoneNumberAlreadyActivatedException if phone already activated
     */
    @Transactional
    public PhoneNumber activatePhoneNumber(Long phoneNumberId, Long customerId) {
        Objects.requireNonNull(phoneNumberId);
        Objects.requireNonNull(customerId);

        checkCustomerExists(customerId);

        PhoneNumber phoneNumber = this.phoneNumberRepository
                .findById(phoneNumberId)
                .orElseThrow(() -> new PhoneNumberNotFoundException(phoneNumberId));

        if (phoneNumber.isActivated()) {
            throw new PhoneNumberAlreadyActivatedException(phoneNumberId);
        }

        phoneNumber.setCustomerId(customerId);
        this.phoneNumberRepository.save(phoneNumber);
        return phoneNumber;
    }

    private void checkCustomerExists(Long customerId) {
        boolean customerExists = this.customerRepository.existsById(customerId);
        if (!customerExists) {
            throw new CustomerNotFoundException(customerId);
        }
    }
}
