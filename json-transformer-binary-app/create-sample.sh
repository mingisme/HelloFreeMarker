#!/bin/bash
# Create a sample binary file for testing

# Use Python to create binary data
python3 << 'EOF'
import struct
import time

# IoT sensor data
device_id = 1001
timestamp = int(time.time() * 1000)  # Current time in milliseconds
device_name = "Sensor-Room-A"
temperature = 23.5
humidity = 65.0
pressure = 1013.25
battery = 87
status_flags = 0x09  # online (0x01) + calibrated (0x08)

# Pad device name to 16 bytes
device_name_bytes = device_name.encode('utf-8')[:16].ljust(16, b'\x00')

# Pack data in little-endian format
data = struct.pack('<IQ16sfffBB',
    device_id,          # int32
    timestamp,          # int64
    device_name_bytes,  # 16 bytes string
    temperature,        # float32
    humidity,           # float32
    pressure,           # float32
    battery,            # uint8
    status_flags        # uint8
)

with open('source.bin', 'wb') as f:
    f.write(data)

print(f"Created source.bin with {len(data)} bytes")
print(f"Device ID: {device_id}")
print(f"Device Name: {device_name}")
print(f"Timestamp: {timestamp}")
print(f"Temperature: {temperature}°C")
print(f"Humidity: {humidity}%")
print(f"Pressure: {pressure} hPa")
print(f"Battery: {battery}%")
print(f"Status: 0x{status_flags:02X}")
EOF

chmod +x source.bin
