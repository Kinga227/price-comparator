# Price Comparator API

This Spring Boot application provides a RESTful API for comparing grocery product prices across major Romanian 
supermarket chains (Kaufland, Lidl, Profi). It supports importing product and discount data from CSV files, retrieving
price history, getting personalized alerts, and generating optimized shopping lists based on product alternatives and 
discounts.

---

## Project Structure

```text
src/
├── main/
│   ├── java/edu/accesa/internship/pricecomparator/demo/
│   │   ├── controller/        # REST controllers
│   │   ├── service/           # Business logic services
│   │   ├── model/             # Entity and DTO classes
│   │   ├── repository/        # Spring Data JPA repositories
│   │   └── importer/          # CSV data loader
│   └── resources/
│       ├── application.properties
│       └── data/              # CSV input files
├── test/
│   └── ...                    # Unit and integration tests
```

## How to Build and Run the Application

### Requirements
- Java 17+
- Gradle 7+
- Spring Boot

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew bootRun
```

## Assumptions and Simplifications
- Product data is loaded from CSV files located in `src/main/resources/data/`
- Products are uniquely identified by their `id` and `store` combination
- Discounts are applied only if the current date is within the discount period
- Price history is stored per product and date, including discounted prices if applicable
- No authentication or authorization is implemented (public API)
- The data is assumed to be consistent and correctly formatted in CSVs

## API endpoints

### Optimize Basket
- **POST** `/api/basket/optimize` - Generates a store-wise optimized shopping list using available discounts and 
alternatives

#### Example
**Request body:**
```text
[
    "P001",
    "P002",
    "P006"
]
```

**Response:**
```text
[
    {
        "store": "kaufland",
        "products": [
            {
                "id": "P006",
                "name": "ouă mărimea M",
                "category": "ouă",
                "brand": "Ferma Veche",
                "packageQuantity": 10.0,
                "packageUnit": "buc",
                "currency": "RON",
                "price": 13.55,
                "store": "kaufland"
            }
        ]
    },
    {
        "store": "profi",
        "products": [
            {
                "id": "P001",
                "name": "lapte zuzu",
                "category": "lactate",
                "brand": "Zuzu",
                "packageQuantity": 1.0,
                "packageUnit": "l",
                "currency": "RON",
                "price": 13.4,
                "store": "profi"
            },
            {
                "id": "P002",
                "name": "iaurt grecesc",
                "category": "lactate",
                "brand": "Lidl",
                "packageQuantity": 0.4,
                "packageUnit": "kg",
                "currency": "RON",
                "price": 11.5,
                "store": "profi"
            }
        ]
    }
]
```

### Discounts
- **GET** `/api/discounts` - Get all available discounts
- **GET** `/api/discounts/best` - Get best current discounts
- **GET** `/api/discounts/new` - Get newly started discounts

### Price Alerts
- **GET** `/api/alerts` - List all alerts
- **POST** `/api/alerts` - Create an alert

#### Example
**Request body:**
```text
{
  "productId": "P001",
  "targetPrice": 10.99,
  "userEmail": "user@example.com"
}
```

### Products
- **GET** `/api/products` - List all products
- **GET** `/api/products/{productId}price-history` - Get price history for a specific product
- **GET** `/api/products/price-history` - Get all products' price history
- **GET** `/api/products/{productId}/recommendations` - Get cheaper or alternative products

## Testing

Run unit and integration tests with:
```text
./gradlew test
```

### Includes:
- `DiscountServiceTest`
- `PriceAlertServiceTest`
- `ProductServiceTest`
- `BasketControllerIntegrationTest`
- `DiscountControllerIntegrationTest`
- `PriceAlertControllerIntegrationTest`
- `ProductControllerIntegrationTest`

## Sample CSV Input

CSV files are located under:
```text
src/main/resources/data/
```
Files include product and discount data for Kaufland, Lidl, and Profi across multiple dates.