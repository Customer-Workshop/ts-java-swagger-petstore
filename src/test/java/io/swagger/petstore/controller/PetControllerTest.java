package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.Category;
import io.swagger.petstore.model.Pet;
import io.swagger.petstore.model.Tag;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetControllerTest {

    private PetController petController;
    private RequestContext mockRequest;

    @Before
    public void setUp() {
        petController = new PetController();
        mockRequest = mock(RequestContext.class);
        when(mockRequest.getHeaders()).thenReturn(new javax.ws.rs.core.MultivaluedHashMap<>());
        when(mockRequest.getAcceptableMediaTypes()).thenReturn(
                Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testGetPetById_existingPet() {
        Pet testPet = createTestPet(300L, "GetById Test Pet");
        petController.addPet(mockRequest, testPet);

        ResponseContext response = petController.getPetById(mockRequest, 300L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Pet);
        assertEquals("GetById Test Pet", ((Pet) response.getEntity()).getName());
    }

    @Test
    public void testGetPetById_nonExistingPet() {
        ResponseContext response = petController.getPetById(mockRequest, 999L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetPetById_nullId() {
        ResponseContext response = petController.getPetById(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testFindPetsByStatus_available() {
        ResponseContext response = petController.findPetsByStatus(mockRequest, "available");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof List);
        List<?> pets = (List<?>) response.getEntity();
        assertFalse(pets.isEmpty());
    }

    @Test
    public void testFindPetsByStatus_nullStatus() {
        ResponseContext response = petController.findPetsByStatus(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddPet() {
        Pet newPet = createTestPet(100L, "Test Dog");
        ResponseContext response = petController.addPet(mockRequest, newPet);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void testAddPet_null() {
        ResponseContext response = petController.addPet(mockRequest, (Pet) null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdatePet_existing() {
        Pet updatedPet = createTestPet(1L, "Updated Cat");
        ResponseContext response = petController.updatePet(mockRequest, updatedPet);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Updated Cat", ((Pet) response.getEntity()).getName());
    }

    @Test
    public void testUpdatePet_nonExisting() {
        Pet updatedPet = createTestPet(999L, "Ghost Pet");
        ResponseContext response = petController.updatePet(mockRequest, updatedPet);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeletePet() {
        Pet newPet = createTestPet(200L, "Delete Me");
        petController.addPet(mockRequest, newPet);

        ResponseContext response = petController.deletePet(mockRequest, null, 200L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeletePet_nullId() {
        ResponseContext response = petController.deletePet(mockRequest, null, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testFindPetsByTags() {
        List<String> tags = Arrays.asList("tag1");
        ResponseContext response = petController.findPetsByTags(mockRequest, tags);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof List);
    }

    @Test
    public void testFindPetsByTags_emptyTags() {
        ResponseContext response = petController.findPetsByTags(mockRequest, new ArrayList<>());
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdatePetWithForm() {
        Pet testPet = createTestPet(400L, "FormUpdate Pet");
        petController.addPet(mockRequest, testPet);

        ResponseContext response = petController.updatePetWithForm(mockRequest, 400L, "New Name", "pending");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Pet pet = (Pet) response.getEntity();
        assertEquals("New Name", pet.getName());
        assertEquals("pending", pet.getStatus());
    }

    @Test
    public void testUpdatePetWithForm_nullPetId() {
        ResponseContext response = petController.updatePetWithForm(mockRequest, null, "Name", "available");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private Pet createTestPet(Long id, String name) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        Category category = new Category();
        category.setId(1L);
        category.setName("Dogs");
        pet.setCategory(category);
        pet.setPhotoUrls(Arrays.asList("http://example.com/photo.jpg"));
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");
        pet.setTags(Arrays.asList(tag));
        pet.setStatus("available");
        return pet;
    }
}
