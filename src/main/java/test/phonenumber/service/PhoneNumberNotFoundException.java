package test.phonenumber.service;

public class PhoneNumberNotFoundException extends RuntimeException {
    public PhoneNumberNotFoundException(Long phoneNumberId) {
        super("Phone number not found by Id: " + phoneNumberId);
    }
}
