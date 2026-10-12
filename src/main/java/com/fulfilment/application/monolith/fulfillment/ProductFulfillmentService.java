package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

@ApplicationScoped
public class ProductFulfillmentService {

    private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;
    private static final int MAX_PRODUCTS_FULF_SIZE = 2;
    private static final int MAX_STORE_FULF_SIZE = 3;

    @Inject
    ProductFulfillmentRepository repository;

    @Transactional
    public void assignProductToWarehouseForStore(Product product, Store store, DbWarehouse warehouse) {
        // Constraints Check:
        // 1. A product for a store can be fulfilled from a maximum of 2 warehouses.
        List<ProductFulfillment> productFulfillments = repository.list("product = ?1 and store = ?2", product, store);
        if (productFulfillments.size() >= MAX_PRODUCTS_FULF_SIZE) {
            throw new WebApplicationException("A product for a store can be fulfilled from a maximum of 2 warehouses", 400);
        }

        // 2. A store can be fulfilled from a maximum of 3 warehouses.
        long distinctWarehousesForStore = repository.find("store", store).stream()
                .map(pf -> pf.warehouse.id)
                .distinct()
                .count();
        
        boolean warehouseAlreadyAssignedToStore = repository.find("store = ?1 and warehouse = ?2", store, warehouse).count() > 0;
        
        if (!warehouseAlreadyAssignedToStore && distinctWarehousesForStore >= MAX_STORE_FULF_SIZE) {
            throw new WebApplicationException("A store can be fulfilled from a maximum of 3 warehouses", 400);
        }

        // 3. A warehouse can fulfill a maximum of 5 product types.
        long distinctProductsInWarehouse = repository.find("warehouse", warehouse).stream()
                .map(pf -> pf.product.id)
                .distinct()
                .count();

        boolean productAlreadyInWarehouse = repository.find("warehouse = ?1 and product = ?2", warehouse, product).count() > 0;

        if (!productAlreadyInWarehouse && distinctProductsInWarehouse >= MAX_PRODUCTS_PER_WAREHOUSE) {
            throw new WebApplicationException("A warehouse can fulfill a maximum of 5 product types", 400);
        }

        // Create assignment
        final ProductFulfillment fulfillment = new ProductFulfillment();
        fulfillment.product = product;
        fulfillment.store = store;
        fulfillment.warehouse = warehouse;
        repository.persist(fulfillment);
    }
}

@ApplicationScoped
class ProductFulfillmentRepository implements PanacheRepository<ProductFulfillment> {
}
