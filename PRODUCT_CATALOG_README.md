# Product Catalog & Delivery Items Module

## Overview

The Product Catalog & Delivery Items module provides comprehensive functionality for managing products and delivery items in the Ethiopian Delivery Platform. This module supports Ethiopian product categories, catalog management, and delivery item assignment.

## Features

### Product Catalog Management
- **Ethiopian Product Categories**: Support for traditional Ethiopian products (Injera, Coffee, Spices, etc.)
- **Product CRUD Operations**: Create, read, update, and delete products
- **SKU Management**: Unique Stock Keeping Unit for each product
- **Supplier Integration**: Link products to suppliers
- **Price Management**: Support for Ethiopian Birr (ETB) pricing
- **Category Management**: Organize products by categories
- **Search and Filtering**: Advanced search capabilities
- **Soft Delete**: Maintain data integrity with soft deletion

### Delivery Items Management
- **Delivery Item Assignment**: Add products to deliveries
- **Quantity Management**: Track quantities in deliveries
- **Price Tracking**: Record prices at delivery time
- **Total Calculation**: Automatic total calculation
- **Delivery Integration**: Link items to deliveries
- **Product Validation**: Ensure only active products can be added

## Architecture

### Entities

#### Product
```java
@Entity
public class Product {
    private Long id;
    private String name;
    private String category;
    private String sku; // Unique identifier
    private BigDecimal price;
    private String unit;
    private Supplier supplier;
    private String description;
    private Boolean active;
    // ... auditing fields
}
```

#### DeliveryItem
```java
@Entity
public class DeliveryItem {
    private Long id;
    private Delivery delivery;
    private Product product;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total; // Calculated field
    private Boolean active;
    // ... auditing fields
}
```

### Database Schema

#### Products Table
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    unit VARCHAR(20) NOT NULL,
    supplier_id BIGINT,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);
```

#### Delivery Items Table
```sql
CREATE TABLE delivery_items (
    id BIGSERIAL PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    total DECIMAL(10,2) NOT NULL CHECK (total > 0),
    active BOOLEAN NOT NULL DEFAULT true,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_delivery_items_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(id),
    CONSTRAINT fk_delivery_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uk_delivery_product UNIQUE (delivery_id, product_id)
);
```

## API Endpoints

### Product Management

#### Create Product
```http
POST /api/v1/products
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Traditional Injera",
    "category": "Injera",
    "sku": "INJ-001",
    "price": 25.00,
    "unit": "piece",
    "supplierId": 1,
    "description": "Traditional Ethiopian flatbread made from teff flour"
}
```

#### Get Product by ID
```http
GET /api/v1/products/{id}
Authorization: Bearer <token>
```

#### Get Product by SKU
```http
GET /api/v1/products/sku/{sku}
Authorization: Bearer <token>
```

#### Update Product
```http
PUT /api/v1/products/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "Updated Product Name",
    "price": 30.00,
    "description": "Updated description"
}
```

#### Delete Product
```http
DELETE /api/v1/products/{id}
Authorization: Bearer <token>
```

#### Get All Products (Paginated)
```http
GET /api/v1/products?page=0&size=10&sort=name,asc
Authorization: Bearer <token>
```

#### Get Products by Category
```http
GET /api/v1/products/category/{category}
Authorization: Bearer <token>
```

#### Search Products
```http
GET /api/v1/products/search?searchTerm=coffee
Authorization: Bearer <token>
```

#### Get Products by Price Range
```http
GET /api/v1/products/price-range?minPrice=50&maxPrice=200
Authorization: Bearer <token>
```

#### Get All Categories
```http
GET /api/v1/products/categories
Authorization: Bearer <token>
```

#### Get Product Statistics
```http
GET /api/v1/products/statistics
Authorization: Bearer <token>
```

### Delivery Items Management

#### Create Delivery Item
```http
POST /api/v1/delivery-items
Authorization: Bearer <token>
Content-Type: application/json

{
    "deliveryId": 1,
    "productId": 1,
    "quantity": 5,
    "price": 25.00
}
```

#### Get Delivery Items by Delivery
```http
GET /api/v1/delivery-items/delivery/{deliveryId}
Authorization: Bearer <token>
```

#### Add Product to Delivery (Convenience)
```http
POST /api/v1/delivery-items/delivery/{deliveryId}/product/{productId}?quantity=5&price=25.00
Authorization: Bearer <token>
```

#### Remove Product from Delivery
```http
DELETE /api/v1/delivery-items/delivery/{deliveryId}/product/{productId}
Authorization: Bearer <token>
```

#### Update Product Quantity
```http
PATCH /api/v1/delivery-items/delivery/{deliveryId}/product/{productId}/quantity?newQuantity=10
Authorization: Bearer <token>
```

#### Get Delivery Total Amount
```http
GET /api/v1/delivery-items/delivery/{deliveryId}/total-amount
Authorization: Bearer <token>
```

#### Get Delivery Total Quantity
```http
GET /api/v1/delivery-items/delivery/{deliveryId}/total-quantity
Authorization: Bearer <token>
```

## Ethiopian Product Categories

The system supports the following Ethiopian product categories:

### Food & Beverages
- **Injera**: Traditional Ethiopian flatbread
- **Coffee**: Ethiopian coffee varieties (Yirgacheffe, Sidamo, Harar)
- **Spices**: Traditional spices (Berbere, Mitmita, Cardamom, Cinnamon)
- **Grains**: Teff, Barley, and other grains
- **Legumes**: Chickpeas, Lentils
- **Honey**: Pure Ethiopian honey
- **Beverages**: Tej (honey wine), Tea

### Traditional Foods
- **Traditional**: Shiro powder, traditional ingredients
- **Snacks**: Kolo, Dabo Kolo
- **Dairy**: Ayib cheese, traditional butter

### Fresh Produce
- **Vegetables**: Tomatoes, Onions, Potatoes, Carrots

### Other Categories
- **Pharmaceuticals**: Basic medications
- **Electronics**: Basic electronic items

## Sample Data

The migration script includes sample Ethiopian products:

### Injera Products
- Traditional Injera (ETB 25.00/piece)
- Teff Injera (ETB 30.00/piece)
- Mixed Grain Injera (ETB 20.00/piece)

### Coffee Products
- Ethiopian Yirgacheffe Coffee (ETB 150.00/kg)
- Sidamo Coffee Beans (ETB 120.00/kg)
- Harar Coffee (ETB 180.00/kg)

### Spices
- Berbere Spice (ETB 80.00/kg)
- Mitmita Spice (ETB 90.00/kg)
- Cardamom (ETB 200.00/kg)

## Business Logic

### Product Management
1. **SKU Uniqueness**: Each product must have a unique SKU
2. **Price Validation**: Prices must be positive
3. **Supplier Linking**: Products can be linked to suppliers
4. **Soft Delete**: Products are deactivated rather than deleted
5. **Category Organization**: Products are organized by Ethiopian categories

### Delivery Items Management
1. **Product Validation**: Only active products can be added to deliveries
2. **Duplicate Prevention**: Each product can only be added once per delivery
3. **Total Calculation**: Automatic calculation of total (quantity × price)
4. **Delivery Integration**: Items are linked to specific deliveries
5. **Quantity Validation**: Quantities must be positive

### Security
- **Role-Based Access**: Different endpoints require different roles
- **Admin Access**: Full CRUD operations for admins
- **Partner Access**: Limited operations for partners
- **Driver Access**: Read-only access for drivers

## Usage Examples

### Creating a Product
```java
CreateProductRequestDTO request = CreateProductRequestDTO.builder()
    .name("Traditional Injera")
    .category("Injera")
    .sku("INJ-001")
    .price(new BigDecimal("25.00"))
    .unit("piece")
    .supplierId(1L)
    .description("Traditional Ethiopian flatbread")
    .build();

ProductResponseDTO product = productService.createProduct(request);
```

### Adding Product to Delivery
```java
DeliveryItemResponseDTO item = deliveryItemService.addProductToDelivery(
    deliveryId, 
    productId, 
    quantity, 
    price
);
```

### Getting Delivery Items
```java
List<DeliveryItemResponseDTO> items = deliveryItemService.getDeliveryItemsByDelivery(deliveryId);
BigDecimal totalAmount = deliveryItemService.getDeliveryTotalAmount(deliveryId);
```

## Testing

### Unit Tests
- **ProductServiceTest**: Comprehensive tests for product management
- **DeliveryItemServiceTest**: Tests for delivery item operations
- **Coverage**: CRUD operations, validation, business logic

### Test Scenarios
- Product creation with valid data
- SKU uniqueness validation
- Supplier linking
- Delivery item assignment
- Quantity and price calculations
- Error handling for invalid data

## Migration

### V6__product_catalog_delivery_items.sql
- Creates `products` and `delivery_items` tables
- Adds indexes for performance
- Inserts sample Ethiopian products
- Sets up foreign key constraints
- Creates triggers for timestamp updates

## Dependencies

- **Spring Boot**: Core framework
- **Spring Data JPA**: Database operations
- **MapStruct**: DTO mapping
- **Lombok**: Boilerplate reduction
- **PostgreSQL**: Database
- **Flyway**: Database migrations

## Configuration

### Application Properties
```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/driverapp
spring.datasource.username=postgres
spring.datasource.password=password

# JPA configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Error Handling

### Common Exceptions
- **ResourceNotFoundException**: Product or delivery not found
- **IllegalArgumentException**: Invalid data (duplicate SKU, negative price)
- **ValidationException**: Data validation failures

### Error Responses
```json
{
    "timestamp": "2024-01-01T12:00:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Product with SKU INJ-001 already exists",
    "path": "/api/v1/products"
}
```

## Performance Considerations

### Indexing
- SKU index for fast product lookup
- Category index for filtering
- Delivery ID index for delivery items
- Active status index for filtering

### Pagination
- Support for paginated product lists
- Configurable page size
- Sorting capabilities

### Caching
- Consider caching frequently accessed products
- Cache category lists
- Cache product statistics

## Future Enhancements

### Planned Features
- **Product Images**: Support for product images
- **Inventory Integration**: Link to inventory management
- **Bulk Operations**: Bulk product import/export
- **Advanced Search**: Full-text search capabilities
- **Product Variants**: Support for product variants
- **Pricing History**: Track price changes over time

### Integration Points
- **Inventory System**: Real-time stock levels
- **Order Management**: Direct order creation
- **Analytics**: Product performance metrics
- **Reporting**: Product and delivery reports

## Support

For questions or issues related to the Product Catalog & Delivery Items module:

1. Check the API documentation
2. Review the unit tests for usage examples
3. Check the migration scripts for database schema
4. Contact the development team

## Contributing

When contributing to this module:

1. Follow the existing code structure
2. Add comprehensive unit tests
3. Update documentation
4. Follow naming conventions
5. Ensure proper error handling
6. Add appropriate logging
