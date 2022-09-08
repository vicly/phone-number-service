package test.phonenumber.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import test.phonenumber.domain.PhoneNumber;
import test.phonenumber.dto.ActivatePhoneNumberRequest;
import test.phonenumber.dto.ListPhoneNumberResponse;
import test.phonenumber.dto.PhoneNumberDTO;
import test.phonenumber.service.CustomerNotFoundException;
import test.phonenumber.service.PhoneNumberAlreadyActivatedException;
import test.phonenumber.service.PhoneNumberNotFoundException;
import test.phonenumber.service.PhoneNumberService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/phone-numbers")
public class PhoneNumberController {

    private PhoneNumberService phoneNumberService;

    @Autowired
    public PhoneNumberController(PhoneNumberService phoneNumberService) {
        this.phoneNumberService = phoneNumberService;
    }

    @Operation(summary = "Find all phone numbers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Phone numbers of current page",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ListPhoneNumberResponse.class)) }
            )
    })
    @GetMapping()
    public ListPhoneNumberResponse listPhoneNumber(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        Page<PhoneNumber> resultPage = this.phoneNumberService.findPhoneNumber(page, size);
        return Mappers.buildListPhoneNumberResponse(resultPage);
    }

    @Operation(summary = "Activate a phone number")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful, return the activated phone number",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PhoneNumberDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Customer not exist", content = @Content),
            @ApiResponse(responseCode = "404", description = "Phone number not exist", content = @Content),
            @ApiResponse(responseCode = "409", description = "Phone number already activated", content = @Content),
    })
    @PostMapping("/{phoneNumberId}/activation")
    public PhoneNumberDTO activatePhoneNumber(@PathVariable Long phoneNumberId, @Valid @RequestBody ActivatePhoneNumberRequest activatePhoneNumberRequest) {
        Long customerId = activatePhoneNumberRequest.getCustomerId();
        try {
            PhoneNumber phoneNumber = this.phoneNumberService.activatePhoneNumber(phoneNumberId, customerId);
            return Mappers.buildPhoneNumberDTO(phoneNumber);
        } catch (CustomerNotFoundException cnfe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customerId, customer not exist");
        } catch (PhoneNumberNotFoundException pnfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Non exist phone number");
        } catch (PhoneNumberAlreadyActivatedException ae) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already activated");
        }
    }
}
