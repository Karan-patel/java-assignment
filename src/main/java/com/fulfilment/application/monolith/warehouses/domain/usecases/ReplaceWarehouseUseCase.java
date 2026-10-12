package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  @Transactional
  public void replace(Warehouse newWarehouse) {
    Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

    if (existingWarehouse == null) {
      throw new WebApplicationException("Warehouse to replace not found: " + newWarehouse.businessUnitCode, 404);
    }

    if (existingWarehouse.archivedAt != null) {
      throw new WebApplicationException("Cannot replace an already archived warehouse", 400);
    }

    // Additional Validations for Replacing a Warehouse
    // 1. Capacity Accommodation: Ensure the new warehouse's capacity can accommodate the stock from the warehouse being replaced.
    if (newWarehouse.capacity < existingWarehouse.stock) {
      throw new WebApplicationException("New warehouse capacity cannot accommodate existing stock", 400);
    }

    // 2. Stock Matching: Confirm that the stock of the new warehouse matches the stock of the previous warehouse.
    if (!newWarehouse.stock.equals(existingWarehouse.stock)) {
      throw new WebApplicationException("New warehouse stock must match existing warehouse stock", 400);
    }

    // Archive the current one
    existingWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(existingWarehouse);

    // Create the new one with the same code
    newWarehouse.createdAt = LocalDateTime.now();
    warehouseStore.create(newWarehouse);
  }
}
