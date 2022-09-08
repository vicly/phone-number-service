package test.phonenumber.service;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found by Id: " + customerId);
    }
}
