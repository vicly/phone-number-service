package test.phonenumber.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.dto.PhoneNumberDTO;
import test.phonenumber.service.CustomerNotFoundException;
import test.phonenumber.service.PhoneNumberService;

import java.awt.print.Book;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/v1/customers")
public class CustomerController {

    private PhoneNumberService phoneNumberService;

    @Autowired
    public CustomerController(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = Objects.requireNonNull(phoneNumberService);
    }

    @Operation(summary = "Find all phone number for an existing customer")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Phone number array. Empty if no data",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PhoneNumberDTO[].class)) }),
            @ApiResponse(responseCode = "404", description = "Customer not exist", content = @Content)
    })
    @GetMapping("/{customerId}/phone-numbers")
    public List<PhoneNumberDTO> listCustomerPhoneNumber(@PathVariable long customerId) {
        try {
            List<PhoneNumber> phoneNumbers = this.phoneNumberService.findAllPhoneNumberByCustomerId(customerId);
            return Mappers.buildPhoneNumberDTOList(phoneNumbers);
        } catch (CustomerNotFoundException nfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not exist");
        }
    }

}
