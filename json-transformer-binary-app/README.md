# JSON Transformer for Binary Data

This module extracts data from binary files and transforms them to JSON using FreeMarker templates.

## Binary Protocol Format

The parser expects IoT sensor data in the following format (42 bytes total):

| Offset | Size | Type    | Description                    |
|--------|------|---------|--------------------------------|
| 0-3    | 4    | int32   | Device ID (little-endian)      |
| 4-11   | 8    | int64   | Timestamp in ms (little-endian)|
| 12-27  | 16   | string  | Device name (UTF-8, padded)    |
| 28-31  | 4    | float32 | Temperature in °C              |
| 32-35  | 4    | float32 | Humidity in %                  |
| 36-39  | 4    | float32 | Pressure in hPa                |
| 40     | 1    | uint8   | Battery level (0-100%)         |
| 41     | 1    | uint8   | Status flags (bitmask)         |

### Status Flags (Byte 41)

- Bit 0 (0x01): Online
- Bit 1 (0x02): Low battery
- Bit 2 (0x04): Error
- Bit 3 (0x08): Calibrated

## Create Sample Binary File

```bash
cd json-transformer-binary-app
chmod +x create-sample.sh
./create-sample.sh
```

This creates a `source.bin` file with sample IoT sensor data.

## Build & Run

**Build from root:**
```bash
mvn clean package
```

**Run the transformer:**
```bash
cd json-transformer-binary-app
java -jar target/json-transformer-binary-app-1.0-SNAPSHOT.jar template.ftl source.bin target.json
cat target.json
```

## Binary Extraction Functions

The template has access to these functions to extract data from binary:

### `readInt(offset)`
Read 4-byte int32 (little-endian) from offset
```ftl
${readInt(0)}  <#-- Read int32 at byte 0 -->
```

### `readLong(offset)`
Read 8-byte int64 (little-endian) from offset
```ftl
${readLong(4)}  <#-- Read int64 at byte 4 -->
```

### `readFloat(offset)`
Read 4-byte float32 (little-endian) from offset
```ftl
${readFloat(12)}  <#-- Read float32 at byte 12 -->
```

### `readByte(offset)`
Read 1-byte unsigned integer from offset
```ftl
${readByte(24)}  <#-- Read uint8 at byte 24 -->
```

### `readBit(offset, bitPosition)`
Read specific bit from byte at offset
```ftl
${readBit(25, 0)?c}  <#-- Read bit 0 from byte 25 -->
```

### `readString(offset, length, [encoding])`
Read string from offset with specified length. Encoding defaults to UTF-8.
```ftl
${readString(0, 16)}  <#-- Read 16-byte UTF-8 string at byte 0 -->
${readString(0, 16, "ASCII")}  <#-- Read 16-byte ASCII string -->
```

### `readShort(offset)`
Read 2-byte int16 (little-endian) from offset
```ftl
${readShort(0)}  <#-- Read int16 at byte 0 -->
```

### `readDouble(offset)`
Read 8-byte float64 (little-endian) from offset
```ftl
${readDouble(0)}  <#-- Read double at byte 0 -->
```

### `readUnsignedInt(offset)`
Read 4-byte unsigned int32 (little-endian) from offset
```ftl
${readUnsignedInt(0)}  <#-- Read uint32 at byte 0 -->
```

### `readIntBE(offset)`, `readLongBE(offset)`, `readFloatBE(offset)`
Read big-endian (network byte order) integers and floats
```ftl
${readIntBE(0)}  <#-- Read big-endian int32 -->
```

### `readBytes(offset, length)`
Read raw bytes as JSON array
```ftl
${readBytes(0, 6)}  <#-- Read 6 bytes as [255, 192, 168, 1, 100, 0] -->
```

### `readHex(offset, length)`
Read bytes as hex string (useful for MAC addresses, UUIDs)
```ftl
"mac": "${readHex(0, 6)}"  <#-- Read MAC address as "FFC0A8016400" -->
```

### `readBoolean(offset)`
Read byte as boolean (0=false, non-zero=true)
```ftl
${readBoolean(0)?c}  <#-- Read boolean at byte 0 -->
```

## Example Template

```ftl
{
  "deviceId": ${readInt(0)},
  "timestamp": ${readLong(4)},
  "temperature": ${readFloat(12)},
  "battery": ${readByte(24)},
  "isOnline": ${readBit(25, 0)?c}
}
```

This approach is completely generic - just change the template to match your binary format!

## Use Cases

- IoT sensor data transformation
- Binary protocol to JSON conversion
- Legacy system integration
- Embedded device data extraction
