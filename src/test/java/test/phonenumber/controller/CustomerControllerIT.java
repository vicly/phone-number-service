package test.phonenumber.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.phonenumber.domain.Customer;
import test.phonenumber.domain.CustomerRepository;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.domain.PhoneNumberRepository;
import test.phonenumber.dto.PhoneNumberDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CustomerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void listCustomerPhoneNumber_404_ifCustomerNotExist() {
        Long nonExistCustomerId = 9999999L;
        ResponseEntity response = this.restTemplate.getForEntity(listCustomerPhoneNumberUrl(nonExistCustomerId), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void listCustomerPhoneNumber_emptyList_ifNoPhoneNumberFound() {
        Customer tod = new Customer("Tod");
        this.customerRepository.saveAndFlush(tod);

        List<PhoneNumberDTO> response = this.restTemplate.getForObject(listCustomerPhoneNumberUrl(tod.getId()), List.class);
        assertThat(response).isEmpty();
    }

    @Test
    public void listCustomerPhoneNumber_notEmpty_ifHasData() {
        Customer rob = new Customer("Rob");
        this.customerRepository.saveAndFlush(rob);
        PhoneNumber phoneNumber1 = new PhoneNumber("+81423950361", rob.getId());
        PhoneNumber phoneNumber2 = new PhoneNumber("+81423950362", rob.getId());
        this.phoneNumberRepository.saveAndFlush(phoneNumber1);
        this.phoneNumberRepository.saveAndFlush(phoneNumber2);

        PhoneNumberDTO[] response = this.restTemplate.getForObject(listCustomerPhoneNumberUrl(rob.getId()), PhoneNumberDTO[].class);
        assertThat(response).hasSize(2);

        PhoneNumberDTO dto1 = response[0];
        assertThat(dto1.getId()).isNotNull();
        assertThat(dto1.getNumber()).isEqualTo(phoneNumber1.getNumber());
        assertThat(dto1.getCustomerId()).isEqualTo(rob.getId());
        assertThat(dto1.isActivated()).isTrue();

        PhoneNumberDTO dto2 = response[1];
        assertThat(dto2.getId()).isNotNull();
        assertThat(dto2.getNumber()).isEqualTo(phoneNumber2.getNumber());
        assertThat(dto2.isActivated()).isTrue();
        assertThat(dto2.getCustomerId()).isEqualTo(rob.getId());
    }

    private String listCustomerPhoneNumberUrl(Long customerId) {
        String path = "/api/v1/customers/" + customerId + "/phone-numbers";
        return url(path);
    }

    private String url(String path) {
        return "http://localhost:" + this.port + (path.startsWith("/") ? path : "/" + path);
    }

}
