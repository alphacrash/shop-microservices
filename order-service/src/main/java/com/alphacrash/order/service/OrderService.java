package com.alphacrash.order.service;

import com.alphacrash.order.dto.OrderRequest;

public interface OrderService {
    public String placeOrder(OrderRequest orderRequest);
}
