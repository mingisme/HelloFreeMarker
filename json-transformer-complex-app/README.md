# Complex FreeMarker Example

This example demonstrates advanced FreeMarker features:

## Features Used

- **Nested object access**: `company.name`, `company.metadata.lastUpdated`
- **Array iteration**: `<#list company.departments as dept>`
- **Array size**: `company.departments?size`
- **Arithmetic operations**: `2025 - company.founded`
- **Map function**: `dept.employees?map(e -> e.salary)`
- **Sum aggregation**: `?sum`
- **Rounding**: `?round`
- **String formatting**: `?string("000")`, `?string(",###")`
- **String manipulation**: `?upper_case`, `?join(", ")`
- **Array indexing**: `emp.skills[0]`
- **Conditional output**: `<#if emp?has_next>,</#if>`
- **Flatten nested arrays**: `?flatten`
- **Sorting**: `?sort_by("salary")?reverse`

## Run

```bash
cd json-transformer-complex-app
java -jar ../json-transformer-app/target/json-transformer-app-1.0-SNAPSHOT.jar template.ftl source.json target.json
cat target.json
```
