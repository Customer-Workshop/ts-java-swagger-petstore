/**
 * Copyright 2018 SmartBear Software
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.swagger.petstore.validation;

import io.swagger.petstore.model.Order;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reusable request validation utility for all API resources.
 * Validates model constraints defined in the OpenAPI specification.
 */
public class RequestValidator {

    private static final Set<String> VALID_PET_STATUSES = new HashSet<>(
            Arrays.asList("available", "pending", "sold"));
    private static final Set<String> VALID_ORDER_STATUSES = new HashSet<>(
            Arrays.asList("placed", "approved", "delivered"));

    private RequestValidator() {
    }

    public static ValidationResult validatePet(Pet pet) {
        List<String> errors = new ArrayList<>();

        if (pet == null) {
            errors.add("Pet object is required");
            return new ValidationResult(errors);
        }

        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            errors.add("Pet name is required");
        }

        if (pet.getPhotoUrls() == null || pet.getPhotoUrls().isEmpty()) {
            errors.add("At least one photo URL is required");
        }

        if (pet.getStatus() != null && !VALID_PET_STATUSES.contains(pet.getStatus())) {
            errors.add("Invalid pet status. Must be one of: available, pending, sold");
        }

        if (pet.getName() != null && pet.getName().length() > 100) {
            errors.add("Pet name must not exceed 100 characters");
        }

        return new ValidationResult(errors);
    }

    public static ValidationResult validateOrder(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            errors.add("Order object is required");
            return new ValidationResult(errors);
        }

        if (order.getPetId() <= 0) {
            errors.add("Valid petId is required");
        }

        if (order.getQuantity() <= 0) {
            errors.add("Quantity must be greater than 0");
        }

        if (order.getStatus() != null && !VALID_ORDER_STATUSES.contains(order.getStatus())) {
            errors.add("Invalid order status. Must be one of: placed, approved, delivered");
        }

        return new ValidationResult(errors);
    }

    public static ValidationResult validateUser(User user) {
        List<String> errors = new ArrayList<>();

        if (user == null) {
            errors.add("User object is required");
            return new ValidationResult(errors);
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Username is required");
        }

        if (user.getUsername() != null && user.getUsername().length() > 50) {
            errors.add("Username must not exceed 50 characters");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                errors.add("Invalid email format");
            }
        }

        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            if (!user.getPhone().matches("^[0-9\\-+() ]+$")) {
                errors.add("Invalid phone number format");
            }
        }

        return new ValidationResult(errors);
    }

    public static ValidationResult validatePetStatus(String status) {
        List<String> errors = new ArrayList<>();
        if (status == null || status.trim().isEmpty()) {
            errors.add("Status parameter is required");
        } else {
            String[] statuses = status.split(",");
            for (String s : statuses) {
                if (!VALID_PET_STATUSES.contains(s.trim())) {
                    errors.add("Invalid status value: " + s.trim() + ". Must be one of: available, pending, sold");
                }
            }
        }
        return new ValidationResult(errors);
    }

    public static ValidationResult validateId(Long id, String fieldName) {
        List<String> errors = new ArrayList<>();
        if (id == null) {
            errors.add(fieldName + " is required");
        } else if (id <= 0) {
            errors.add(fieldName + " must be a positive number");
        }
        return new ValidationResult(errors);
    }
}
