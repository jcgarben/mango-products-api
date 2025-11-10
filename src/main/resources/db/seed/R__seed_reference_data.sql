-- Insert example products
INSERT INTO products (name, description)
VALUES
  ('Zapatillas deportivas', 'Modelo 2025 edición limitada'),
  ('Camiseta básica', 'Algodón 100% orgánico');

-- Product 1 prices
INSERT INTO product_prices (product_id, value, init_date, end_date)
VALUES
  (1, 10.00, DATE '2022-01-01', DATE '2022-01-31'),
  (1, 20.00, DATE '2022-02-01', DATE '2022-02-28'),
  (1, 30.00, DATE '2022-03-01', NULL);

-- Product 2 prices
INSERT INTO product_prices (product_id, value, init_date, end_date)
VALUES
  (2, 15.00, DATE '2022-01-01', NULL);
