package com.msbeigi.customer;

import com.msbeigi.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomers();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                39
        );
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int id = -1;

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        // When
        underTest.insertCustomer(customer);

        Optional<Integer> id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst();

        // Then
        assertThat(id).isPresent().hasValue(1);
    }

    @Test
    void existPersonWithEmail() {
        // Given
        String email = FAKER.internet().emailAddress() + "_" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                39
        );

        underTest.insertCustomer(customer);

        // When
        var actual = underTest.existCustomerWithEmail(email);

        // Then
        assertThat(actual).isTrue();

    }

    @Test
    void existPersonWithEmailReturnsFalseWhenEmailDoesNotExist() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        // When
        boolean actual = underTest.existCustomerWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        // When
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers()
                .stream()
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(id);

        Optional<Customer> actual = underTest.selectAllCustomers()
                .stream()
                .findFirst();

        // Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void existCustomerById() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        // When
        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Then
        boolean actual = underTest.existCustomerById(id);

        assertThat(actual).isTrue();
    }

    @Test
    void existPersonWithIdWillReturnFalseWhenIdDoesNotPresent() {
        // Given
        int id = -1;

        // When
        boolean actual = underTest.existCustomerById(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerAge() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Customer updatedCustomer = new Customer(id, customer.getName(), customer.getEmail(), 30);

        underTest.updateCustomer(updatedCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId().equals(id));
                    assertThat(c.getName().equals(customer.getName()));
                    assertThat(c.getAge().equals(30));
                    assertThat(c.getEmail().equals(customer.getEmail()));
                });

    }

    @Test
    void updateCustomerName() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String newName = "foo";
        Customer updatedCustomer = new Customer(id, newName, customer.getEmail(), customer.getAge());

        underTest.updateCustomer(updatedCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId().equals(id));
                    assertThat(c.getName().equals(newName));
                    assertThat(c.getAge().equals(customer.getAge()));
                    assertThat(c.getEmail().equals(customer.getEmail()));
                });

    }

    @Test
    void updateCustomerEmail() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String newEmail = "foo@bar.com";
        Customer updatedCustomer = new Customer(id, customer.getName(), newEmail, customer.getAge());

        underTest.updateCustomer(updatedCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId().equals(id));
                    assertThat(c.getName().equals(customer.getName()));
                    assertThat(c.getAge().equals(customer.getAge()));
                    assertThat(c.getEmail().equals(newEmail));
                });

    }

    @Test
    void updateAllCustomerProperties() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        String newName = "foo";
        String newEmail = "foo@bar.com";
        int newAge = 100;

        Customer updatedCustomer = new Customer(id, newName, newEmail, newAge);

        underTest.updateCustomer(updatedCustomer);

        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValue(updatedCustomer);
    }

    @Test
    void willNotUpdateCustomerWhenNothingToUpdate() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().emailAddress() + "_" + UUID.randomUUID(),
                39
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(customer.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var updateCustomer = new Customer();
        updateCustomer.setId(id);

        underTest.updateCustomer(updateCustomer);

        // Then
        var actual = underTest.selectCustomerById(id);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId().equals(id));
                    assertThat(c.getName().equals(customer.getName()));
                    assertThat(c.getAge().equals(customer.getAge()));
                    assertThat(c.getEmail().equals(customer.getEmail()));
                });
    }
}