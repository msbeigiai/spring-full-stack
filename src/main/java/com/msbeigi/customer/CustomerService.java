package com.msbeigi.customer;

import com.msbeigi.exception.DuplicateResourceException;
import com.msbeigi.exception.RequestValidationException;
import com.msbeigi.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Customer with id [%s] not found!".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email exist
        if (customerDao.existPersonWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        customerRegistrationRequest.age()
                )
        );
    }

    public void deleteCustomerById(Integer id) {
        if (!customerDao.existCustomerById(id)) {
            throw new ResourceNotFound("customer with id [%s]".formatted(id));
        }
        customerDao.deleteCustomerById(id);
    }

    public void updateCustomerById(Integer id, CustomerUpdateRequest customerUpdateRequest) {
        boolean status = false;
        if (!customerDao.existCustomerById(id)) {
            throw new ResourceNotFound("customer with id [%s]".formatted(id));
        }

        Customer customer = customerDao.selectCustomerById(id).get();

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            customer.setEmail(customerUpdateRequest.email());
            status = true;
        }
        if (customerUpdateRequest.age() != null) {
            customer.setAge(customerUpdateRequest.age());
            status = true;
        }
        if (customerUpdateRequest.name() != null) {
            customer.setName(customerUpdateRequest.name());
            status = true;
        }

        if (!status) {
            throw new RequestValidationException("no data changed found!");
        }

        customerDao.updateCustomer(customer);
    }
}
