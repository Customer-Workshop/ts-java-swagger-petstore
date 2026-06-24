package io.swagger.petstore.controller;

import io.swagger.oas.inflector.models.RequestContext;
import io.swagger.oas.inflector.models.ResponseContext;
import io.swagger.petstore.model.Order;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;
    private RequestContext mockRequest;

    @Before
    public void setUp() {
        orderController = new OrderController();
        mockRequest = mock(RequestContext.class);
        when(mockRequest.getHeaders()).thenReturn(new javax.ws.rs.core.MultivaluedHashMap<>());
        when(mockRequest.getAcceptableMediaTypes()).thenReturn(
                Arrays.asList(MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testGetInventory() {
        ResponseContext response = orderController.getInventory(mockRequest);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Map);
    }

    @Test
    public void testGetOrderById_existing() {
        ResponseContext response = orderController.getOrderById(mockRequest, 1L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Order);
    }

    @Test
    public void testGetOrderById_nonExisting() {
        ResponseContext response = orderController.getOrderById(mockRequest, 999L);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetOrderById_null() {
        ResponseContext response = orderController.getOrderById(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPlaceOrder() {
        Order order = createTestOrder(100L);
        ResponseContext response = orderController.placeOrder(mockRequest, order);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    public void testPlaceOrder_null() {
        ResponseContext response = orderController.placeOrder(mockRequest, (Order) null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteOrder() {
        Order order = createTestOrder(200L);
        orderController.placeOrder(mockRequest, order);

        ResponseContext response = orderController.deleteOrder(mockRequest, 200L);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteOrder_nullId() {
        ResponseContext response = orderController.deleteOrder(mockRequest, null);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private Order createTestOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        order.setPetId(1L);
        order.setQuantity(2);
        order.setShipDate(new Date());
        order.setStatus("placed");
        order.setComplete(false);
        return order;
    }
}
