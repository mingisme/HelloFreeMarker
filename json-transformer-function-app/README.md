# JSON Transformer with Custom Functions

This module demonstrates custom FreeMarker functions with Guava cache support.

## Custom Functions

### lookup(key)
Cache-based lookup function for converting IDs to names. Uses Guava LoadingCache with:
- Max size: 100 entries
- Expiration: 10 minutes after write
- Example: `${lookup(data.deviceId)}` → "Temperature Sensor A"

### convert(value, fromUnit, toUnit)
Unit conversion function supporting:
- Temperature: celsius ↔ fahrenheit
- Example: `${convert(23.5, 'celsius', 'fahrenheit')}` → 74.3

### format(value, format)
String formatting function:
- "uppercase" - Convert to uppercase
- "lowercase" - Convert to lowercase
- "capitalize" - Capitalize first letter
- Example: `${format(data.location, 'uppercase')}` → "WAREHOUSE-A"

## Build & Run

```bash
# Build from root
mvn clean package

# Run
cd json-transformer-function-app
java -jar target/json-transformer-function-app-1.0-SNAPSHOT.jar template.ftl source.json target.json
cat target.json
```

---

## Built-in FreeMarker Features

### Data Type Conversion

**String to Number:**
```ftl
${"123"?number}
```
**Output:** `123`

**Number to String:**
```ftl
${123?string}
```
**Output:** `"123"`

**Boolean:**
```ftl
${"true"?boolean}
${value?string("yes", "no")}
```

---

### Date/Time Formatting

**Convert Unix timestamp (milliseconds) to date:**
```ftl
${data.timestamp?number_to_datetime?string("yyyy-MM-dd HH:mm:ss")}
```
**Output:** `"2024-10-24 12:00:00"`

**ISO 8601 format:**
```ftl
${data.timestamp?number_to_datetime?iso_utc}
```
**Output:** `"2024-10-24T12:00:00Z"`

**Custom format:**
```ftl
${data.timestamp?number_to_datetime?string("yyyy-MM-dd'T'HH:mm:ss'Z'")}
```

**Complete example:**
```ftl
{
  "timestamp": ${data.timestamp},
  "formattedTime": "${data.timestamp?number_to_datetime?string("yyyy-MM-dd HH:mm:ss")}",
  "isoTime": "${data.timestamp?number_to_datetime?iso_utc}"
}
```

---

## Extending

Add new custom functions by:

1. **Implement `TemplateMethodModelEx`:**
   ```java
   static class MyFunction implements TemplateMethodModelEx {
       @Override
       public Object exec(List arguments) throws TemplateModelException {
           // Your logic here
           return result;
       }
   }
   ```

2. **Register in `FunctionApp.java`:**
   ```java
   dataModel.put("myFunction", new MyFunction());
   ```

3. **Use in templates:**
   ```ftl
   ${myFunction(arg1, arg2)}
   ```