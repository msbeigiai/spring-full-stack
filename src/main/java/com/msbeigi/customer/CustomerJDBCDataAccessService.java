package com.msbeigi.customer;

import com.msbeigi.exception.ResourceNotFound;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                select id, name, email, age from customer
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                select id, name, email, age from customer where id = ?;
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                insert into customer(name, email, age) values (?, ?, ?);
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existPersonWithEmail(String email) {
        var sql = """
                select email from customer where email = ?;
                """;
        String e = jdbcTemplate.queryForObject(sql, String.class, email);

        return e != null && e.equals(email);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        if (!existCustomerById(id))
            throw new ResourceNotFound("customer with id [%s] not found!".formatted(id));
        var sql = """
                delete from customer where id = ?;
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existCustomerById(Integer customerId) {
        var sql = """
                select count(id) from customer where id = ?;
                """;
        var count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = """
                update customer 
                set name = ?, email = ?, age = ?
                where id = ?;
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), customer.getId());
    }
}
