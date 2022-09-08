package test.phonenumber.dto;

import lombok.Data;

@Data
public class PhoneNumberDTO {
    private Long id;
    private String number;
    private Long customerId;
    private boolean activated;
}
