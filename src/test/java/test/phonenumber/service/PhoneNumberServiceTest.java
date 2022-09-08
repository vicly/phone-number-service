package test.phonenumber.service;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import test.phonenumber.domain.CustomerRepository;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.domain.PhoneNumberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PhoneNumberServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    PhoneNumberRepository phoneNumberRepository;

    @InjectMocks
    PhoneNumberService phoneNumberService;

    @Test
    public void findPhoneNumber() {
        Sort sortByIdAsc = Sort.by(Sort.Order.asc("id"));
        Page<PhoneNumber> fakeResult = Mockito.mock(Page.class);
        when(this.phoneNumberRepository.findAll(any(Pageable.class))).thenReturn(fakeResult);

        Page<PhoneNumber> actual = this.phoneNumberService.findPhoneNumber(1, 20);
        assertThat(actual).isSameAs(fakeResult);
        verify(this.phoneNumberRepository).findAll(PageRequest.of(1, 20, sortByIdAsc));
    }

    @Test
    public void findPhoneNumber_page_tolerant() {
        Sort sortByIdAsc = Sort.by(Sort.Order.asc("id"));
        this.phoneNumberService.findPhoneNumber(-1, 20);
        verify(this.phoneNumberRepository).findAll(PageRequest.of(0, 20, sortByIdAsc));
    }

    @Test
    public void findPhoneNumber_size_tolerant() {
        int page = 2;
        int defaultSize = 50;
        int maxSize = 500;
        Sort sortByIdAsc = Sort.by(Sort.Order.asc("id"));

        this.phoneNumberService.findPhoneNumber(page, 0);
        verify(this.phoneNumberRepository).findAll(PageRequest.of(page, defaultSize, sortByIdAsc));

        this.phoneNumberService.findPhoneNumber(page, maxSize+1);
        verify(this.phoneNumberRepository).findAll(PageRequest.of(page, maxSize, sortByIdAsc));
    }

    @Test
    public void findAllPhoneNumberByCustomerId() {
        List<PhoneNumber> phoneNumbers = Lists.newArrayList(randomPhoneNumber());
        Long customerId = 3L;
        when(this.customerRepository.existsById(customerId)).thenReturn(true);
        when(this.phoneNumberRepository.findByCustomerId(customerId)).thenReturn(phoneNumbers);

        List<PhoneNumber> actual = this.phoneNumberService.findAllPhoneNumberByCustomerId(customerId);
        assertThat(actual).isSameAs(phoneNumbers);
    }

    @Test
    public void findAllPhoneNumberByCustomerId_error_whenArgumentNull() {
        assertThatThrownBy(() -> this.phoneNumberService.findAllPhoneNumberByCustomerId(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void findAllPhoneNumberByCustomerId_error_whenCustomerNotFound() {
        when(this.customerRepository.existsById(any())).thenReturn(false);
        assertThatThrownBy(() -> this.phoneNumberService.findAllPhoneNumberByCustomerId(1L))
                .isInstanceOf(CustomerNotFoundException.class);
        verifyNoInteractions(this.phoneNumberRepository);
    }

    @Test
    public void activatePhoneNumber() {
        Long customerId = 3L;
        PhoneNumber phoneNumber = randomPhoneNumber();
        phoneNumber.setCustomerId(null);
        when(this.customerRepository.existsById(customerId)).thenReturn(true);
        when(this.phoneNumberRepository.findById(any(Long.class))).thenReturn(Optional.of(phoneNumber));

        PhoneNumber actual = this.phoneNumberService.activatePhoneNumber(phoneNumber.getId(), customerId);
        assertThat(actual.getId()).isEqualTo(phoneNumber.getId());
        assertThat(actual.getNumber()).isEqualTo(phoneNumber.getNumber());
        assertThat(actual.getCustomerId()).isEqualTo(customerId);
        verify(this.phoneNumberRepository).save(phoneNumber);
    }

    @Test
    public void activatePhoneNumber_error_whenArgumentNull() {
        assertThatThrownBy(() -> this.phoneNumberService.activatePhoneNumber(null, 1L))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> this.phoneNumberService.activatePhoneNumber(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void activatePhoneNumber_error_whenCustomerNotFound() {
        when(this.customerRepository.existsById(any(Long.class))).thenReturn(false);
        assertThatThrownBy(() -> this.phoneNumberService.activatePhoneNumber(1L, 1L))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    public void activatePhoneNumber_error_whenPhoneNumberNotFound() {
        when(this.customerRepository.existsById(any(Long.class))).thenReturn(true);
        when(this.phoneNumberRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> this.phoneNumberService.activatePhoneNumber(1L, 1L))
                .isInstanceOf(PhoneNumberNotFoundException.class);
    }

    @Test
    public void activatePhoneNumber_error_whenPhoneNumberAlreadyActivated() {
        Long customerId = 3L;
        PhoneNumber phoneNumber = randomPhoneNumber();
        phoneNumber.setCustomerId(customerId);

        when(this.customerRepository.existsById(any(Long.class))).thenReturn(true);
        when(this.phoneNumberRepository.findById(any(Long.class))).thenReturn(Optional.of(phoneNumber));

        assertThatThrownBy(() -> this.phoneNumberService.activatePhoneNumber(1L, customerId))
                .isInstanceOf(PhoneNumberAlreadyActivatedException.class);
    }

    private PhoneNumber randomPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber("+61423950361");
        phoneNumber.setId(1L);
        return phoneNumber;
    }
}
