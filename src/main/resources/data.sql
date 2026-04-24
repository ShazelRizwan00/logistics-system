-- =============================================================================
-- Seed data for Logistics & Delivery Tracking System
-- Compatible with H2 (dev) and PostgreSQL (prod).
--
-- Passwords are BCrypt-hashed. The plaintext for all accounts is "password123"
-- Hash generated with BCryptPasswordEncoder(12).
-- =============================================================================

-- -----------------------------------------------------------------------------
-- USERS (single table — discriminator column: role)
-- -----------------------------------------------------------------------------
INSERT INTO users (user_id, name, contact_info, password, role, address, vehicle_info, availability_status, created_at, updated_at)
VALUES
  -- Admin
  (1, 'Super Admin',     'admin@logistics.com',  '$2a$12$aNaUoq8Xsq8hbg5337gxsurGKylqLR9.4tpAw1vOQJPDFOW4aNt0.', 'ADMIN',          NULL,                  NULL,             NULL,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Customers
  (2, 'Alice Johnson',   'alice@example.com',    '$2a$12$aNaUoq8Xsq8hbg5337gxsurGKylqLR9.4tpAw1vOQJPDFOW4aNt0.', 'CUSTOMER',       '12 Maple Ave, Lahore', NULL,            NULL,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'Bob Smith',       'bob@example.com',      '$2a$12$aNaUoq8Xsq8hbg5337gxsurGKylqLR9.4tpAw1vOQJPDFOW4aNt0.', 'CUSTOMER',       '9 Oak St, Karachi',   NULL,             NULL,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Delivery Agents
  (4, 'Carlos Rivera',   'carlos@logistics.com', '$2a$12$aNaUoq8Xsq8hbg5337gxsurGKylqLR9.4tpAw1vOQJPDFOW4aNt0.', 'DELIVERY_AGENT', NULL,                  'Toyota Hiace PKL-001', TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (5, 'Diana Patel',     'diana@logistics.com',  '$2a$12$aNaUoq8Xsq8hbg5337gxsurGKylqLR9.4tpAw1vOQJPDFOW4aNt0.', 'DELIVERY_AGENT', NULL,                  'Honda CD-70 PKL-002',  TRUE,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- ORDERS
-- -----------------------------------------------------------------------------
INSERT INTO orders (order_id, order_date, status, customer_id, created_at, updated_at)
VALUES
  (1, DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'SHIPPED',    2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'CONFIRMED',  2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'PENDING',    3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- PAYMENTS
-- -----------------------------------------------------------------------------
INSERT INTO payments (payment_id, amount, method, status, order_id, created_at, updated_at)
VALUES
  (1, 2500.00, 'CREDIT_CARD',      'COMPLETED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2,  850.50, 'BANK_TRANSFER',    'COMPLETED', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 1200.00, 'CASH_ON_DELIVERY', 'PENDING',   3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- SHIPMENTS
-- -----------------------------------------------------------------------------
INSERT INTO shipments (shipment_id, origin, destination, status, order_id, created_at, updated_at)
VALUES
  (1, 'Warehouse A - Lahore',   'Alice Johnson, 12 Maple Ave, Lahore',   'OUT_FOR_DELIVERY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 'Warehouse B - Karachi',  'Alice Johnson, 12 Maple Ave, Lahore',   'IN_TRANSIT',       1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 'Warehouse A - Lahore',   'Alice Johnson, 12 Maple Ave, Lahore',   'CREATED',          2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 'Warehouse C - Islamabad','Bob Smith, 9 Oak St, Karachi',          'CREATED',          3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- PACKAGES
-- -----------------------------------------------------------------------------
INSERT INTO packages (package_id, weight, dimensions, description, shipment_id, created_at, updated_at)
VALUES
  (1, 2.5,  '30x20x15', 'Electronics - Laptop',          1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (2, 0.8,  '20x15x10', 'Accessories - Mouse & Keyboard', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (3, 5.0,  '50x40x30', 'Clothing Bundle',                2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (4, 1.2,  '25x20x12', 'Books x3',                       3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  (5, 3.7,  '40x30x25', 'Kitchen Appliance',              4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- DELIVERIES
-- -----------------------------------------------------------------------------
INSERT INTO deliveries (delivery_id, delivery_status, delivery_time, shipment_id, agent_id, created_at, updated_at)
VALUES
  (1, 'EN_ROUTE', NULL, 1, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- -----------------------------------------------------------------------------
-- TRACKING INFO (chronological events for shipment 1)
-- -----------------------------------------------------------------------------
INSERT INTO tracking_info (tracking_id, current_location, timestamp, status, shipment_id, created_at)
VALUES
  (1, 'Warehouse A - Lahore',           DATEADD('DAY',  -5, CURRENT_TIMESTAMP), 'CREATED',          1, CURRENT_TIMESTAMP),
  (2, 'Lahore Sorting Hub',             DATEADD('DAY',  -4, CURRENT_TIMESTAMP), 'PICKED_UP',        1, CURRENT_TIMESTAMP),
  (3, 'Motorway M-2 Transit',           DATEADD('DAY',  -3, CURRENT_TIMESTAMP), 'IN_TRANSIT',       1, CURRENT_TIMESTAMP),
  (4, 'Lahore Distribution Centre',     DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 'OUT_FOR_DELIVERY', 1, CURRENT_TIMESTAMP);

-- Keep H2 identity generators ahead of explicitly seeded primary keys.
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 6;
ALTER TABLE orders ALTER COLUMN order_id RESTART WITH 4;
ALTER TABLE payments ALTER COLUMN payment_id RESTART WITH 4;
ALTER TABLE shipments ALTER COLUMN shipment_id RESTART WITH 5;
ALTER TABLE packages ALTER COLUMN package_id RESTART WITH 6;
ALTER TABLE deliveries ALTER COLUMN delivery_id RESTART WITH 2;
ALTER TABLE tracking_info ALTER COLUMN tracking_id RESTART WITH 5;
