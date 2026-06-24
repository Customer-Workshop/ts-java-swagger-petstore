package io.swagger.petstore.security;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApiKeyStoreTest {

    private ApiKeyStore apiKeyStore;

    @Before
    public void setUp() {
        apiKeyStore = ApiKeyStore.getInstance();
    }

    @Test
    public void testDefaultKeys() {
        assertTrue(apiKeyStore.isValidKey("special-key"));
        assertTrue(apiKeyStore.isValidKey("test-api-key"));
    }

    @Test
    public void testInvalidKey() {
        assertFalse(apiKeyStore.isValidKey("invalid-key"));
        assertFalse(apiKeyStore.isValidKey(""));
        assertFalse(apiKeyStore.isValidKey(null));
    }

    @Test
    public void testAddKey() {
        apiKeyStore.addKey("new-key");
        assertTrue(apiKeyStore.isValidKey("new-key"));
        apiKeyStore.removeKey("new-key");
    }

    @Test
    public void testRemoveKey() {
        apiKeyStore.addKey("temp-key");
        assertTrue(apiKeyStore.isValidKey("temp-key"));
        apiKeyStore.removeKey("temp-key");
        assertFalse(apiKeyStore.isValidKey("temp-key"));
    }

    @Test
    public void testSingleton() {
        ApiKeyStore instance1 = ApiKeyStore.getInstance();
        ApiKeyStore instance2 = ApiKeyStore.getInstance();
        assertSame(instance1, instance2);
    }
}
