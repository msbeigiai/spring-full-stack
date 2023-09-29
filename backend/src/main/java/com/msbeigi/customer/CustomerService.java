package com.msbeigi.customer;

import com.msbeigi.exception.DuplicateResourceException;
import com.msbeigi.exception.RequestValidationException;
import com.msbeigi.exception.ResourceNotFoundException;
import com.msbeigi.s3.S3Buckets;
import com.msbeigi.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final S3Buckets buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao,
                           CustomerDTOMapper customerDTOMapper,
                           PasswordEncoder passwordEncoder,
                           S3Service s3Service, S3Buckets buckets) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
        this.buckets = buckets;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDao.selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Integer id) {
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] not found!".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email exist
        if (customerDao.existCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "email already taken"
            );
        }
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer id) {
        checkIfCustomerExistsOrThrow(id);
        customerDao.deleteCustomerById(id);
    }

    private void checkIfCustomerExistsOrThrow(Integer id) {
        if (!customerDao.existCustomerById(id)) {
            throw new ResourceNotFoundException("customer with id [%s] not found.".formatted(id));
        }
    }

    public void updateCustomerById(Integer id, CustomerUpdateRequest customerUpdateRequest) {

        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] not found!".formatted(id)));

        boolean status = false;

        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            status = true;
        }

        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            status = true;
        }

        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            if (customerDao.existCustomerWithEmail(customerUpdateRequest.email())) {
                throw new DuplicateResourceException("email already was taken.");
            }
            customer.setEmail(customerUpdateRequest.email());
            status = true;
        }

        if (!status) {
            throw new RequestValidationException("no data changes found!");
        }

        customerDao.updateCustomer(customer);
    }


    public void uploadCustomerProfileImage(Integer customerId, MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        String profileImageId = UUID.randomUUID().toString();
        try {
            s3Service.putObject(
                    buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }
        customerDao.updateCustomerProfileImageId(profileImageId, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        var customer = customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found!".formatted(customerId)
                ));

        if (StringUtils.isBlank(customer.profileImageId())) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] profile image not found!".formatted(customerId));
        }

        return s3Service.getObject(
                buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
    }
}
