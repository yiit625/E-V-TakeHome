# Engel & Völkers — Listing Matcher API

A Spring Boot REST API that manages property listings and matches them to customer search criteria using a configurable scoring engine.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.4.5, Java 21 |
| Database | H2 (in-memory) |
| ORM | Spring Data JPA / Hibernate |
| Validation | Jakarta Bean Validation |
| Documentation | Springdoc OpenAPI / Swagger UI |
| Utilities | Lombok, Spring Boot Actuator |

---

## Running the Application

```bash
./mvnw spring-boot:run
```

---

## URLs

| Tool | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

**H2 Console connection:**
- JDBC URL: `jdbc:h2:mem:evdb`
- Username: `sa`
- Password: *(leave empty)*

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/listings` | Create a new property listing |
| `POST` | `/api/properties/search` | Search and rank active listings by similarity |

---

## Scoring Engine

Each active listing is scored against the customer's search criteria across three dimensions:

| Criterion | Score Logic |
|---|---|
| Price | `max(0, 1 - │propertyPrice - targetPrice│ / targetPrice)` |
| Rooms | `1.0` if meets minimum, otherwise `propertyRooms / minRooms` |
| Space | `1.0` if meets minimum, otherwise `propertySquareMeters / minSquareMeters` |

**Final score** = weighted average of the three scores. Weights are configurable via `application.yaml`:

```yaml
scoring:
  weights:
    price: 1.0
    rooms: 1.0
    space: 1.0
```

---

## Demo Scenarios

### Step 1 — Create listings via `POST /api/listings`

**1. Perfect Match**
```json
{
  "title": "Perfect Match Penthouse",
  "description": "Exactly matches all search criteria",
  "price": 500000,
  "zipCode": "20095",
  "numberOfRooms": 4,
  "squareMeters": 100.0
}
```

**2. Over Budget**
```json
{
  "title": "Over Budget Villa",
  "description": "Price is 2x the target — price score will be 0",
  "price": 1000000,
  "zipCode": "20095",
  "numberOfRooms": 4,
  "squareMeters": 100.0
}
```

**3. Small Rooms**
```json
{
  "title": "Small Rooms Apartment",
  "description": "Only 2 rooms against a minimum of 4 — rooms score will be 0.5",
  "price": 500000,
  "zipCode": "20095",
  "numberOfRooms": 2,
  "squareMeters": 100.0
}
```

**4. Small Space**
```json
{
  "title": "Small Space Studio",
  "description": "Only 50 sqm against a minimum of 100 — space score will be 0.5",
  "price": 500000,
  "zipCode": "20095",
  "numberOfRooms": 4,
  "squareMeters": 50.0
}
```

---

### Step 2 — Activate listings via H2 Console

Listings are created with `PREPARATION` status. Run this SQL in the H2 Console to activate all of them:

```sql
UPDATE PROPERTY_LISTING_ENTITY SET STATUS = 'ACTIVE';
```

---

### Step 3 — Search via `POST /api/properties/search`

```json
{
  "targetPrice": 500000,
  "minRooms": 4,
  "minSquareMeters": 100.0
}
```

---

### Expected Results (sorted by score descending)

| Listing | Price Score | Rooms Score | Space Score | **Total Score** |
|---|---|---|---|---|
| Perfect Match Penthouse | 1.000 | 1.000 | 1.000 | **1.000** |
| Small Rooms Apartment | 1.000 | 0.500 | 1.000 | **0.833** |
| Small Space Studio | 1.000 | 1.000 | 0.500 | **0.833** |
| Over Budget Villa | 0.000 | 1.000 | 1.000 | **0.667** |

---

## Seeded Agents

Three agents are automatically seeded on startup and are automatically assigned to listings by ZIP code:

| Agent | ZIP Code |
|---|---|
| Anna Müller | 20095 (Hamburg) |
| Ben Schmidt | 10115 (Berlin) |
| Clara Weber | 80331 (Munich) |

---

## Running Tests

```bash
./mvnw test
```

25 tests across unit, slice, and integration layers.
