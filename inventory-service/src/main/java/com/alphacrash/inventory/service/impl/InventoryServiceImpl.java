package com.alphacrash.inventory.service.impl;

import com.alphacrash.inventory.dto.InventoryResponse;
import com.alphacrash.inventory.model.Inventory;
import com.alphacrash.inventory.repository.InventoryRepository;
import com.alphacrash.inventory.service.InventoryService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    @Override
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        log.info("Wait Started");
        Thread.sleep(10000);
        log.info("Wait Ended");
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .inStock(inventory.getQuantity() > 0)
                        .build()).toList();
    }
}
