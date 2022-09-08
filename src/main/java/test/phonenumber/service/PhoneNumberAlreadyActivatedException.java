package test.phonenumber.service;

public class PhoneNumberAlreadyActivatedException extends RuntimeException {
    public PhoneNumberAlreadyActivatedException(Long phoneNumberId) {
        super("Phone number has already been activated: " + phoneNumberId);
    }
}
