package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private LocationResolver locationResolver;
  private CreateWarehouseUseCase createWarehouseUseCase;

  @BeforeEach
  public void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    locationResolver = mock(LocationResolver.class);
    createWarehouseUseCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);
  }

  @Test
  public void testCreateWarehouse_Success() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "LOC001";
    warehouse.capacity = 100;
    warehouse.stock = 50;

    Location location = new Location("LOC001", 2, 200);

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
    when(warehouseStore.getAll()).thenReturn(Collections.emptyList());

    createWarehouseUseCase.create(warehouse);

    verify(warehouseStore).create(warehouse);
  }

  @Test
  public void testCreateWarehouse_DuplicateBUCode() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(new Warehouse());

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      createWarehouseUseCase.create(warehouse);
    });

    assertEquals(409, exception.getResponse().getStatus());
  }

  @Test
  public void testCreateWarehouse_InvalidLocation() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "INVALID";

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      createWarehouseUseCase.create(warehouse);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }

  @Test
  public void testCreateWarehouse_MaxWarehousesReached() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "LOC001";

    Location location = new Location("LOC001", 1, 200);

    Warehouse existing = new Warehouse();
    existing.location = "LOC001";
    existing.archivedAt = null;

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
    when(warehouseStore.getAll()).thenReturn(List.of(existing));

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      createWarehouseUseCase.create(warehouse);
    });

    assertEquals(403, exception.getResponse().getStatus());
  }

  @Test
  public void testCreateWarehouse_CapacityExceeded() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "LOC001";
    warehouse.capacity = 150;

    Location location = new Location("LOC001", 2, 200);

    Warehouse existing = new Warehouse();
    existing.location = "LOC001";
    existing.capacity = 100;
    existing.archivedAt = null;

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
    when(warehouseStore.getAll()).thenReturn(List.of(existing));

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      createWarehouseUseCase.create(warehouse);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }

  @Test
  public void testCreateWarehouse_StockExceedsCapacity() {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = "BU001";
    warehouse.location = "LOC001";
    warehouse.capacity = 100;
    warehouse.stock = 150;

    Location location = new Location("LOC001", 2, 200);

    when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
    when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
    when(warehouseStore.getAll()).thenReturn(Collections.emptyList());

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      createWarehouseUseCase.create(warehouse);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }
}
