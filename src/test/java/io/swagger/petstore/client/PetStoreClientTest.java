package io.swagger.petstore.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class PetStoreClientTest {

    @Test
    public void testClientCreation() {
        PetStoreClient client = new PetStoreClient("http://localhost:8080/api/v3", "test-key");
        assertNotNull(client);
        assertEquals("http://localhost:8080/api/v3", client.getBaseUrl());
    }

    @Test
    public void testClientCreation_trailingSlash() {
        PetStoreClient client = new PetStoreClient("http://localhost:8080/api/v3/", "test-key");
        assertEquals("http://localhost:8080/api/v3", client.getBaseUrl());
    }

    @Test
    public void testClientConfig_builder() {
        ApiClientConfig config = ApiClientConfig.builder()
                .baseUrl("http://localhost:8080/api/v3")
                .apiKey("my-key")
                .connectTimeout(3000)
                .readTimeout(5000)
                .build();

        assertEquals("http://localhost:8080/api/v3", config.getBaseUrl());
        assertEquals("my-key", config.getApiKey());
        assertEquals(3000, config.getConnectTimeout());
        assertEquals(5000, config.getReadTimeout());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientConfig_nullBaseUrl() {
        ApiClientConfig.builder()
                .baseUrl(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClientConfig_emptyBaseUrl() {
        ApiClientConfig.builder()
                .baseUrl("")
                .build();
    }

    @Test
    public void testClientCreation_withConfig() {
        ApiClientConfig config = ApiClientConfig.builder()
                .baseUrl("http://petstore.example.com/api/v3")
                .apiKey("config-key")
                .build();

        PetStoreClient client = new PetStoreClient(config);
        assertNotNull(client);
        assertEquals("http://petstore.example.com/api/v3", client.getBaseUrl());
    }

    @Test
    public void testApiResponse_success() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatusCode(200);
        response.setBody("OK");
        response.setRawBody("OK");

        assertTrue(response.isSuccess());
        assertFalse(response.isClientError());
        assertFalse(response.isServerError());
    }

    @Test
    public void testApiResponse_clientError() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatusCode(404);

        assertFalse(response.isSuccess());
        assertTrue(response.isClientError());
        assertFalse(response.isServerError());
    }

    @Test
    public void testApiResponse_serverError() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatusCode(500);

        assertFalse(response.isSuccess());
        assertFalse(response.isClientError());
        assertTrue(response.isServerError());
    }
}
