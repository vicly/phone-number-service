package test.phonenumber.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.phonenumber.domain.Customer;
import test.phonenumber.domain.CustomerRepository;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.domain.PhoneNumberRepository;
import test.phonenumber.dto.ActivatePhoneNumberRequest;
import test.phonenumber.dto.ListPhoneNumberResponse;
import test.phonenumber.dto.PhoneNumberDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class PhoneNumberControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getAllPhoneNumbers() {
        PhoneNumber phoneNumber = new PhoneNumber("+71423950361");
        this.phoneNumberRepository.saveAndFlush(phoneNumber);

        ListPhoneNumberResponse response = this.restTemplate.getForObject(url("/api/v1/phone-numbers"), ListPhoneNumberResponse.class);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isGreaterThan(0);

        List<PhoneNumberDTO> data = response.getData();
        assertThat(data).isNotEmpty();

        PhoneNumberDTO dto = data.get(data.size() - 1);
        assertThat(dto.getId()).isEqualTo(phoneNumber.getId());
        assertThat(dto.getNumber()).isEqualTo(phoneNumber.getNumber());
        assertThat(dto.getCustomerId()).isNull();
        assertThat(dto.isActivated()).isFalse();
    }


    @Test
    public void getAllPhoneNumbers_pagination() {
        // LoadData.ts has init 8 numbers
        List<PhoneNumber> allData = this.phoneNumberRepository.findAll(Sort.by(Sort.Order.asc("id")));

        ListPhoneNumberResponse response;
        List<PhoneNumberDTO> data;
        // 1st page
        response = this.restTemplate.getForObject(url("/api/v1/phone-numbers?page=0&size=2"), ListPhoneNumberResponse.class);
        data = response.getData();
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(data).hasSize(2);
        assertPhoneNumberEqual(data.get(0), allData.get(0));
        assertPhoneNumberEqual(data.get(1), allData.get(1));
        // 2nd page
        response = this.restTemplate.getForObject(url("/api/v1/phone-numbers?page=1&size=2"), ListPhoneNumberResponse.class);
        data = response.getData();
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getSize()).isEqualTo(2);
        assertThat(data).hasSize(2);
        assertPhoneNumberEqual(data.get(0), allData.get(2));
        assertPhoneNumberEqual(data.get(1), allData.get(3));
    }

    private void assertPhoneNumberEqual(PhoneNumberDTO actual, PhoneNumber expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getNumber()).isEqualTo(expected.getNumber());
        assertThat(actual.getCustomerId()).isEqualTo(expected.getCustomerId());
        assertThat(actual.isActivated()).isEqualTo(expected.isActivated());
    }

    @Test
    public void activatePhoneNumber() {
        Customer vic = new Customer("vic");
        this.customerRepository.saveAndFlush(vic);

        PhoneNumber phoneNumber = new PhoneNumber("+91423950361");
        this.phoneNumberRepository.saveAndFlush(phoneNumber);

        PhoneNumberDTO actual = this.restTemplate.postForObject(
                activatePhoneNumberPath(phoneNumber.getId()),
                new ActivatePhoneNumberRequest(vic.getId()),
                PhoneNumberDTO.class);

        assertThat(actual.getId()).isEqualTo(phoneNumber.getId());
        assertThat(actual.getNumber()).isEqualTo(phoneNumber.getNumber());
        assertThat(actual.getCustomerId()).isEqualTo(vic.getId());
        assertThat(actual.isActivated()).isTrue();
    }

    @Test
    public void activatePhoneNumber_400_ifCustomerNotExist() {
        Long nonExistCustomerId = 9999999L;
        PhoneNumber phoneNumber = new PhoneNumber("+91853950361");
        this.phoneNumberRepository.saveAndFlush(phoneNumber);

        ResponseEntity response = this.restTemplate.postForEntity(
                activatePhoneNumberPath(nonExistCustomerId),
                new ActivatePhoneNumberRequest(nonExistCustomerId),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void activatePhoneNumber_404_ifAlreadyActivated() {
        Long nonExistPhoneNumberId = 9999999L;
        Customer vic = new Customer("vic");
        this.customerRepository.saveAndFlush(vic);

        ResponseEntity response = this.restTemplate.postForEntity(
                activatePhoneNumberPath(nonExistPhoneNumberId),
                new ActivatePhoneNumberRequest(vic.getId()),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void activatePhoneNumber_409_ifAlreadyActivated() {
        Customer currentOwner = new Customer("vic");
        Customer someoneElse = new Customer("tony");
        this.customerRepository.saveAllAndFlush(List.of(currentOwner, someoneElse));

        PhoneNumber phoneNumber = new PhoneNumber("+91453950361", currentOwner.getId());
        this.phoneNumberRepository.saveAndFlush(phoneNumber);

        ResponseEntity response = this.restTemplate.postForEntity(
                activatePhoneNumberPath(phoneNumber.getId()),
                new ActivatePhoneNumberRequest(someoneElse.getId()),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private String activatePhoneNumberPath(Long phoneNumberId) {
        String path = "/api/v1/phone-numbers/" + phoneNumberId + "/activation";
        return url(path);
    }

    private String url(String path) {
        return "http://localhost:" + this.port + (path.startsWith("/") ? path : "/" + path);
    }

}
