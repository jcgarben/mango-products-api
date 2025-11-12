import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const productCreationDuration = new Trend('product_creation_duration');
const priceCreationDuration = new Trend('price_creation_duration');
const getProductDuration = new Trend('get_product_duration');
const getPricesDuration = new Trend('get_prices_duration');

// Test configuration - Optimized for faster execution (~1 minute)
export const options = {
  stages: [
    { duration: '10s', target: 20 },  // Quick ramp-up to 20 users
    { duration: '20s', target: 50 },  // Increase to 50 users
    { duration: '15s', target: 100 }, // Peak at 100 users (stress test)
    { duration: '10s', target: 20 },  // Cool down to 20
    { duration: '5s', target: 0 },    // Quick ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% of requests should be below 500ms
    http_req_failed: ['rate<0.05'],    // Error rate should be less than 5%
    errors: ['rate<0.05'],
  },
};

const BASE_URL = 'http://app:8080';

// Product names generator
function generateProductName() {
  const categories = ['Camiseta', 'Pantalón', 'Zapatos', 'Chaqueta', 'Jersey'];
  const styles = ['Casual', 'Deportivo', 'Formal', 'Vintage', 'Moderno'];
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000);

  return `${categories[Math.floor(Math.random() * categories.length)]} ${styles[Math.floor(Math.random() * styles.length)]} ${timestamp}-${random}`;
}

// Setup function - runs once at the beginning
export function setup() {
  console.log('🚀 Starting performance test...');
  console.log(`📊 Base URL: ${BASE_URL}`);

  // Wait for application to be ready
  let retries = 30;
  while (retries > 0) {
    try {
      const res = http.get(`${BASE_URL}/actuator/health`);
      if (res.status === 200) {
        console.log('✅ Application is ready!');
        return { ready: true };
      }
    } catch (e) {
      console.log(`⏳ Waiting for application... (${retries} retries left)`);
    }
    sleep(2);
    retries--;
  }

  console.error('❌ Application did not start in time');
  return { ready: false };
}

// Main test scenario
export default function (data) {
  if (!data.ready) {
    return;
  }

  // Scenario 1: Create Product (30% of traffic)
  if (Math.random() < 0.3) {
    const productPayload = JSON.stringify({
      name: generateProductName(),
      description: 'Producto de prueba de rendimiento'
    });

    const createProductRes = http.post(
      `${BASE_URL}/products`,
      productPayload,
      {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'CreateProduct' },
      }
    );

    productCreationDuration.add(createProductRes.timings.duration);

    const createProductCheck = check(createProductRes, {
      'product created': (r) => r.status === 201,
      'product has id': (r) => JSON.parse(r.body).id !== undefined,
    });

    errorRate.add(!createProductCheck);

    if (createProductRes.status === 201) {
      const productId = JSON.parse(createProductRes.body).id;

      // Add price to the created product
      const currencies = ['EUR', 'USD', 'GBP'];
      const randomCurrency = currencies[Math.floor(Math.random() * currencies.length)];

      const pricePayload = JSON.stringify({
        value: Math.floor(Math.random() * 200) + 10,
        currency: randomCurrency,
        initDate: '2025-01-01',
        endDate: '2025-12-31'
      });

      const createPriceRes = http.post(
        `${BASE_URL}/products/${productId}/prices`,
        pricePayload,
        {
          headers: { 'Content-Type': 'application/json' },
          tags: { name: 'CreatePrice' },
        }
      );

      priceCreationDuration.add(createPriceRes.timings.duration);

      const createPriceCheck = check(createPriceRes, {
        'price created': (r) => r.status === 201,
        'price has value': (r) => JSON.parse(r.body).value !== undefined,
      });

      errorRate.add(!createPriceCheck);
    }
  }

  // Scenario 2: Get Product by ID (40% of traffic)
  else if (Math.random() < 0.7) {
    // Use a random ID between 1 and 100 (will include 404s which is realistic)
    const productId = Math.floor(Math.random() * 100) + 1;

    const getProductRes = http.get(
      `${BASE_URL}/products/${productId}`,
      { tags: { name: 'GetProduct' } }
    );

    getProductDuration.add(getProductRes.timings.duration);

    const getProductCheck = check(getProductRes, {
      'product retrieved or not found': (r) => r.status === 200 || r.status === 404,
    });

    errorRate.add(!getProductCheck);
  }

  // Scenario 3: Get Product Prices (30% of traffic)
  else {
    const productId = Math.floor(Math.random() * 50) + 1;

    const getPricesRes = http.get(
      `${BASE_URL}/products/${productId}/prices`,
      { tags: { name: 'GetPrices' } }
    );

    getPricesDuration.add(getPricesRes.timings.duration);

    const getPricesCheck = check(getPricesRes, {
      'prices retrieved or not found': (r) => r.status === 200 || r.status === 404,
    });

    errorRate.add(!getPricesCheck);

    // Query current price by date (50% of the time)
    if (Math.random() < 0.5) {
      const queryDate = '2025-06-15';

      // 50% with currency filter, 50% without
      let queryUrl = `${BASE_URL}/products/${productId}/prices?date=${queryDate}`;
      if (Math.random() < 0.5) {
        const currencies = ['EUR', 'USD', 'GBP'];
        const randomCurrency = currencies[Math.floor(Math.random() * currencies.length)];
        queryUrl += `&currency=${randomCurrency}`;
      }

      const getCurrentPriceRes = http.get(
        queryUrl,
        { tags: { name: 'GetCurrentPrice' } }
      );

      check(getCurrentPriceRes, {
        'current price retrieved or not found': (r) => r.status === 200 || r.status === 404,
      });
    }

    // Query price history by currency (20% of the time)
    if (Math.random() < 0.2) {
      const currencies = ['EUR', 'USD', 'GBP'];
      const randomCurrency = currencies[Math.floor(Math.random() * currencies.length)];

      const getPricesByCurrencyRes = http.get(
        `${BASE_URL}/products/${productId}/prices?currency=${randomCurrency}`,
        { tags: { name: 'GetPricesByCurrency' } }
      );

      check(getPricesByCurrencyRes, {
        'prices by currency retrieved or not found': (r) => r.status === 200 || r.status === 404,
      });
    }
  }

  sleep(0.1); // Small pause between requests
}

// Teardown function - runs once at the end
export function teardown(data) {
  console.log('🏁 Performance test completed!');
}

