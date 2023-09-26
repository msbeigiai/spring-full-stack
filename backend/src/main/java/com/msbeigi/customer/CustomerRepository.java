package com.msbeigi.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsCustomerByEmail(String email);
    boolean existsCustomerById(Integer customerId);
    Optional<Customer> findCustomerByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query(
            value = "UPDATE customer SET profile_image_id = ?1 WHERE id = ?2",
            nativeQuery = true
    )
    void updateProfileImageId(String profileImageId, Integer customerId);
}
