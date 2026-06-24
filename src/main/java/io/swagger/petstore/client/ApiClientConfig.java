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

package io.swagger.petstore.client;

/**
 * Configuration class for the PetStore API client.
 * Supports builder pattern for fluent configuration.
 *
 * Usage:
 * <pre>
 *   ApiClientConfig config = ApiClientConfig.builder()
 *       .baseUrl("http://localhost:8080/api/v3")
 *       .apiKey("special-key")
 *       .connectTimeout(5000)
 *       .readTimeout(10000)
 *       .build();
 *   PetStoreClient client = new PetStoreClient(config);
 * </pre>
 */
public class ApiClientConfig {

    private String baseUrl;
    private String apiKey;
    private int connectTimeout;
    private int readTimeout;

    private ApiClientConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.apiKey = builder.apiKey;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public static class Builder {
        private String baseUrl = "http://localhost:8080/api/v3";
        private String apiKey = "";
        private int connectTimeout = 5000;
        private int readTimeout = 10000;

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder connectTimeout(int millis) {
            this.connectTimeout = millis;
            return this;
        }

        public Builder readTimeout(int millis) {
            this.readTimeout = millis;
            return this;
        }

        public ApiClientConfig build() {
            if (baseUrl == null || baseUrl.isEmpty()) {
                throw new IllegalArgumentException("baseUrl is required");
            }
            return new ApiClientConfig(this);
        }
    }
}
