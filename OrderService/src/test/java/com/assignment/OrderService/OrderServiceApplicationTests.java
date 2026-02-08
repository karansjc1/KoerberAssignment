package com.assignment.OrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.assignment.OrderService.persistence.entity.Order;
import com.assignment.OrderService.persistence.repository.OrderRepository;
import com.assignment.OrderService.rest.model.OrderRequest;
import com.assignment.OrderService.service.OrderService;

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
	private OrderService service;

	@Test
	void placeOrder_ShouldSaveOrder_WhenInventoryUpdateSucceeds() {
		// Arrange
		OrderRequest request = new OrderRequest();
		request.setProductId(1001L);
		request.setQuantity(5);

		Order savedOrder = new Order();
		savedOrder.setOrderId(1L);
		savedOrder.setStatus("PLACED");

		// Mock successful Inventory call (returns null or any string, doesn't throw
		// exception)
		when(restTemplate.postForObject(anyString(), any(), eq(String.class))).thenReturn("Success");
		when(repository.save(any(Order.class))).thenReturn(savedOrder);

		// Act
		Order result = service.placeOrder(request);

		// Assert
		assertNotNull(result);
		assertEquals(1L, result.getOrderId());
		verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(String.class));
		verify(repository, times(1)).save(any(Order.class));
	}

	@Test
	void placeOrder_ShouldThrowException_WhenInventoryUpdateFails() {
		// Arrange
		OrderRequest request = new OrderRequest();
		request.setProductId(1001L);
		request.setQuantity(500); // Assume too much quantity

		// Mock Inventory call throwing exception (e.g., 400 Bad Request / Insufficient
		// Stock)
		when(restTemplate.postForObject(anyString(), any(), eq(String.class)))
				.thenThrow(new RuntimeException("Insufficient Stock"));

		// Act & Assert
		Exception exception = assertThrows(RuntimeException.class, () -> {
			service.placeOrder(request);
		});

		assertEquals("Failed to place order: Insufficient Stock", exception.getMessage());

		// IMPORTANT: Verify that repository.save() was NEVER called
		verify(repository, never()).save(any(Order.class));
	}
}