## Overview

Avro is an open-source data serialization system for defining schemas and performing efficient binary serialization and deserialization, with schema evolution support for compatibility as data systems change. This module provides the capability to serialize and deserialize data using Avro schemas.

## Key Features

- Schema-based binary serialization and deserialization
- Schema evolution support for compatibility across versions
- Convert Ballerina values to and from Avro-encoded binary data

## Schema

The `Schema` instance accepts an Avro schema in `string` format. If the provided schema is not valid according to Avro, an error is returned. The client can be used to serialize data into bytes using the defined schema and deserialize the bytes back to the correct data type based on the schema.

A `Schema` can be defined using the `string` value of an Avro schema as shown below.

```ballerina
avro:Schema schema = check new(string `{"type": "int", "namespace": "example.data" }`);
```

### APIs associated with Avro

- **toAvro**: Serializes the given data according to the Avro format.
- **fromAvro**: Deserializes the given Avro encoded message to the given data type.

#### `toAvro`

Serializes the given data according to the Avro format.

```ballerina
import ballerina/avro;

public function main() returns error? {
    int value = 5;
    byte[] serializedData = check schema.toAvro(value);
}
```

#### `fromAvro`

Deserializes the given Avro encoded message to the given data type.

```ballerina
import ballerina/avro;

public function main() returns error? {
    byte[] data = // Avro encoded message ;
    int deserializedData = check schema.fromAvro(data);
}
```
