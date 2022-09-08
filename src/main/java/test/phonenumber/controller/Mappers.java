package test.phonenumber.controller;

import org.springframework.data.domain.Page;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.dto.ListPhoneNumberResponse;
import test.phonenumber.dto.PhoneNumberDTO;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Map between DTO and model.
 */
public class Mappers {
    public static PhoneNumberDTO buildPhoneNumberDTO(PhoneNumber phoneNumber) {
        PhoneNumberDTO dto = new PhoneNumberDTO();
        dto.setId(phoneNumber.getId());
        dto.setNumber(phoneNumber.getNumber());
        dto.setCustomerId(phoneNumber.getCustomerId());
        dto.setActivated(phoneNumber.isActivated());
        return dto;
    };

    public static List<PhoneNumberDTO> buildPhoneNumberDTOList(List<PhoneNumber> phoneNumbers) {
        return phoneNumbers.stream().map(Mappers::buildPhoneNumberDTO).collect(Collectors.toList());
    }

    public static ListPhoneNumberResponse buildListPhoneNumberResponse(Page<PhoneNumber> resultPage) {
        Function<PhoneNumber, PhoneNumberDTO> toDto = phoneNumber -> buildPhoneNumberDTO(phoneNumber);
        ListPhoneNumberResponse response = new ListPhoneNumberResponse();
        response.setPage(resultPage.getNumber());
        response.setSize(resultPage.getSize());
        response.setData(buildPhoneNumberDTOList(resultPage.getContent()));
        return response;
    }
}
