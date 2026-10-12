package com.fulfilment.application.monolith.fulfillment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductFulfillmentServiceTest {

    private ProductFulfillmentService service;
    private ProductFulfillmentRepository repository;
    private Product product;
    private Store store;
    private DbWarehouse warehouse;

    @BeforeEach
    public void setUp() {
        service = new ProductFulfillmentService();
        repository = mock(ProductFulfillmentRepository.class);
        service.repository = repository;
        
        product = new Product();
        product.id = 1L;
        store = new Store();
        store.id = 1L;
        warehouse = new DbWarehouse();
        warehouse.id = 1L;
    }

    @Test
    public void testAssignProductToWarehouseForStore_Success() {
        when(repository.list(eq("product = ?1 and store = ?2"), eq(product), eq(store)))
                .thenReturn(Collections.emptyList());
        
        PanacheQuery queryStore = mock(PanacheQuery.class);
        when(repository.find("store", store)).thenReturn(queryStore);
        when(queryStore.stream()).thenReturn(Stream.empty());

        PanacheQuery queryStoreWarehouse = mock(PanacheQuery.class);
        when(repository.find("store = ?1 and warehouse = ?2", store, warehouse)).thenReturn(queryStoreWarehouse);
        when(queryStoreWarehouse.count()).thenReturn(0L);

        PanacheQuery queryWarehouse = mock(PanacheQuery.class);
        when(repository.find("warehouse", warehouse)).thenReturn(queryWarehouse);
        when(queryWarehouse.stream()).thenReturn(Stream.empty());

        PanacheQuery queryWarehouseProduct = mock(PanacheQuery.class);
        when(repository.find("warehouse = ?1 and product = ?2", warehouse, product)).thenReturn(queryWarehouseProduct);
        when(queryWarehouseProduct.count()).thenReturn(0L);

        service.assignProductToWarehouseForStore(product, store, warehouse);

        verify(repository).persist(any(ProductFulfillment.class));
    }

    @Test
    public void testAssignProductToWarehouseForStore_MaxWarehousesPerStoreReached() {
        when(repository.list(eq("product = ?1 and store = ?2"), eq(product), eq(store)))
                .thenReturn(Collections.emptyList());

        PanacheQuery queryStore = mock(PanacheQuery.class);
        when(repository.find("store", store)).thenReturn(queryStore);
        
        DbWarehouse w1 = new DbWarehouse(); w1.id = 10L;
        DbWarehouse w2 = new DbWarehouse(); w2.id = 11L;
        DbWarehouse w3 = new DbWarehouse(); w3.id = 12L;
        
        ProductFulfillment pf1 = new ProductFulfillment(); pf1.warehouse = w1;
        ProductFulfillment pf2 = new ProductFulfillment(); pf2.warehouse = w2;
        ProductFulfillment pf3 = new ProductFulfillment(); pf3.warehouse = w3;
        
        when(queryStore.stream()).thenReturn(Stream.of(pf1, pf2, pf3));

        PanacheQuery queryStoreWarehouse = mock(PanacheQuery.class);
        when(repository.find("store = ?1 and warehouse = ?2", store, warehouse)).thenReturn(queryStoreWarehouse);
        when(queryStoreWarehouse.count()).thenReturn(0L);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            service.assignProductToWarehouseForStore(product, store, warehouse);
        });

        assertEquals(400, exception.getResponse().getStatus());
        assertEquals("A store can be fulfilled from a maximum of 3 warehouses", exception.getMessage());
    }

    @Test
    public void testAssignProductToWarehouseForStore_MaxProductsPerWarehouseReached() {
        when(repository.list(eq("product = ?1 and store = ?2"), eq(product), eq(store)))
                .thenReturn(Collections.emptyList());

        PanacheQuery queryStore = mock(PanacheQuery.class);
        when(repository.find("store", store)).thenReturn(queryStore);
        when(queryStore.stream()).thenReturn(Stream.empty());

        PanacheQuery queryStoreWarehouse = mock(PanacheQuery.class);
        when(repository.find("store = ?1 and warehouse = ?2", store, warehouse)).thenReturn(queryStoreWarehouse);
        when(queryStoreWarehouse.count()).thenReturn(0L);

        PanacheQuery queryWarehouse = mock(PanacheQuery.class);
        when(repository.find("warehouse", warehouse)).thenReturn(queryWarehouse);
        
        Product p1 = new Product(); p1.id = 10L;
        Product p2 = new Product(); p2.id = 11L;
        Product p3 = new Product(); p3.id = 12L;
        Product p4 = new Product(); p4.id = 13L;
        Product p5 = new Product(); p5.id = 14L;
        
        ProductFulfillment pf1 = new ProductFulfillment(); pf1.product = p1;
        ProductFulfillment pf2 = new ProductFulfillment(); pf2.product = p2;
        ProductFulfillment pf3 = new ProductFulfillment(); pf3.product = p3;
        ProductFulfillment pf4 = new ProductFulfillment(); pf4.product = p4;
        ProductFulfillment pf5 = new ProductFulfillment(); pf5.product = p5;
        
        when(queryWarehouse.stream()).thenReturn(Stream.of(pf1, pf2, pf3, pf4, pf5));

        PanacheQuery queryWarehouseProduct = mock(PanacheQuery.class);
        when(repository.find("warehouse = ?1 and product = ?2", warehouse, product)).thenReturn(queryWarehouseProduct);
        when(queryWarehouseProduct.count()).thenReturn(0L);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            service.assignProductToWarehouseForStore(product, store, warehouse);
        });

        assertEquals(400, exception.getResponse().getStatus());
        assertEquals("A warehouse can fulfill a maximum of 5 product types", exception.getMessage());
    }
    @Test
    public void testAssignProductToWarehouseForStore_MaxWarehousesPerProductReached() {
        when(repository.list(eq("product = ?1 and store = ?2"), eq(product), eq(store)))
                .thenReturn(List.of(new ProductFulfillment(), new ProductFulfillment()));

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            service.assignProductToWarehouseForStore(product, store, warehouse);
        });

        assertEquals(400, exception.getResponse().getStatus());
        assertEquals("A product for a store can be fulfilled from a maximum of 2 warehouses", exception.getMessage());
    }
}
