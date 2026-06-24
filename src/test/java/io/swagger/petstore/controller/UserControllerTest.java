package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;
    private RequestContext mockRequest;

    @Before
    public void setUp() {
        userController = new UserController();
        mockRequest = mock(RequestContext.class);
        when(mockRequest.getHeaders()).thenReturn(new javax.ws.rs.core.MultivaluedHashMap<>());
        when(mockRequest.getAcceptableMediaTypes()).thenReturn(
                Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testCreateUser() {
        User user = createTestUser("testuser");
        ResponseContext response = userController.createUser(mockRequest, user);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void testCreateUser_null() {
        ResponseContext response = userController.createUser(mockRequest, (User) null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserByName_existing() {
        ResponseContext response = userController.getUserByName(mockRequest, "user1");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof User);
        assertEquals("user1", ((User) response.getEntity()).getUsername());
    }

    @Test
    public void testGetUserByName_nonExisting() {
        ResponseContext response = userController.getUserByName(mockRequest, "nonexistent");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserByName_null() {
        ResponseContext response = userController.getUserByName(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testLoginUser() {
        ResponseContext response = userController.loginUser(mockRequest, "user1", "password");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void testLogoutUser() {
        ResponseContext response = userController.logoutUser(mockRequest);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = createTestUser("user1");
        updatedUser.setFirstName("Updated First");
        ResponseContext response = userController.updateUser(mockRequest, "user1", updatedUser);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUser_nonExisting() {
        User updatedUser = createTestUser("ghost");
        ResponseContext response = userController.updateUser(mockRequest, "nonexistent", updatedUser);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUser_nullUsername() {
        User updatedUser = createTestUser("user1");
        ResponseContext response = userController.updateUser(mockRequest, null, updatedUser);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteUser_nullUsername() {
        ResponseContext response = userController.deleteUser(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateUsersWithListInput() {
        User[] users = new User[]{createTestUser("listuser1"), createTestUser("listuser2")};
        ResponseContext response = userController.createUsersWithListInput(mockRequest, users);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateUsersWithListInput_empty() {
        ResponseContext response = userController.createUsersWithListInput(mockRequest, new User[]{});
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private User createTestUser(String username) {
        User user = new User();
        user.setId(100L);
        user.setUsername(username);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhone("123-456-7890");
        user.setUserStatus(1);
        return user;
    }
}
