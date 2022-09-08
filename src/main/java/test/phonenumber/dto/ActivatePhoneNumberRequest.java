package test.phonenumber.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActivatePhoneNumberRequest {
    @Min(1)
    private Long customerId;
}
