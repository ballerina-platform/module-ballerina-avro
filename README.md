# Ballerina Avro Module

[![Build](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-timestamped-master.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-timestamped-master.yml)
[![codecov](https://codecov.io/gh/ballerina-platform/module-ballerina-avro/branch/main/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerina-avro)
[![Trivy](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/trivy-scan.yml)
[![GraalVM Check](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-with-bal-test-graalvm.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-with-bal-test-graalvm.yml)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerina-avro.svg)](https://github.com/ballerina-platform/module-ballerina-avro/commits/main)
[![Github issues](https://img.shields.io/github/issues/ballerina-platform/ballerina-standard-library/module/avro.svg?label=Open%20Issues)](https://github.com/ballerina-platform/ballerina-standard-library/labels/module%2Favro)

## Overview

Avro is an open-source data serialization system for defining schemas and
performing efficient binary serialization and deserialization, with schema
evolution support for compatibility as data systems change. This module
provides the capability to serialize and deserialize data using Avro schemas.

## Schema

The `Schema` instance accepts an Avro schema in `string` format. If the provided schema is not valid according to Avro, an error is returned. The client can be used to serialize data into bytes using the defined schema and deserialize the bytes back to the correct data type based on the schema.

A `Schema` can be defined using the `string` value of an Avro schema as shown below.

```ballerina
avro:Schema schema = check new(string `{"type": "int", "namespace": "example.data" }`);
```

## APIs associated with Avro

- **toAvro**: Serializes the given data according to the Avro format.
- **fromAvro**: Deserializes the given Avro encoded message to the given data type.

### `toAvro`

Serializes the given data according to the Avro format.

```ballerina
import ballerina/avro;

public function main() returns error? {
    int value = 5;
    byte[] serializedData = check schema.toAvro(value);
}
```

### `fromAvro`

Deserializes the given Avro encoded message to the given data type.

```ballerina
import ballerina/avro;

public function main() returns error? {
    byte[] data = // Avro encoded message ;
    int deserializedData = check schema.fromAvro(data);
}
```

## Issues and projects

The **Issues** and **Projects** tabs are disabled for this repository as this is part of the Ballerina library. To report bugs, request new features, start new discussions, view project boards, etc., visit the Ballerina library [parent repository](https://github.com/ballerina-platform/ballerina-library).

This repository only contains the source code for the package.

## Building from the source

### Prerequisites

1. Download and install Java SE Development Kit (JDK) version 21. You can download it from either of the following sources:

   - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - [OpenJDK](https://adoptium.net/)

    > **Note:** After installation, remember to set the `JAVA_HOME` environment variable to the directory where JDK was installed.

2. Download and install [Ballerina Swan Lake](https://ballerina.io/).

3. Download and install [Docker](https://www.docker.com/get-started).

    > **Note**: Ensure that the Docker daemon is running before executing any tests.

4. Generate a Github access token with read package permissions, then set the following `env` variables:

    ```bash
   export packageUser=<Your GitHub Username>
   export packagePAT=<GitHub Personal Access Token>
    ```

### Build options

Execute the commands below to build from the source.

1. To build the package:

   ```bash
   ./gradlew clean build
   ```

2. To run the tests:

   ```bash
   ./gradlew clean test
   ```

3. To build the without the tests:

   ```bash
   ./gradlew clean build -x test
   ```

4. To debug package with a remote debugger:

   ```bash
   ./gradlew clean build -Pdebug=<port>
   ```

5. To debug with Ballerina language:

   ```bash
   ./gradlew clean build -PbalJavaDebug=<port>
   ```

6. Publish the generated artifacts to the local Ballerina central repository:

   ```bash
   ./gradlew clean build -PpublishToLocalCentral=true
   ```

7. Publish the generated artifacts to the Ballerina central repository:

   ```bash
   ./gradlew clean build -PpublishToCentral=true
   ```

## Contributing to Ballerina

As an open-source project, Ballerina welcomes contributions from the community.

For more information, go to the [contribution guidelines](https://github.com/ballerina-platform/ballerina-lang/blob/master/CONTRIBUTING.md).

## Code of conduct

All contributors are encouraged to read the [Ballerina Code of Conduct](https://ballerina.io/code-of-conduct).

## Useful links

- Discuss code changes of the Ballerina project in [ballerina-dev@googlegroups.com](mailto:ballerina-dev@googlegroups.com).
- Chat live with us via our [Discord server](https://discord.gg/ballerinalang).
- Post all technical questions on Stack Overflow with the [#ballerina](https://stackoverflow.com/questions/tagged/ballerina) tag.
