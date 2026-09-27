# API Test Automation Framework

Cucumber + TestNG + REST Assured framework for API testing with reusable utilities and Excel-driven test data.

## Tech Stack

- **Java 11+**
- **Cucumber JVM** – BDD test orchestration
- **TestNG** – Test runner and assertions
- **REST Assured** – HTTP client and response validation
- **Apache POI** – Excel test data management
- **Jackson** – JSON serialization/deserialization
- **Maven** – Build and dependency management

## Project Structure

```text
src/test/
├── java/
│   ├── stepDefinitions/          # Cucumber step definitions
│   ├── utils/                    # Reusable utilities
│   ├── pojo/                     # Request/Response DTOs
│   └── hooks/                    # Cucumber hooks
├── resources/
│   ├── features/                 # Gherkin feature files
│   ├── schemas/                  # JSON Schema files
│   └── test-data/                # Excel test data files
└── testng.xml                    # TestNG suite configuration
```

## Utilities

| Utility | Purpose |
|---------|---------|
| `ApiExecutor` | Execute HTTP requests (GET/POST/PUT/DELETE) |
| `RequestBuilder` | Build `RequestSpecification` with auth, path params, body |
| `ResponseValidator` | Validate status codes, schemas, error messages |
| `BatchResponseValidator` | Batch-specific response assertions |
| `BatchRequestUtil` | Build/modify Batch JSON payloads |
| `ExcelReader` | Low-level Excel row/sheet access |
| `ExcelDataUtil` | High-level Excel helpers with validation |
| `ScenarioContext` | Per-scenario state (request, response, Excel data) |
| `SharedTestData` | Cross-scenario shared data (IDs, names) |
| `TestDataUtil` | Generate unique test data (names, suffixes) |
| `ScenarioUtil` | Interpret scenario names (e.g., NoAuth detection) |
| `BatchPrerequisiteUtil` | Validate required Program/Batch data |
| `ContextKeys` | Centralized `ScenarioContext` key constants |

## Running Tests

### Prerequisites

1. Java 11+ installed
2. Maven installed
3. `TestData.xlsx` in `src/test/resources/test-data/`
4. Base URL and auth configured in `TestHooks.java` or `RequestBuilder.java`

### Run All Tests

```bash
mvn test
```

### Run Specific Feature

```bash
mvn test -Dcucumber.filter.tags="@Batch"
```

### Run Specific Scenario

```bash
mvn test -Dcucumber.filter.name="Admin receives success code with response body"
```

## Adding a New Test Scenario

### 1. Add Test Data to Excel

Open `TestData.xlsx`, navigate to the appropriate sheet (e.g., `Batch`), and add a new row:

| ScenarioName | Endpoint | Body | ExpectedStatusCode |
|--------------|----------|------|--------------------|
| GetBatchByProgram_InvalidProgramId | `/api/programs/{programId}/batches` | - | 404 |

### 2. Add Gherkin Scenario

In `src/test/resources/features/ProgramBatch.feature`:

```gherkin
Scenario: Admin receives 404 for invalid programId
  Given Admin create GET request by programId with invalid input for "GetBatchByProgram_InvalidProgramId" from excel sheet
  When Admin sends GET request to retrieve batches by programId
  Then Admin receives expected status code with error message
```

### 3. Run the Test

```bash
mvn test -Dcucumber.filter.name="Admin receives 404 for invalid programId"
```

## Best Practices

- Use `ExcelDataUtil` for all Excel access (not direct `ExcelReader` calls in StepDefs)
- Use `ContextKeys` constants for `ScenarioContext` keys
- Use `BatchPrerequisiteUtil` to validate required data before scenarios
- Keep StepDefs focused on orchestration; move logic to utilities
- Use `ScenarioUtil.isNoAuthScenario()` for NoAuth detection
- Validate responses with `ResponseValidator` (generic) and `BatchResponseValidator` (specific)

## Troubleshooting

**Excel Data Not Found** – Verify `ScenarioName` in Excel matches the Gherkin step exactly (case-insensitive) and `TestData.xlsx` is in the correct location.

**SharedTestData is Null/Invalid** – Ensure prerequisite scenarios (e.g., Create Batch) run before dependent scenarios. Use `BatchPrerequisiteUtil.requireValidBatch()`.

**Schema Validation Fails** – Verify the schema file exists and the API response structure matches the schema (field names, types, nesting).

## License

Internal use only.
