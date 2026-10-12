package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplaceWarehouseUseCaseTest {

  private WarehouseStore warehouseStore;
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  public void setUp() {
    warehouseStore = mock(WarehouseStore.class);
    replaceWarehouseUseCase = new ReplaceWarehouseUseCase(warehouseStore);
  }

  @Test
  public void testReplaceWarehouse_Success() {
    String buCode = "BU001";
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = buCode;
    existing.stock = 50;
    existing.capacity = 100;
    existing.archivedAt = null;

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = buCode;
    replacement.stock = 50;
    replacement.capacity = 150;

    when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(existing);

    replaceWarehouseUseCase.replace(replacement);

    assertNotNull(existing.archivedAt);
    verify(warehouseStore).update(existing);
    verify(warehouseStore).create(replacement);
    assertNotNull(replacement.createdAt);
  }

  @Test
  public void testReplaceWarehouse_NotFound() {
    String buCode = "BU001";
    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = buCode;

    when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(null);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      replaceWarehouseUseCase.replace(replacement);
    });

    assertEquals(404, exception.getResponse().getStatus());
  }

  @Test
  public void testReplaceWarehouse_AlreadyArchived() {
    String buCode = "BU001";
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = buCode;
    existing.archivedAt = LocalDateTime.now();

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = buCode;

    when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(existing);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      replaceWarehouseUseCase.replace(replacement);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }

  @Test
  public void testReplaceWarehouse_CapacityCannotAccommodateStock() {
    String buCode = "BU001";
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = buCode;
    existing.stock = 100;
    existing.archivedAt = null;

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = buCode;
    replacement.capacity = 50;
    replacement.stock = 50;

    when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(existing);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      replaceWarehouseUseCase.replace(replacement);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }

  @Test
  public void testReplaceWarehouse_StockMismatch() {
    String buCode = "BU001";
    Warehouse existing = new Warehouse();
    existing.businessUnitCode = buCode;
    existing.stock = 50;
    existing.archivedAt = null;

    Warehouse replacement = new Warehouse();
    replacement.businessUnitCode = buCode;
    replacement.capacity = 100;
    replacement.stock = 60;

    when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(existing);

    WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
      replaceWarehouseUseCase.replace(replacement);
    });

    assertEquals(400, exception.getResponse().getStatus());
  }
}
