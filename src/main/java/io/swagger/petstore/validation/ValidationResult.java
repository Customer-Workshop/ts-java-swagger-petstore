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

import java.util.Collections;
import java.util.List;

/**
 * Result of a validation operation. Contains a list of validation errors.
 * If the list is empty, validation passed.
 */
public class ValidationResult {

    private final List<String> errors;

    public ValidationResult(List<String> errors) {
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getErrorMessage() {
        return String.join("; ", errors);
    }

    @Override
    public String toString() {
        if (isValid()) {
            return "ValidationResult{valid=true}";
        }
        return "ValidationResult{valid=false, errors=" + errors + "}";
    }
}
