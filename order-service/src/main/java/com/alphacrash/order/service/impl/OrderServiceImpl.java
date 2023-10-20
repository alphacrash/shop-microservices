package com.alphacrash.order.service.impl;

import com.alphacrash.order.dto.InventoryResponse;
import com.alphacrash.order.dto.OrderLineItemsDto;
import com.alphacrash.order.dto.OrderRequest;
import com.alphacrash.order.model.Order;
import com.alphacrash.order.model.OrderLineItems;
import com.alphacrash.order.repository.OrderRepository;
import com.alphacrash.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    @CircuitBreaker(name = "inventory", fallbackMethod = "placeOrderFallback")
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(OrderServiceImpl::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory/",
                        uriBuilder -> uriBuilder.queryParam("sku-code", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        if (inventoryResponses != null && Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock)) {
            orderRepository.save(order);
            return "Order placed successfully";
        } else {
            throw new IllegalArgumentException("Inventory is not in stock");
        }
    }

    private static OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }

    public String placeOrderFallback(OrderRequest orderRequest, Exception e) {
        return "Order Service is down. Please try again later.";
    }
}
