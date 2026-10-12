package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import jakarta.persistence.*;

@Entity
@Table(name = "product_fulfillment")
public class ProductFulfillment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    public Product product;

    @ManyToOne
    @JoinColumn(name = "store_id")
    public Store store;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    public DbWarehouse warehouse;
}
