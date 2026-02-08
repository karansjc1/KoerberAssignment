package com.assignment.OrderService.service;

import com.assignment.OrderService.persistence.entity.Order;
import com.assignment.OrderService.persistence.repository.OrderRepository;
import com.assignment.OrderService.rest.model.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository repository;

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OrderService orderService;

	private final String INVENTORY_URL = "http://inventory-service/api/products";
	private final String INVENTORY_UPDATE_URL = "http://inventory-service/api/inventory/update";

	@BeforeEach
	void setUp() {
		// Inject the @Value fields manually
		ReflectionTestUtils.setField(orderService, "inventoryServiceBaseUrl", INVENTORY_URL);
		ReflectionTestUtils.setField(orderService, "inventoryServiceUpdateUrl", INVENTORY_UPDATE_URL);
	}

	@Test
	void placeOrder_ShouldSaveOrder_WhenInventoryUpdateSucceeds() throws JsonProcessingException {

		OrderRequest request = new OrderRequest();
		request.setProductId(1001L);
		request.setQuantity(5);

		String mockProductResponse = "{\"productId\": 1001, \"productName\": \"Test Laptop\"}";
		when(restTemplate.getForObject(anyString(), eq(String.class)))
				.thenReturn(mockProductResponse);

		when(restTemplate.postForObject(eq(INVENTORY_UPDATE_URL), any(HttpEntity.class), eq(String.class)))
				.thenReturn("Inventory Updated");

		Order savedOrder = new Order();
		savedOrder.setOrderId(1L);
		savedOrder.setProductName("Test Laptop");
		savedOrder.setStatus("PLACED");
		when(repository.save(any(Order.class))).thenReturn(savedOrder);

		Order result = orderService.placeOrder(request);

		assertNotNull(result);
		assertEquals(1L, result.getOrderId());
		verify(restTemplate).getForObject(contains("1001"), eq(String.class));
		verify(repository).save(any(Order.class));
	}

	@Test
	void placeOrder_ShouldThrowException_WhenInventoryUpdateFails() {
		OrderRequest request = new OrderRequest();
		request.setProductId(1001L);
		request.setQuantity(5);

		String mockProductResponse = "{\"productId\": 1001, \"productName\": \"Test Laptop\"}";
		when(restTemplate.getForObject(anyString(), eq(String.class)))
				.thenReturn(mockProductResponse);

		when(restTemplate.postForObject(eq(INVENTORY_UPDATE_URL), any(HttpEntity.class), eq(String.class)))
				.thenThrow(new RestClientException("Insufficient Stock"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			orderService.placeOrder(request);
		});

		assertTrue(exception.getMessage().contains("Insufficient Stock"));

		verify(repository, never()).save(any(Order.class));
	}
}