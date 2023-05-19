package com.ecom.catalog.admin.infrastructure.product;

import com.ecom.catalog.admin.MySQLGatewayTest;
import com.ecom.catalog.admin.infrastructure.product.persistence.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class ProductMySQLGatewayTest {

    @Autowired
    private ProductMySQLGateway productMySQLGateway;

    @Autowired
    private ProductRepository productRepository;


}
