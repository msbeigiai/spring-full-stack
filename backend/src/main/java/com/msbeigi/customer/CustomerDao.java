package com.msbeigi.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existCustomerWithEmail(String email);
    void deleteCustomerById(Integer id);
    boolean existCustomerById(Integer customerId);
    void updateCustomer(Customer customer);
    Optional<Customer> selectUserByEmail(String email);
}
