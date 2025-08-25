# Payment & Billing System

## Overview

The Payment & Billing System is a comprehensive module for the Ethiopian Delivery Platform that handles all financial transactions, including payments, invoicing, driver earnings, and wallet management. The system integrates with major Ethiopian payment providers and provides robust billing capabilities for partners and drivers.

## Features

### 🏦 Payment Processing
- **Multi-Provider Support**: Integration with major Ethiopian payment providers
  - TeleBirr (Ethio Telecom)
  - CBE Birr (Commercial Bank of Ethiopia)
  - M-Birr (M-Birr Mobile Money)
  - HelloCash (Dashen Bank)
  - Amole (Amole Digital Wallet)
- **Payment Lifecycle Management**: Initiation, processing, confirmation, and refund handling
- **Transaction Tracking**: Complete audit trail for all payment activities
- **Failure Handling**: Robust error handling and retry mechanisms

### 📄 Invoice Management
- **Partner Billing**: Automated invoice generation for partner services
- **Invoice Lifecycle**: Draft, sent, paid, overdue, and cancelled statuses
- **Due Date Management**: Automatic overdue detection and notifications
- **Payment Tracking**: Partial and full payment support with reconciliation

### 💰 Driver Earnings
- **Earnings Calculation**: Automatic calculation based on delivery completion
- **Payout Processing**: Batch and individual payout processing
- **Commission Management**: Configurable commission rates (default 75%)
- **Payout Status Tracking**: Pending, processing, completed, failed states

### 💳 Wallet System
- **Multi-Owner Support**: Separate wallets for drivers and partners
- **Credit/Debit Operations**: Secure balance management with transaction history
- **Balance Validation**: Insufficient balance prevention
- **Transaction Audit**: Complete transaction history with before/after balances

## Architecture

### Core Entities

#### Payment
```java
@Entity
public class Payment {
    private Long id;
    private User user;
    private Delivery delivery;
    private BigDecimal amount;
    private String currency; // ETB
    private PaymentProvider provider;
    private PaymentStatus status;
    private String transactionRef;
    private String description;
    private String failureReason;
    // ... auditing fields
}
```

#### Invoice
```java
@Entity
public class Invoice {
    private Long id;
    private Partner partner;
    private String invoiceNumber;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private BigDecimal paidAmount;
    private LocalDate paidDate;
    private String paymentReference;
    // ... auditing fields
}
```

#### DriverEarning
```java
@Entity
public class DriverEarning {
    private Long id;
    private Driver driver;
    private Delivery delivery;
    private BigDecimal amount;
    private PayoutStatus payoutStatus;
    private String payoutReference;
    private Instant payoutDate;
    private String failureReason;
    // ... auditing fields
}
```

#### Wallet
```java
@Entity
public class Wallet {
    private Long id;
    private Long ownerId;
    private WalletOwnerType ownerType; // DRIVER, PARTNER
    private BigDecimal balance;
    private String description;
    // ... auditing fields
}
```

#### WalletTransaction
```java
@Entity
public class WalletTransaction {
    private Long id;
    private Wallet wallet;
    private TransactionType transactionType; // CREDIT, DEBIT
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String reference;
    private String description;
    // ... auditing fields
}
```

### Payment Provider Integration

The system uses a pluggable architecture for payment providers:

```java
public interface PaymentProviderService {
    PaymentProvider getProvider();
    PaymentResponse initiatePayment(PaymentRequest request);
    PaymentResponse confirmPayment(String transactionRef);
    PaymentResponse refundPayment(String transactionRef, BigDecimal amount);
    boolean isSupported(PaymentProvider provider);
}
```

#### Supported Providers
- **TeleBirr**: Primary mobile money service in Ethiopia
- **CBE Birr**: Commercial Bank of Ethiopia's digital payment
- **M-Birr**: Mobile money service
- **HelloCash**: Dashen Bank's digital wallet
- **Amole**: Digital wallet service

## API Endpoints

### Payment Management

#### Initiate Payment
```http
POST /api/v1/payments/initiate
Content-Type: application/json

{
  "deliveryId": 1,
  "amount": 150.00,
  "currency": "ETB",
  "provider": "TELEBIRR",
  "phoneNumber": "+251912345678",
  "description": "Delivery payment"
}
```

#### Confirm Payment
```http
POST /api/v1/payments/confirm
Content-Type: application/json

{
  "transactionRef": "TEL_123456789"
}
```

#### Get Payment by ID
```http
GET /api/v1/payments/{id}
```

#### Get Payments by User
```http
GET /api/v1/payments/user/{userId}?page=0&size=20
```

#### Get Payments by Status
```http
GET /api/v1/payments/status/{status}?page=0&size=20
```

### Invoice Management

#### Create Invoice
```http
POST /api/v1/invoices
Content-Type: application/json

{
  "partnerId": 1,
  "totalAmount": 5000.00,
  "dueDate": "2024-02-15",
  "description": "Monthly service invoice"
}
```

#### Update Invoice
```http
PUT /api/v1/invoices/{id}
Content-Type: application/json

{
  "totalAmount": 5500.00,
  "status": "SENT"
}
```

#### Mark Invoice as Paid
```http
POST /api/v1/invoices/{id}/mark-paid?amount=5000.00&paymentReference=PAY_REF_001
```

#### Generate Invoice from Deliveries
```http
POST /api/v1/invoices/generate-from-deliveries?partnerId=1&dueDate=2024-02-15&description=Monthly invoice
```

### Driver Earnings

#### Create Earning from Delivery
```http
POST /api/v1/driver-earnings/delivery/{deliveryId}
```

#### Process Payout
```http
POST /api/v1/driver-earnings/payout
Content-Type: application/json

{
  "driverId": 1
}
```

#### Process All Pending Payouts
```http
POST /api/v1/driver-earnings/payout/all
```

#### Get Earnings by Driver
```http
GET /api/v1/driver-earnings/driver/{driverId}?page=0&size=20
```

### Wallet Management

#### Create Wallet
```http
POST /api/v1/wallets?ownerId=1&ownerType=DRIVER&description=Driver wallet
```

#### Credit Wallet
```http
POST /api/v1/wallets/credit
Content-Type: application/json

{
  "ownerId": 1,
  "ownerType": "DRIVER",
  "amount": 500.00,
  "reference": "PAY_REF_001",
  "description": "Payment credit"
}
```

#### Debit Wallet
```http
POST /api/v1/wallets/debit
Content-Type: application/json

{
  "ownerId": 1,
  "ownerType": "DRIVER",
  "amount": 100.00,
  "reference": "WITHDRAW_REF_001",
  "description": "Cash withdrawal"
}
```

#### Get Wallet by Owner
```http
GET /api/v1/wallets/owner/{ownerId}/type/{ownerType}
```

#### Get Wallet Transactions
```http
GET /api/v1/wallets/{walletId}/transactions?page=0&size=20
```

#### Get Total Balance
```http
GET /api/v1/wallets/balance/total
```

## Business Logic

### Payment Processing Flow

1. **Initiation**: User initiates payment for a delivery
2. **Provider Integration**: System calls appropriate payment provider
3. **Transaction Creation**: Payment record created with PENDING status
4. **Confirmation**: User confirms payment via provider
5. **Status Update**: Payment status updated to COMPLETED/FAILED
6. **Wallet Credit**: Driver wallet credited upon successful payment

### Invoice Generation Flow

1. **Delivery Completion**: System tracks completed deliveries
2. **Periodic Aggregation**: Monthly/weekly aggregation of partner services
3. **Invoice Creation**: Automatic invoice generation with unique number
4. **Notification**: Invoice sent to partner
5. **Payment Tracking**: Monitor payment status and due dates
6. **Overdue Handling**: Automatic overdue status updates

### Driver Earnings Flow

1. **Delivery Completion**: Driver completes delivery
2. **Earnings Calculation**: 75% of delivery price calculated as earnings
3. **Earnings Record**: DriverEarning record created with PENDING status
4. **Payout Processing**: Batch or individual payout processing
5. **Wallet Credit**: Driver wallet credited upon successful payout

### Wallet Operations

1. **Balance Validation**: Check sufficient balance before debit operations
2. **Transaction Recording**: Every operation creates a transaction record
3. **Balance Update**: Atomic balance updates with transaction history
4. **Audit Trail**: Complete audit trail for all wallet operations

## Security & Authorization

### Role-Based Access Control

- **ADMIN**: Full access to all payment and billing operations
- **MANAGER**: Access to payment processing, invoice management, and reporting
- **USER**: Limited access to initiate payments and view own transactions

### Security Features

- **Transaction Validation**: All financial operations validated
- **Audit Logging**: Complete audit trail for compliance
- **Balance Protection**: Insufficient balance prevention
- **Provider Security**: Secure integration with payment providers

## Database Schema

### Key Tables

```sql
-- Payments table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    delivery_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'ETB',
    provider VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(255),
    description TEXT,
    failure_reason TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    -- ... auditing fields
);

-- Invoices table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    partner_id BIGINT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    total_amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    paid_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    paid_date DATE,
    payment_reference VARCHAR(255),
    -- ... auditing fields
);

-- Driver earnings table
CREATE TABLE driver_earnings (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    delivery_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payout_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payout_reference VARCHAR(255),
    payout_date TIMESTAMP,
    failure_reason TEXT,
    -- ... auditing fields
);

-- Wallets table
CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(20) NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0,
    description TEXT,
    -- ... auditing fields
);

-- Wallet transactions table
CREATE TABLE wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    balance_before DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    reference VARCHAR(255),
    description TEXT,
    -- ... auditing fields
);
```

### Indexes for Performance

```sql
-- Payment indexes
CREATE INDEX ix_payments_user_id ON payments(user_id) WHERE active = true;
CREATE INDEX ix_payments_delivery_id ON payments(delivery_id) WHERE active = true;
CREATE INDEX ix_payments_status ON payments(status) WHERE active = true;
CREATE INDEX ix_payments_provider ON payments(provider) WHERE active = true;
CREATE INDEX ix_payments_transaction_ref ON payments(transaction_ref) WHERE active = true;

-- Invoice indexes
CREATE INDEX ix_invoices_partner_id ON invoices(partner_id) WHERE active = true;
CREATE INDEX ix_invoices_status ON invoices(status) WHERE active = true;
CREATE INDEX ix_invoices_due_date ON invoices(due_date) WHERE active = true;
CREATE INDEX ix_invoices_invoice_number ON invoices(invoice_number) WHERE active = true;

-- Driver earnings indexes
CREATE INDEX ix_driver_earnings_driver_id ON driver_earnings(driver_id) WHERE active = true;
CREATE INDEX ix_driver_earnings_payout_status ON driver_earnings(payout_status) WHERE active = true;

-- Wallet indexes
CREATE INDEX ix_wallets_owner_id ON wallets(owner_id) WHERE active = true;
CREATE INDEX ix_wallets_owner_type ON wallets(owner_type) WHERE active = true;
CREATE INDEX ix_wallets_balance ON wallets(balance) WHERE active = true;
```

## Testing

### Unit Tests

The module includes comprehensive unit tests covering:

- **Payment Processing**: Initiation, confirmation, failure scenarios
- **Invoice Management**: Creation, updates, payment tracking
- **Driver Earnings**: Calculation, payout processing
- **Wallet Operations**: Credit, debit, balance validation
- **Provider Integration**: Mock provider responses

### Test Coverage

```bash
# Run payment service tests
mvn test -Dtest=PaymentServiceTest

# Run wallet service tests
mvn test -Dtest=WalletServiceTest

# Run all payment & billing tests
mvn test -Dtest="*Payment*Test,*Wallet*Test,*Invoice*Test,*DriverEarning*Test"
```

## Configuration

### Application Properties

```properties
# Payment provider configuration
payment.provider.telebirr.enabled=true
payment.provider.cbe-birr.enabled=true
payment.provider.m-birr.enabled=true
payment.provider.hellocash.enabled=true
payment.provider.amole.enabled=true

# Driver commission rate (default 75%)
driver.commission.rate=0.75

# Invoice settings
invoice.number.prefix=INV
invoice.auto-generate.enabled=true
invoice.overdue.days=30

# Wallet settings
wallet.minimum.balance=0.00
wallet.maximum.transaction.amount=10000.00
```

## Monitoring & Reporting

### Key Metrics

- **Payment Success Rate**: Percentage of successful payments
- **Provider Performance**: Response times and success rates by provider
- **Invoice Collection**: Days sales outstanding (DSO)
- **Driver Payouts**: Payout processing times and success rates
- **Wallet Activity**: Transaction volumes and balance trends

### Dashboard Views

```sql
-- Payment summary view
CREATE VIEW payment_summary AS
SELECT 
    provider,
    status,
    COUNT(*) as count,
    SUM(amount) as total_amount
FROM payments 
WHERE active = true 
GROUP BY provider, status;

-- Invoice summary view
CREATE VIEW invoice_summary AS
SELECT 
    status,
    COUNT(*) as count,
    SUM(total_amount) as total_amount,
    SUM(paid_amount) as total_paid
FROM invoices 
WHERE active = true 
GROUP BY status;

-- Driver earnings summary view
CREATE VIEW driver_earnings_summary AS
SELECT 
    payout_status,
    COUNT(*) as count,
    SUM(amount) as total_amount
FROM driver_earnings 
WHERE active = true 
GROUP BY payout_status;

-- Wallet summary view
CREATE VIEW wallet_summary AS
SELECT 
    owner_type,
    COUNT(*) as count,
    SUM(balance) as total_balance
FROM wallets 
WHERE active = true 
GROUP BY owner_type;
```

## Error Handling

### Common Error Scenarios

1. **Payment Failures**: Provider timeout, insufficient funds, network issues
2. **Invoice Overdue**: Automatic status updates and notifications
3. **Payout Failures**: Bank account issues, insufficient balance
4. **Wallet Errors**: Insufficient balance, invalid transaction amounts

### Error Recovery

- **Retry Mechanisms**: Automatic retry for transient failures
- **Manual Intervention**: Admin override for stuck transactions
- **Audit Trail**: Complete error logging for troubleshooting
- **Notifications**: Alert system for critical failures

## Future Enhancements

### Planned Features

1. **Advanced Analytics**: Machine learning for fraud detection
2. **Multi-Currency Support**: Support for USD, EUR transactions
3. **Subscription Billing**: Recurring invoice generation
4. **Tax Integration**: Automatic tax calculation and reporting
5. **Bank Integration**: Direct bank account integration
6. **Mobile App**: Native mobile payment interface

### Scalability Considerations

- **Horizontal Scaling**: Database sharding for high-volume transactions
- **Caching**: Redis caching for frequently accessed data
- **Async Processing**: Message queues for payment processing
- **Microservices**: Service decomposition for independent scaling

## Support & Maintenance

### Regular Maintenance

- **Database Optimization**: Regular index maintenance and cleanup
- **Provider Monitoring**: Health checks for payment providers
- **Security Updates**: Regular security patches and updates
- **Performance Tuning**: Query optimization and monitoring

### Troubleshooting

- **Payment Issues**: Check provider status and transaction logs
- **Invoice Problems**: Verify partner data and delivery records
- **Payout Failures**: Validate driver information and bank details
- **Wallet Errors**: Check transaction history and balance calculations

## Conclusion

The Payment & Billing System provides a robust, scalable foundation for financial operations in the Ethiopian Delivery Platform. With comprehensive Ethiopian payment provider integration, automated billing processes, and secure wallet management, the system supports the complete financial lifecycle of the delivery platform.

The modular architecture ensures easy maintenance and future enhancements, while the comprehensive testing and monitoring capabilities provide confidence in the system's reliability and performance.
