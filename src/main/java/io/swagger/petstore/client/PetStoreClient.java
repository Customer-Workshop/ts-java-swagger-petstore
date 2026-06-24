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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Reusable REST API client for interacting with the Petstore API.
 * Provides typed methods for all petstore operations with built-in
 * authentication, error handling, and response parsing.
 *
 * Usage:
 * <pre>
 *   PetStoreClient client = new PetStoreClient("http://localhost:8080/api/v3", "special-key");
 *   ApiResponse&lt;Pet&gt; response = client.getPetById(1L);
 *   if (response.isSuccess()) {
 *       Pet pet = response.getBody();
 *   }
 * </pre>
 */
public class PetStoreClient {

    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private int connectTimeout = 5000;
    private int readTimeout = 10000;

    public PetStoreClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public PetStoreClient(ApiClientConfig config) {
        this(config.getBaseUrl(), config.getApiKey());
        this.connectTimeout = config.getConnectTimeout();
        this.readTimeout = config.getReadTimeout();
    }

    // --- Pet Operations ---

    public <T> ApiResponse<T> getPetById(Long petId, Class<T> responseType) throws IOException {
        return doGet("/pet/" + petId, responseType);
    }

    public ApiResponse<String> findPetsByStatus(String status) throws IOException {
        return doGet("/pet/findByStatus?status=" + encode(status), String.class);
    }

    public ApiResponse<String> findPetsByTags(List<String> tags) throws IOException {
        StringBuilder sb = new StringBuilder("/pet/findByTags?");
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) sb.append("&");
            sb.append("tags=").append(encode(tags.get(i)));
        }
        return doGet(sb.toString(), String.class);
    }

    public ApiResponse<String> addPet(Object pet) throws IOException {
        return doPost("/pet", pet);
    }

    public ApiResponse<String> updatePet(Object pet) throws IOException {
        return doPut("/pet", pet);
    }

    public ApiResponse<String> deletePet(Long petId) throws IOException {
        return doDelete("/pet/" + petId);
    }

    // --- Store Operations ---

    public ApiResponse<String> getInventory() throws IOException {
        return doGet("/store/inventory", String.class);
    }

    public ApiResponse<String> placeOrder(Object order) throws IOException {
        return doPost("/store/order", order);
    }

    public <T> ApiResponse<T> getOrderById(Long orderId, Class<T> responseType) throws IOException {
        return doGet("/store/order/" + orderId, responseType);
    }

    public ApiResponse<String> deleteOrder(Long orderId) throws IOException {
        return doDelete("/store/order/" + orderId);
    }

    // --- User Operations ---

    public ApiResponse<String> createUser(Object user) throws IOException {
        return doPost("/user", user);
    }

    public ApiResponse<String> createUsersWithList(List<?> users) throws IOException {
        return doPost("/user/createWithList", users);
    }

    public <T> ApiResponse<T> getUserByName(String username, Class<T> responseType) throws IOException {
        return doGet("/user/" + encode(username), responseType);
    }

    public ApiResponse<String> updateUser(String username, Object user) throws IOException {
        return doPut("/user/" + encode(username), user);
    }

    public ApiResponse<String> deleteUser(String username) throws IOException {
        return doDelete("/user/" + encode(username));
    }

    public ApiResponse<String> loginUser(String username, String password) throws IOException {
        return doGet("/user/login?username=" + encode(username) + "&password=" + encode(password), String.class);
    }

    public ApiResponse<String> logoutUser() throws IOException {
        return doGet("/user/logout", String.class);
    }

    // --- HTTP Methods ---

    private <T> ApiResponse<T> doGet(String path, Class<T> responseType) throws IOException {
        HttpURLConnection conn = createConnection(path, "GET");
        return executeRequest(conn, responseType);
    }

    private ApiResponse<String> doPost(String path, Object body) throws IOException {
        HttpURLConnection conn = createConnection(path, "POST");
        writeBody(conn, body);
        return executeRequest(conn, String.class);
    }

    private ApiResponse<String> doPut(String path, Object body) throws IOException {
        HttpURLConnection conn = createConnection(path, "PUT");
        writeBody(conn, body);
        return executeRequest(conn, String.class);
    }

    private ApiResponse<String> doDelete(String path) throws IOException {
        HttpURLConnection conn = createConnection(path, "DELETE");
        return executeRequest(conn, String.class);
    }

    private HttpURLConnection createConnection(String path, String method) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        if (apiKey != null && !apiKey.isEmpty()) {
            conn.setRequestProperty("api_key", apiKey);
        }

        return conn;
    }

    private void writeBody(HttpURLConnection conn, Object body) throws IOException {
        conn.setDoOutput(true);
        String json = objectMapper.writeValueAsString(body);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ApiResponse<T> executeRequest(HttpURLConnection conn, Class<T> responseType) throws IOException {
        int statusCode = conn.getResponseCode();
        String responseBody = readResponse(conn);

        ApiResponse<T> response = new ApiResponse<>();
        response.setStatusCode(statusCode);
        response.setRawBody(responseBody);

        if (statusCode >= 200 && statusCode < 300 && responseBody != null && !responseBody.isEmpty()) {
            if (responseType == String.class) {
                response.setBody((T) responseBody);
            } else {
                T parsed = objectMapper.readValue(responseBody, responseType);
                response.setBody(parsed);
            }
        }

        return response;
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            if (conn.getErrorStream() != null) {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            } else {
                return null;
            }
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }

    // --- Configuration ---

    public void setConnectTimeout(int millis) {
        this.connectTimeout = millis;
    }

    public void setReadTimeout(int millis) {
        this.readTimeout = millis;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
