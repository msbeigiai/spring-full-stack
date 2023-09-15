package com.msbeigi.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer customerId);
}
