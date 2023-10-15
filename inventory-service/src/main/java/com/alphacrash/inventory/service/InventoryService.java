package com.alphacrash.inventory.service;

import com.alphacrash.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    public List<InventoryResponse> isInStock(List<String> skuCodes);
}
