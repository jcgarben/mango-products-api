-- Add currency column (ISO 4217 code: EUR, USD, GBP, etc.)
ALTER TABLE product_prices
  ADD COLUMN currency VARCHAR(3) NOT NULL DEFAULT 'EUR';

-- Remove default value after applying it (to make it truly required for new inserts)
ALTER TABLE product_prices
  ALTER COLUMN currency DROP DEFAULT;

-- Drop old constraint that doesn't consider currency
ALTER TABLE product_prices
  DROP CONSTRAINT ux_product_period;

-- Create new constraint: overlap prevention PER currency
-- This allows EUR and USD to exist for the same period, but not EUR-EUR overlap
ALTER TABLE product_prices
  ADD CONSTRAINT ux_product_period_currency EXCLUDE USING gist (
    product_id WITH =,
    currency WITH =,
    period WITH &&
  );

-- Index for queries filtering by currency
CREATE INDEX ix_product_prices_currency ON product_prices(currency);

-- Composite index for common query pattern (product + currency)
CREATE INDEX ix_product_prices_product_currency ON product_prices(product_id, currency);

-- Constraint to validate ISO 4217 format (3 uppercase letters)
ALTER TABLE product_prices
  ADD CONSTRAINT ck_currency_iso4217 CHECK (currency ~ '^[A-Z]{3}$');

-- Comment for documentation
COMMENT ON COLUMN product_prices.currency IS 'Currency code following ISO 4217 standard (e.g., EUR, USD, GBP)';

