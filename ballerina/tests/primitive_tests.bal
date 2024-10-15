// Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/test;

@test:Config {
    groups: ["primitive", "int"]
}
public isolated function testIntValue() returns error? {
    string schema = string `
        {
            "type": "int",
            "name" : "intValue", 
            "namespace": "data"
        }`;

    int value = 5;
    return verifyOperation(int, value, schema);
}

@test:Config {
    groups: ["primitive", "float"]
}
public isolated function testFloatValue() returns error? {
    string schema = string `
        {
            "type": "float",
            "name" : "floatValue", 
            "namespace": "data"
        }`;

    float value = 5.5;
    return verifyOperation(float, value, schema);
}

@test:Config {
    groups: ["primitive", "double"]
}
public isolated function testDoubleValue() returns error? {
    string schema = string `
        {
            "type": "double",
            "name" : "doubleValue", 
            "namespace": "data"
        }`;

    float value = 5.5595;
    return verifyOperation(float, value, schema);
}

@test:Config {
   groups: ["primitive", "check", "l"]
}
public isolated function testLongValue() returns error? {
    string schema = string `
        {
            "type": "long",
            "name" : "longValue", 
            "namespace": "data"
        }`;

    int value = 555950000000000000;
    return verifyOperation(int, value, schema);
}

@test:Config {
    groups: ["primitive", "check"]
}
public isolated function testStringValue() returns error? {
    string schema = string `
        {
            "type": "string",
            "name" : "stringValue", 
            "namespace": "data"
        }`;

    string value = "test";
    return verifyOperation(string, value, schema);
}

@test:Config {
    groups: ["primitive", "check"]
}
public isolated function testBoolean() returns error? {
    string schema = string `
        {
            "type": "boolean",
            "name" : "booleanValue", 
            "namespace": "data"
        }`;

    boolean value = true;
    return verifyOperation(boolean, value, schema);
}

@test:Config {
    groups: ["primitive", "null"]
}
public isolated function testNullValues() returns error? {
    string schema = string `
        {
            "type": "null",
            "name" : "nullValue", 
            "namespace": "data"
        }`;
    return verifyOperation(NullType, (), schema);
}

@test:Config {
    groups: ["primitive", "null"]
}
public isolated function testNullValuesWithNonNullData() returns error? {
    string schema = string `
        {
            "type": "null",
            "name" : "nullValue", 
            "namespace": "data"
        }`;

    Schema avro = check new (schema);
    byte[]|error serializedValue = avro.toAvro("string");
    test:assertTrue(serializedValue is error);
}

@test:Config {
   groups: ["primitive"]
}
public isolated function testUnsignedShortValue() returns error? {
    string schema = string `
        {
            "type": "int",
            "name" : "shortValue", 
            "namespace": "data"
        }`;

    int:Unsigned8 value = 255;
    return verifyOperation(int:Unsigned8, value, schema);
}

@test:Config {
   groups: ["primitive", "check"]
}
public isolated function testSignedShortValue() returns error? {
    string schema = string `
        {
            "type": "int",
            "name" : "shortValue", 
            "namespace": "data"
        }`;

    int:Signed32 value = 555950000;
    return verifyOperation(int:Signed32, value, schema);
}

@test:Config {
   groups: ["primitive", "check"]
}
public isolated function testSignedMinusShortValue() returns error? {
    string schema = string `
        {
            "type": "double",
            "name" : "shortValue", 
            "namespace": "data"
        }`;

    int:Signed32 value = -2147483;
    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(value);
    float deserializedValue = check avro.fromAvro(serializedValue);
    test:assertEquals(deserializedValue, -2147483.0);
}

@test:Config {
   groups: ["primitive", "check"]
}
public isolated function testByteValue() returns error? {
    string schema = string `
        {
            "type": "bytes",
            "name" : "byteValue", 
            "namespace": "data"
        }`;
    byte[] data = [];
    byte value = 2;
    data.push(value);
    return verifyOperation(ByteArray, data, schema);
}
