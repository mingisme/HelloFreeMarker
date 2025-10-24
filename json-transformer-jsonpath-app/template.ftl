{
  "device": {
    "id": "${jsonPath('$.deviceId')}",
    "firmware": "${jsonPath('$.metadata.firmware')}",
    "batteryLevel": ${jsonPath('$.metadata.battery')}
  },
  "measurements": {
    "temperatureReadings": ${jsonPath('$.sensors[?(@.type=="temperature")].value')},
    "temperatureLocations": ${jsonPath('$.sensors[?(@.type=="temperature")].location')},
    "firstTemperature": ${jsonPath('$.sensors[?(@.type=="temperature")].value[0]')},
    "humidityValue": ${jsonPath('$.sensors[?(@.type=="humidity")].value[0]')},
    "allSensorTypes": ${jsonPath('$.sensors[*].type')},
    "sensorCount": ${jsonPath('$.sensors.length()')}
  },
  "timestamp": ${jsonPath('$.timestamp')}
}
