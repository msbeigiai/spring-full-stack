package com.msbeigi.customer;

import com.msbeigi.exception.DuplicateResourceException;
import com.msbeigi.exception.RequestValidationException;
import com.msbeigi.exception.ResourceNotFoundException;
import com.msbeigi.s3.S3Buckets;
import com.msbeigi.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets s3Buckets;

    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(
                customerDao,
                customerDTOMapper,
                passwordEncoder,
                s3Service,
                s3Buckets
        );
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // Given
        int id = 10;
        var customer =  new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomerById(10);

        // Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmpltyOptional() {
        // Given
        int id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Customer actual = underTest.getCustomerById(id);

        // Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found!".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existCustomerWithEmail(email)).thenReturn(false);

        var request = new CustomerRegistrationRequest(
                "Alex", email, "password", 22, Gender.MALE
        );

        String passwordHash = "$kkd;spoaq;";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenEmailExistWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDao.existCustomerWithEmail(email)).thenReturn(true);

        var request = new CustomerRegistrationRequest(
                "Alex", email, "password", 22, Gender.MALE
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        verify(customerDao, never()).insertCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 10;

        when(customerDao.existCustomerById(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExist() {
        // Given
        int id = 10;

        when(customerDao.existCustomerById(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found.".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);
    }


    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "updated@gmail.com";

        var updateRequest =
                new CustomerUpdateRequest("updated", newEmail, 36);

        when(customerDao.existCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        var updateRequest =
                new CustomerUpdateRequest("updated", null, null);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        var newEmail = "updated@gmail.com";

        var updateRequest =
                new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        int newAge = 44;

        var updateRequest =
                new CustomerUpdateRequest(null, null, newAge);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(newAge);
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailAlreadyTaken() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        var newEmail = "updated@gmail.com";

        var updateRequest =
                new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existCustomerWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already was taken.");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanged() {
        // Given
        int id = 10;
        var customer = new Customer(
                id,
                "Alex",
                "alex@gmail.com",
                "password", 22,
                Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        var updateRequest =
                new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        // When
        assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found!");

        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void canUploadProfileImage() {
        // Given
        int customerId = 10;

        when(customerDao.existCustomerById(customerId)).thenReturn(true);

        byte[] bytes = "Hello World!".getBytes();

        MultipartFile multipartFile = new MockMultipartFile("file", bytes);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        underTest.uploadCustomerProfileImage(customerId, multipartFile);

        // Then
        ArgumentCaptor<String> profileImageIdArgumentCaptor =
                ArgumentCaptor.forClass(String.class);
        verify(customerDao).updateCustomerProfileImageId(
                profileImageIdArgumentCaptor.capture(),
                eq(customerId)
        );

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()),
                bytes);
    }

    @Test
    void canNotUploadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.existCustomerById(customerId)).thenReturn(false);

        // when
        assertThatThrownBy(() ->
                underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class))
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [" + customerId + "] not found.");

        // then
        verify(customerDao).existCustomerById(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(s3Buckets);
        verifyNoInteractions(s3Service);

    }

    @Test
    void canNotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        // Given
        int customerId = 10;

        when(customerDao.existCustomerById(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        // When
        assertThatThrownBy(() ->
                underTest.uploadCustomerProfileImage(customerId, multipartFile)
        ).isInstanceOf(RuntimeException.class)
                .hasMessage("failed to upload profile image")
                .hasRootCauseInstanceOf(IOException.class);
        // Then
        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());
    }

    @Test
    void canDownloadProfileImage() {
        // Given
        int customerId = 10;
        String profileImageId = "22222";
        var customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                22,
                Gender.MALE,
                "password",
                profileImageId);
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        String bucket = "customer-bucket";
        when(s3Buckets.getCustomer()).thenReturn(bucket);

        byte[] expectedImage = "image".getBytes();

        when(s3Service.getObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageId)
        ))
                .thenReturn(expectedImage);

        // When
        byte[] actualImage = underTest.getCustomerProfileImage(customerId);

        // Then
        assertThat(actualImage).isEqualTo(expectedImage);
    }

    @Test
    void canNotDownloadProfileImageWhenNoProfileImageId() {
        // Given
        int customerId = 10;
        var customer = new Customer(
                customerId,
                "Alex",
                "alex@gmail.com",
                "password",
                22,
                Gender.MALE
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        // When
        // then
        assertThatThrownBy(() ->
                underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found!".formatted(customerId));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }

    @Test
    void canNotDownloadProfileImageWhenCustomerDoesNotExists() {
        // Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        // When
        // then
        assertThatThrownBy(() ->
                underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found!".formatted(customerId));

        verifyNoInteractions(s3Service);
        verifyNoInteractions(s3Buckets);

    }
}