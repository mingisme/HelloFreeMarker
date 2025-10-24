{
  "device": {
    "id": ${readInt(0)},
    "name": "${readString(12, 16)}",
    "battery": ${readByte(40)},
    "status": {
      "online": ${readBit(41, 0)?c},
      "lowBattery": ${readBit(41, 1)?c},
      "error": ${readBit(41, 2)?c},
      "calibrated": ${readBit(41, 3)?c}
    }
  },
  "measurements": {
    "temperature": {
      "value": ${readFloat(28)},
      "unit": "celsius"
    },
    "humidity": {
      "value": ${readFloat(32)},
      "unit": "percent"
    },
    "pressure": {
      "value": ${readFloat(36)},
      "unit": "hPa"
    }
  },
  "timestamp": ${readLong(4)},
  "formattedTime": "${readLong(4)?number_to_datetime?string("yyyy-MM-dd HH:mm:ss")}"
}
