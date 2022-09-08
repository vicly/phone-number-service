package test.phonenumber.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListPhoneNumberResponse {
    private Integer page;
    private Integer size;
    private List<PhoneNumberDTO> data;
}