-- Clean existing seed data (idempotent script)
DELETE FROM product_prices;
DELETE FROM products;

-- Insert example products
INSERT INTO products (id, name, description)
VALUES
  (1, 'Zapatillas deportivas', 'Modelo 2025 edición limitada'),
  (2, 'Camiseta básica', 'Algodón 100% orgánico');

-- Set sequence to start from 3 so k6 benchmark can create products without collision
SELECT setval('products_id_seq', 3, false);

-- Product 1 prices (historical data example)
INSERT INTO product_prices (product_id, value, init_date, end_date)
VALUES
  (1, 10.00, DATE '2022-01-01', DATE '2022-01-31'),
  (1, 20.00, DATE '2022-02-01', DATE '2022-02-28'),
  (1, 30.00, DATE '2022-03-01', NULL);

-- Product 2 prices
INSERT INTO product_prices (product_id, value, init_date, end_date)
VALUES
  (2, 15.00, DATE '2022-01-01', NULL);
