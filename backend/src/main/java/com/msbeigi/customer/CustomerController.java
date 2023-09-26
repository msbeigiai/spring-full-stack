package com.msbeigi.customer;

import com.msbeigi.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping
    public List<CustomerDTO> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public CustomerDTO getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{id}")
    public void deleteCustomerById(@PathVariable("id") Integer id) {
        customerService.deleteCustomerById(id);
    }

    @PutMapping("{id}")
    public void updateCustomerById(@PathVariable("id") Integer id,
                                   @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomerById(id, customerUpdateRequest);
    }

    @PostMapping(
            value = "{customerId}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public void uploadCustomerProfileImage(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file") MultipartFile file
    ) {
        customerService.uploadCustomerProfileImage(customerId, file);
    }

    @GetMapping(
            value = "{customerId}/profile-image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getCustomerProfileImage(@PathVariable("customerId") Integer customerId) {
        return customerService.getCustomerProfileImage(customerId);
    }
}
















