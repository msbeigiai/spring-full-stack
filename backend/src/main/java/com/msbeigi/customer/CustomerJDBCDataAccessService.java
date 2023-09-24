package com.msbeigi.customer;

import com.msbeigi.exception.ResourceNotFoundException;
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
                select id, name, email, password, age, gender 
                from customer
                limit 1000;
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                select id, name, email, password, age, gender from customer where id = ?;
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                insert into customer(name, email, password, age, gender) values (?, ?, ?, ?, ?);
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getPassword(),
                customer.getAge(), customer.getGender().name());
    }

    @Override
    public boolean existCustomerWithEmail(String email) {
        var sql = """
                select count(id) from customer where email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                delete from customer where id = ?;
                """;
        int result = jdbcTemplate.update(sql, id);
        System.out.println("deleted customer = " + result);
    }

    @Override
    public boolean existCustomerById(Integer customerId) {
        var sql = """
                select count(id) from customer where id = ?;
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customerId);
        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer update) {
        if (update.getName() != null) {
            String sql = "update customer set name = ? where id = ?";
            int result = jdbcTemplate.update(sql, update.getName(), update.getId());
            System.out.println("update customer name result = " + result);
        }
        if (update.getAge() != null) {
            String sql = "update customer set age = ? where id = ?";
            int result = jdbcTemplate.update(sql, update.getAge(), update.getId());
            System.out.println("update customer age result = " + result);
        }
        if (update.getEmail() != null) {
            String sql = "update customer set email = ? where id = ?";
            int result = jdbcTemplate.update(sql, update.getEmail(), update.getId());
            System.out.println("update customer email result = " + result);
        }
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        String sql = "select id, name, email, password, age, gender from customer where email = ?";
        return jdbcTemplate.query(sql, customerRowMapper, email)
                .stream()
                .findFirst();
    }
}
