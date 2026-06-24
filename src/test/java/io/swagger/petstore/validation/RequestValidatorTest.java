package io.swagger.petstore.validation;

import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.User;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class RequestValidatorTest {

    // --- Pet Validation Tests ---

    @Test
    public void testValidatePet_valid() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));
        pet.setStatus("available");

        ValidationResult result = RequestValidator.validatePet(pet);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidatePet_null() {
        ValidationResult result = RequestValidator.validatePet(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Pet object is required"));
    }

    @Test
    public void testValidatePet_missingName() {
        Pet pet = new Pet();
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));
        pet.setStatus("available");

        ValidationResult result = RequestValidator.validatePet(pet);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Pet name is required"));
    }

    @Test
    public void testValidatePet_emptyPhotoUrls() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setPhotoUrls(Collections.emptyList());
        pet.setStatus("available");

        ValidationResult result = RequestValidator.validatePet(pet);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("At least one photo URL is required"));
    }

    @Test
    public void testValidatePet_invalidStatus() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));
        pet.setStatus("unknown");

        ValidationResult result = RequestValidator.validatePet(pet);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().get(0).contains("Invalid pet status"));
    }

    @Test
    public void testValidatePet_nameTooLong() {
        Pet pet = new Pet();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            longName.append("a");
        }
        pet.setName(longName.toString());
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));

        ValidationResult result = RequestValidator.validatePet(pet);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Pet name must not exceed 100 characters"));
    }

    // --- Order Validation Tests ---

    @Test
    public void testValidateOrder_valid() {
        Order order = new Order();
        order.setPetId(1L);
        order.setQuantity(5);
        order.setStatus("placed");

        ValidationResult result = RequestValidator.validateOrder(order);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateOrder_null() {
        ValidationResult result = RequestValidator.validateOrder(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Order object is required"));
    }

    @Test
    public void testValidateOrder_invalidPetId() {
        Order order = new Order();
        order.setPetId(0L);
        order.setQuantity(5);
        order.setStatus("placed");

        ValidationResult result = RequestValidator.validateOrder(order);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Valid petId is required"));
    }

    @Test
    public void testValidateOrder_invalidQuantity() {
        Order order = new Order();
        order.setPetId(1L);
        order.setQuantity(0);
        order.setStatus("placed");

        ValidationResult result = RequestValidator.validateOrder(order);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Quantity must be greater than 0"));
    }

    @Test
    public void testValidateOrder_invalidStatus() {
        Order order = new Order();
        order.setPetId(1L);
        order.setQuantity(5);
        order.setStatus("invalid");

        ValidationResult result = RequestValidator.validateOrder(order);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().get(0).contains("Invalid order status"));
    }

    // --- User Validation Tests ---

    @Test
    public void testValidateUser_valid() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPhone("123-456-7890");

        ValidationResult result = RequestValidator.validateUser(user);
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateUser_null() {
        ValidationResult result = RequestValidator.validateUser(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("User object is required"));
    }

    @Test
    public void testValidateUser_missingUsername() {
        User user = new User();
        user.setEmail("john@example.com");

        ValidationResult result = RequestValidator.validateUser(user);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Username is required"));
    }

    @Test
    public void testValidateUser_invalidEmail() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("not-an-email");

        ValidationResult result = RequestValidator.validateUser(user);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Invalid email format"));
    }

    @Test
    public void testValidateUser_invalidPhone() {
        User user = new User();
        user.setUsername("john_doe");
        user.setPhone("abc-def-ghij");

        ValidationResult result = RequestValidator.validateUser(user);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Invalid phone number format"));
    }

    @Test
    public void testValidateUser_usernameTooLong() {
        User user = new User();
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longName.append("a");
        }
        user.setUsername(longName.toString());

        ValidationResult result = RequestValidator.validateUser(user);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().contains("Username must not exceed 50 characters"));
    }

    // --- Utility Validation Tests ---

    @Test
    public void testValidatePetStatus_valid() {
        ValidationResult result = RequestValidator.validatePetStatus("available");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidatePetStatus_multipleValid() {
        ValidationResult result = RequestValidator.validatePetStatus("available,pending,sold");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidatePetStatus_invalid() {
        ValidationResult result = RequestValidator.validatePetStatus("unknown");
        assertFalse(result.isValid());
    }

    @Test
    public void testValidatePetStatus_null() {
        ValidationResult result = RequestValidator.validatePetStatus(null);
        assertFalse(result.isValid());
    }

    @Test
    public void testValidateId_valid() {
        ValidationResult result = RequestValidator.validateId(1L, "petId");
        assertTrue(result.isValid());
    }

    @Test
    public void testValidateId_null() {
        ValidationResult result = RequestValidator.validateId(null, "petId");
        assertFalse(result.isValid());
    }

    @Test
    public void testValidateId_negative() {
        ValidationResult result = RequestValidator.validateId(-1L, "petId");
        assertFalse(result.isValid());
    }
}
