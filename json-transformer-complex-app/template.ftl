{
  "organizationReport": {
    "companyName": "${company.name}",
    "age": ${2025 - company.founded},
    "totalDepartments": ${company.departments?size},
    "reportDate": "${company.metadata.lastUpdated}",
    "employees": [
      {
        "employeeId": "EMP-${company.departments[0].employees[0].id?string("000")}",
        "fullName": "${company.departments[0].employees[0].name?upper_case}",
        "department": "${company.departments[0].name}",
        "position": "${company.departments[0].employees[0].role}",
        "skillCount": ${company.departments[0].employees[0].skills?size},
        "primarySkill": "${company.departments[0].employees[0].skills[0]}",
        "allSkills": "${company.departments[0].employees[0].skills?join(", ")}",
        "salaryFormatted": "$${company.departments[0].employees[0].salary?string(",###")}"
      },
      {
        "employeeId": "EMP-${company.departments[1].employees[0].id?string("000")}",
        "fullName": "${company.departments[1].employees[0].name?upper_case}",
        "department": "${company.departments[1].name}",
        "position": "${company.departments[1].employees[0].role}",
        "skillCount": ${company.departments[1].employees[0].skills?size},
        "primarySkill": "${company.departments[1].employees[0].skills[0]}",
        "allSkills": "${company.departments[1].employees[0].skills?join(", ")}",
        "salaryFormatted": "$${company.departments[1].employees[0].salary?string(",###")}"
      }
    ]
  }
}
