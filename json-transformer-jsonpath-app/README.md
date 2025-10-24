# JSON Transformer with JSONPath Support

This module extends FreeMarker templates with JSONPath query capabilities for dynamic JSON transformations.

## Features

- **JSONPath queries** - Use `${jsonPath('$.path.to.field')}` in templates
- **Filtering** - `$.sensors[?(@.type=="temperature")]` to filter arrays
- **Wildcards** - `$.sensors[*].type` to get all values
- **Array functions** - `.length()`, array indexing

## Example

The template uses JSONPath to:
- Extract specific sensor readings by type
- Filter temperature sensors dynamically
- Get first matching values
- Count array elements

## Build & Run

```bash
# Build from root
mvn clean package

# Run
cd json-transformer-jsonpath-app
java -jar target/json-transformer-jsonpath-app-1.0-SNAPSHOT.jar template.ftl source.json target.json
cat target.json
```

## JSONPath Syntax

- `$.field` - Root level field
- `$.nested.field` - Nested field
- `$.array[0]` - Array index
- `$.array[*]` - All array elements
- `$.array[?(@.field=="value")]` - Filter by condition
- `$.array.length()` - Array length
