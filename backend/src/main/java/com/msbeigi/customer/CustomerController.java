package com.msbeigi.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable("id") Integer id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
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

}
