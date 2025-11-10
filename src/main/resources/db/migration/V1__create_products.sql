-- Extension required for exclusion constraints with ranges (if not already created)
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Products table
CREATE TABLE products (
  id BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,
  CONSTRAINT ux_products_name UNIQUE (name)
);

-- Historical prices table
CREATE TABLE product_prices (
  id BIGSERIAL PRIMARY KEY,
  product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  value NUMERIC(12,2) NOT NULL,
  init_date DATE NOT NULL,
  end_date DATE NULL,
  -- Auto-generated range (start included, end included)
  period DATERANGE GENERATED ALWAYS AS (
    daterange(init_date, COALESCE(end_date, 'infinity'::date), '[]')
  ) STORED,
  CONSTRAINT ck_prices_dates CHECK (end_date IS NULL OR end_date >= init_date)
);

-- GIST index for period searches
CREATE INDEX ix_product_period ON product_prices USING gist (product_id, period);

-- Prevents range overlaps for the same product
ALTER TABLE product_prices
  ADD CONSTRAINT ux_product_period EXCLUDE USING gist (
    product_id WITH =,
    period WITH &&
  );
