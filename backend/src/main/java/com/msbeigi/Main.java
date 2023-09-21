package com.msbeigi;

import com.github.javafaker.Faker;
import com.msbeigi.customer.Customer;
import com.msbeigi.customer.CustomerRepository;
import com.msbeigi.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder encoder) {
       return args -> {
           var faker = new Faker();
           Random random = new Random();

           String name = faker.name().firstName();
           String lastName = faker.name().lastName();
           int age = random.nextInt(19, 88);
           Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
           String emailAddress = name.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";

           var customer = new Customer(name + " " + lastName, emailAddress,
                   encoder.encode(UUID.randomUUID().toString()), age, gender);

             customerRepository.save(customer);
       };
    }

}
