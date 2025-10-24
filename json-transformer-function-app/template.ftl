{
  "device": {
    "id": "${data.deviceId}",
    "name": "${lookup(data.deviceId)}",
    "location": "${format(data.location, 'uppercase')}"
  },
  "measurements": {
    "temperatureCelsius": ${data.temperature.value},
    "temperatureFahrenheit": ${convert(data.temperature.value, 'celsius', 'fahrenheit')?round},
    "unit": "${data.temperature.unit}"
  },
  "timestamp": ${data.timestamp}
}
