# Customer Management Module

## Overview

The Customer Management module is a comprehensive system for managing customer information, addresses, and delivery preferences within the Ethiopian Delivery Platform. It provides full CRUD operations for customers and their associated addresses, with support for multiple saved addresses, delivery preferences, and customer verification.

## Architecture

### Entities

#### Customer
- **id**: Primary key
- **userId**: Link to User entity (one-to-one relationship)
- **fullName**: Customer's full name
- **phone**: Unique phone number
- **email**: Unique email address
- **preferredPayment**: Payment method preference (CASH, CARD, MOBILE_MONEY, BANK_TRANSFER)
- **defaultAddressId**: Reference to default delivery address
- **region**: Customer's region/location
- **deliveryPreferences**: JSON string for flexible delivery preferences
- **active**: Whether customer account is active
- **verified**: Whether customer is verified
- **addresses**: One-to-many relationship with Address entities
- **deliveries**: One-to-many relationship with Delivery entities

#### Address
- **id**: Primary key
- **customerId**: Reference to Customer entity
- **addressLine1**: Primary address line
- **addressLine2**: Secondary address line (optional)
- **city**: City name
- **region**: Region/state name
- **postalCode**: Postal/ZIP code
- **landmark**: Nearby landmark for easy identification
- **additionalInstructions**: Special delivery instructions
- **isDefault**: Whether this is the default address
- **active**: Whether address is active

### Key Features

1. **Customer Management**
   - Create, read, update, delete customer profiles
   - Customer verification system
   - Search customers by name, phone, email, or region
   - Pagination support for all list operations

2. **Address Management**
   - Multiple saved addresses per customer
   - Default address designation
   - Address validation and business logic
   - Soft delete for addresses

3. **Delivery Preferences**
   - JSON-based flexible preference storage
   - Support for preferred delivery times
   - Special instructions per customer
   - Integration with delivery system

4. **Security & Access Control**
   - Role-based access control (ADMIN, MANAGER, DRIVER, CUSTOMER)
   - User-specific data access
   - Secure API endpoints

## Database Schema

### Tables

#### customers
```sql
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    full_name VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(254) UNIQUE,
    preferred_payment VARCHAR(50),
    default_address_id BIGINT,
    region VARCHAR(100),
    delivery_preferences TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### addresses
```sql
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    landmark VARCHAR(100),
    additional_instructions VARCHAR(500),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Indexes
- Customer indexes: user_id, phone, email, full_name, region, active, verified
- Address indexes: customer_id, is_default, region, city, active
- Delivery indexes: customer_id

## API Endpoints

### Customer Endpoints

#### Create Customer
```
POST /api/v1/customers
Authorization: ADMIN, MANAGER
Body: CreateCustomerRequestDTO
```

#### Get Customer by ID
```
GET /api/v1/customers/{id}
Authorization: ADMIN, MANAGER, DRIVER
```

#### Get Customer by User ID
```
GET /api/v1/customers/user/{userId}
Authorization: ADMIN, MANAGER, DRIVER, or own user
```

#### Get All Customers
```
GET /api/v1/customers?page=0&size=20
Authorization: ADMIN, MANAGER
```

#### Get Customers by Region
```
GET /api/v1/customers/region/{region}?page=0&size=20
Authorization: ADMIN, MANAGER, DRIVER
```

#### Get Verified Customers
```
GET /api/v1/customers/verified?page=0&size=20
Authorization: ADMIN, MANAGER
```

#### Search Customers
```
GET /api/v1/customers/search?searchTerm=test&page=0&size=20
Authorization: ADMIN, MANAGER, DRIVER
```

#### Update Customer
```
PUT /api/v1/customers/{id}
Authorization: ADMIN, MANAGER, or own customer
Body: UpdateCustomerRequestDTO
```

#### Delete Customer
```
DELETE /api/v1/customers/{id}
Authorization: ADMIN, MANAGER
```

#### Verify Customer
```
POST /api/v1/customers/{id}/verify
Authorization: ADMIN, MANAGER
```

#### Unverify Customer
```
POST /api/v1/customers/{id}/unverify
Authorization: ADMIN, MANAGER
```

#### Update Delivery Preferences
```
PUT /api/v1/customers/{id}/preferences
Authorization: ADMIN, MANAGER, or own customer
Body: UpdateDeliveryPreferencesRequestDTO
```

### Address Endpoints

#### Create Address
```
POST /api/v1/customers/addresses
Authorization: ADMIN, MANAGER, CUSTOMER
Body: CreateAddressRequestDTO
```

#### Get Address by ID
```
GET /api/v1/customers/addresses/{id}
Authorization: ADMIN, MANAGER, DRIVER
```

#### Get Addresses by Customer
```
GET /api/v1/customers/{customerId}/addresses
Authorization: ADMIN, MANAGER, DRIVER, or own customer
```

#### Update Address
```
PUT /api/v1/customers/addresses/{id}
Authorization: ADMIN, MANAGER, CUSTOMER
Body: UpdateAddressRequestDTO
```

#### Delete Address
```
DELETE /api/v1/customers/addresses/{id}
Authorization: ADMIN, MANAGER, CUSTOMER
```

#### Set Default Address
```
POST /api/v1/customers/{customerId}/addresses/{addressId}/default
Authorization: ADMIN, MANAGER, or own customer
```

### Statistics Endpoint

#### Get Customer Statistics
```
GET /api/v1/customers/statistics
Authorization: ADMIN, MANAGER
```

## DTOs

### Request DTOs

#### CreateCustomerRequestDTO
```java
{
    "userId": 1,
    "fullName": "Abebe Kebede",
    "phone": "+251911234567",
    "email": "abebe.kebede@email.com",
    "preferredPayment": "CASH",
    "region": "Addis Ababa",
    "deliveryPreferences": "{\"preferredTime\": \"morning\"}"
}
```

#### UpdateCustomerRequestDTO
```java
{
    "fullName": "Updated Name",
    "phone": "+251922345678",
    "email": "updated@email.com",
    "preferredPayment": "MOBILE_MONEY",
    "region": "Dire Dawa",
    "deliveryPreferences": "{\"preferredTime\": \"afternoon\"}"
}
```

#### CreateAddressRequestDTO
```java
{
    "customerId": 1,
    "addressLine1": "Bole Road",
    "addressLine2": "Building 123, Apartment 4A",
    "city": "Addis Ababa",
    "region": "Addis Ababa",
    "postalCode": "1000",
    "landmark": "Near Bole Airport",
    "additionalInstructions": "Call before delivery",
    "isDefault": true
}
```

### Response DTOs

#### CustomerResponseDTO
```java
{
    "id": 1,
    "userId": 1,
    "fullName": "Abebe Kebede",
    "phone": "+251911234567",
    "email": "abebe.kebede@email.com",
    "preferredPayment": "CASH",
    "defaultAddressId": 1,
    "region": "Addis Ababa",
    "deliveryPreferences": "{\"preferredTime\": \"morning\"}",
    "active": true,
    "verified": true,
    "addresses": [...],
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
}
```

#### AddressResponseDTO
```java
{
    "id": 1,
    "customerId": 1,
    "addressLine1": "Bole Road",
    "addressLine2": "Building 123, Apartment 4A",
    "city": "Addis Ababa",
    "region": "Addis Ababa",
    "postalCode": "1000",
    "landmark": "Near Bole Airport",
    "additionalInstructions": "Call before delivery",
    "isDefault": true,
    "active": true,
    "fullAddress": "Bole Road, Building 123, Apartment 4A, Addis Ababa, Addis Ababa 1000",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
}
```

## Business Logic

### Customer Management
- **Validation**: Phone and email uniqueness, user existence
- **Verification**: Customer verification workflow
- **Soft Delete**: Customers are marked inactive rather than deleted
- **Search**: Multi-field search with pagination

### Address Management
- **Default Address**: Only one default address per customer
- **Business Logic**: Automatic default address handling
- **Validation**: Address format and customer ownership
- **Soft Delete**: Addresses are marked inactive

### Delivery Preferences
- **Flexible Storage**: JSON-based preference storage
- **Integration**: Links to delivery system
- **Validation**: JSON format validation

## Security Features

### Role-Based Access Control
- **ADMIN**: Full access to all operations
- **MANAGER**: Customer management and verification
- **DRIVER**: Read access to customer and address data
- **CUSTOMER**: Own profile and address management

### Data Protection
- **User Isolation**: Users can only access their own data
- **Input Validation**: Comprehensive validation on all inputs
- **Audit Trail**: Created/updated timestamps on all entities

## Testing

### Unit Tests
- **CustomerServiceTest**: Comprehensive test coverage for all business logic
- **Test Coverage**: CRUD operations, validation, business rules
- **Mock Testing**: Repository and mapper mocking
- **Error Scenarios**: Exception handling and edge cases

### Test Scenarios
- Customer creation, update, deletion
- Address management and default address logic
- Customer verification workflow
- Search and filtering operations
- Error handling and validation

## Integration Points

### With User Management
- Links customers to user accounts
- User authentication and authorization
- Profile synchronization

### With Delivery System
- Customer information in deliveries
- Delivery preferences integration
- Address selection for deliveries

### With Inventory Management
- Customer preferences for product delivery
- Regional inventory availability

## Performance Considerations

### Database Optimization
- **Indexes**: Optimized indexes for common queries
- **Pagination**: Efficient pagination for large datasets
- **Lazy Loading**: JPA lazy loading for relationships

### Caching Strategy
- **Customer Data**: Frequently accessed customer information
- **Address Data**: Default addresses and common locations
- **Search Results**: Cached search results for performance

## Monitoring and Logging

### Logging
- **Structured Logging**: SLF4J with structured log messages
- **Audit Trail**: All customer and address changes logged
- **Error Tracking**: Comprehensive error logging

### Metrics
- **Customer Statistics**: Total, active, verified customers
- **Address Usage**: Default address distribution
- **API Performance**: Response times and throughput

## Future Enhancements

### Planned Features
- **Customer Segmentation**: Advanced customer categorization
- **Preference Analytics**: Delivery preference analysis
- **Address Validation**: Integration with address validation services
- **Customer Communication**: Notification and messaging system

### Scalability Improvements
- **Database Sharding**: Horizontal scaling for large datasets
- **Microservices**: Service decomposition for better scalability
- **Event-Driven Architecture**: Asynchronous event processing

## Deployment

### Database Migration
- **Flyway Migration**: V5__customer_management.sql
- **Sample Data**: Initial test data included
- **Rollback Support**: Migration rollback capabilities

### Configuration
- **Environment Variables**: Database and service configuration
- **Feature Flags**: Toggle new features
- **Monitoring**: Health checks and metrics endpoints

## Support and Maintenance

### Documentation
- **API Documentation**: OpenAPI/Swagger documentation
- **Code Comments**: Comprehensive code documentation
- **User Guides**: Customer management workflows

### Maintenance
- **Regular Updates**: Security and performance updates
- **Data Cleanup**: Inactive customer and address cleanup
- **Backup Strategy**: Regular data backups and recovery

---

This Customer Management module provides a robust foundation for managing customer relationships in the Ethiopian Delivery Platform, with comprehensive features for address management, delivery preferences, and customer verification.
