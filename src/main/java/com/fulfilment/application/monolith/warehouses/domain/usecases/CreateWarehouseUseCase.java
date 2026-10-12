package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  @Transactional
  public void create(Warehouse warehouse) {

    if(isValid(warehouse)){
      // if all went well, create the warehouse
      warehouseStore.create(warehouse);
    }
  }

  /*
    Validations and constraints before creating a warehouse:
  */
  private boolean isValid(Warehouse warehouse){

    // 1. Business Unit Code Verification
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WebApplicationException("Business unit code already exists: " + warehouse.businessUnitCode, 409);
    }

    // 2. Location Validation
    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
      throw new WebApplicationException("Invalid location identifier: " + warehouse.location, 400);
    }

    List<Warehouse> existingWarehousesInLocation = warehouseStore.getAll().stream()
            .filter(w -> w.location.equalsIgnoreCase(warehouse.location) && w.archivedAt == null)
            .toList();

    // 3. Warehouse Creation Feasibility
    if (existingWarehousesInLocation.size() >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException("Maximum number of warehouses reached for location: " + warehouse.location, 403);
    }

    // 4. Capacity and Stock Validation
    int currentTotalCapacity = existingWarehousesInLocation.stream()
            .mapToInt(w -> w.capacity)
            .sum();

    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException("Total capacity exceeds maximum for location: " + warehouse.location, 400);
    }

    if (warehouse.stock > warehouse.capacity) {
      throw new WebApplicationException("Stock exceeds warehouse capacity", 400);
    }

    return true;
  }
}
