# Change Log

This file contains all the notable changes done to the Ballerina Avro package through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.6] - 2026-10-08

### Added
- [[#9274] Add package icon for the stdlib packages missing a logo in the Integration Store](https://github.com/ballerina-platform/ballerina-library/issues/9274)

### Changed
- [[#9112] Update Keywords and Reformat README for Connector Store Discoverability](https://github.com/ballerina-platform/ballerina-library/issues/9112)

### Fixed
- [[#9017] Fix silent data alteration in `toAvro` by throwing `AvroSerializationException` for values that do not conform to the Avro schema](https://github.com/ballerina-platform/ballerina-library/issues/9017)
- [[#9164] Fix `fromAvro` NPE for an array field nested in an optional sub-record](https://github.com/ballerina-platform/ballerina-library/issues/9164)
- Fix `fromAvro` throwing for any nested record/array/map field when the parent target type is an open/dynamic record (e.g. `record {}`) with no declared fields - a regression introduced by the fix above.

## [1.2.2] - 2026-07-24

### Fixed

- [Security vulnerabilities with Jackson library](https://github.com/ballerina-platform/ballerina-library/issues/8924)

## [1.0.2] - 2024-10-18

### Fixed

- [[#27] Fix handling union type values in the Avro payload](https://github.com/ballerina-platform/module-ballerina-avro/pull/27)

## [1.0.1] - 2024-10-10

### Fixed

- [[#25] Fix `CVE-2024-47561` security vulnerability](https://github.com/ballerina-platform/module-ballerina-avro/pull/25)

## [1.0.0] - 2024-05-13

### Added

- [[#17] Introduce `byte` support to Avro module](https://github.com/ballerina-platform/ballerina-library/issues/6463)
