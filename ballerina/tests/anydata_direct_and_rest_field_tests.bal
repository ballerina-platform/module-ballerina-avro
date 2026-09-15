// Copyright (c) 2026 WSO2 LLC. (http://www.wso2.com).
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

public type DirectArrayHolder record {
    string[] tags;
};

@test:Config {
    groups: ["array", "union"]
}
public isolated function testAnydataTargetWithDirectArrayField() returns error? {
    string schema = string `
        {"type":"record","name":"DirectArrayHolder","fields":[
          {"name":"tags","type":{"type":"array","items":"string"}}
        ]}`;

    Schema avro = check new (schema);
    DirectArrayHolder typedEvent = {tags: ["a", "b"]};
    byte[] serializedValue = check avro.toAvro(typedEvent);

    anydata decoded = check avro.fromAvro(serializedValue);
    test:assertEquals(decoded.cloneWithType(DirectArrayHolder), typedEvent);
}

public type DirectMapHolder record {
    map<string> attrs;
};

@test:Config {
    groups: ["map", "union"]
}
public isolated function testAnydataTargetWithDirectMapField() returns error? {
    string schema = string `
        {"type":"record","name":"DirectMapHolder","fields":[
          {"name":"attrs","type":{"type":"map","values":"string"}}
        ]}`;

    Schema avro = check new (schema);
    DirectMapHolder typedEvent = {attrs: {"a": "1"}};
    byte[] serializedValue = check avro.toAvro(typedEvent);

    anydata decoded = check avro.fromAvro(serializedValue);
    test:assertEquals(decoded.cloneWithType(DirectMapHolder), typedEvent);
}

public type DirectRecordHeader record {
    string x;
};

public type DirectRecordHolder record {
    DirectRecordHeader header;
};

@test:Config {
    groups: ["record", "union"]
}
public isolated function testAnydataTargetWithDirectRecordField() returns error? {
    string schema = string `
        {"type":"record","name":"DirectRecordHolder","fields":[
          {"name":"header","type":{"type":"record","name":"DirectRecordHeader","fields":[
            {"name":"x","type":"string"}
          ]}}
        ]}`;

    Schema avro = check new (schema);
    DirectRecordHolder typedEvent = {header: {x: "hi"}};
    byte[] serializedValue = check avro.toAvro(typedEvent);

    anydata decoded = check avro.fromAvro(serializedValue);
    test:assertEquals(decoded.cloneWithType(DirectRecordHolder), typedEvent);
}

public type ConstrainedOpenHeader record {
    string x;
};

public type ConstrainedOpen record {|
    string known;
    ConstrainedOpenHeader...;
|};

@test:Config {
    groups: ["record"]
}
public isolated function testConstrainedCompatibleRestFieldType() returns error? {
    string schema = string `
        {"type":"record","name":"Outer","fields":[
          {"name":"known","type":"string"},
          {"name":"extra","type":{"type":"record","name":"ConstrainedOpenHeader","fields":[
            {"name":"x","type":"string"}
          ]}}
        ]}`;

    Schema avro = check new (schema);
    record {| string known; ConstrainedOpenHeader extra; |} typedEvent = {
        known: "k",
        extra: {x: "hi"}
    };
    byte[] serializedValue = check avro.toAvro(typedEvent);

    ConstrainedOpen decoded = check avro.fromAvro(serializedValue);
    test:assertTrue(decoded.cloneWithType(ConstrainedOpen) is ConstrainedOpen);
}

public type IntRestOpen record {|
    string known;
    int...;
|};

@test:Config {
    groups: ["record"]
}
public isolated function testConstrainedIncompatibleRestFieldTypeErrors() returns error? {
    string schema = string `
        {"type":"record","name":"Outer","fields":[
          {"name":"known","type":"string"},
          {"name":"extra","type":{"type":"record","name":"ConstrainedOpenHeader","fields":[
            {"name":"x","type":"string"}
          ]}}
        ]}`;

    Schema avro = check new (schema);
    record {| string known; ConstrainedOpenHeader extra; |} typedEvent = {
        known: "k",
        extra: {x: "hi"}
    };
    byte[] serializedValue = check avro.toAvro(typedEvent);

    IntRestOpen|error decoded = avro.fromAvro(serializedValue);
    test:assertTrue(decoded is error);
}
