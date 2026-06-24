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

package io.swagger.petstore.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for valid API keys.
 * In production, replace with a database or external auth service lookup.
 */
public class ApiKeyStore {

    private static final ApiKeyStore INSTANCE = new ApiKeyStore();
    private final Set<String> validKeys = ConcurrentHashMap.newKeySet();

    private ApiKeyStore() {
        validKeys.add("special-key");
        validKeys.add("test-api-key");
    }

    public static ApiKeyStore getInstance() {
        return INSTANCE;
    }

    public boolean isValidKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }
        return validKeys.contains(apiKey);
    }

    public void addKey(String apiKey) {
        validKeys.add(apiKey);
    }

    public void removeKey(String apiKey) {
        validKeys.remove(apiKey);
    }

    public void clear() {
        validKeys.clear();
    }
}
