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

public type ArrayHeader record {
    string[] changedFields;
};

public type OptionalNestedArrayEvent record {
    ArrayHeader? header;
};

// https://github.com/ballerina-platform/ballerina-library/issues/9164
@test:Config {
    groups: ["record", "array", "union"]
}
public isolated function testArrayFieldNestedInOptionalSubRecord() returns error? {
    string schema = string `
        {"type":"record","name":"OptionalNestedArrayEvent","fields":[
          {"name":"header","type":["null",{"type":"record","name":"ArrayHeader","fields":[
            {"name":"changedFields","type":{"type":"array","items":"string"}}
          ]}]}
        ]}`;

    OptionalNestedArrayEvent event = {
        header: {
            changedFields: ["Name", "Phone"]
        }
    };

    return verifyOperation(OptionalNestedArrayEvent, event, schema);
}

public type NoteRecord record {
    string note;
};

public type MultipleRecordFieldsEvent record {
    ArrayHeader header;
    NoteRecord other;
};

// https://github.com/ballerina-platform/ballerina-library/issues/9164
@test:Config {
    groups: ["record", "array"]
}
public isolated function testArrayFieldNestedInSubRecordWithSiblingRecordField() returns error? {
    string schema = string `
        {"type":"record","name":"MultipleRecordFieldsEvent","fields":[
          {"name":"header","type":{"type":"record","name":"ArrayHeader","fields":[
            {"name":"changedFields","type":{"type":"array","items":"string"}}
          ]}},
          {"name":"other","type":{"type":"record","name":"NoteRecord","fields":[
            {"name":"note","type":"string"}
          ]}}
        ]}`;

    MultipleRecordFieldsEvent event = {
        header: {
            changedFields: ["Name", "Phone"]
        },
        other: {
            note: "hi"
        }
    };

    return verifyOperation(MultipleRecordFieldsEvent, event, schema);
}

// Flagged by CodeRabbit on PR #71: handleRecordField's cast to RecordType
// assumed the container built by DeserializeVisitor#createAvroRecord is always
// RecordType-backed, but for an `anydata` target it is map-backed instead.
@test:Config {
    groups: ["record", "union"]
}
public isolated function testAnydataTargetWithUnionWrappedNestedRecord() returns error? {
    string schema = string `
        {"type":"record","name":"AnydataUnionOuter","fields":[
          {"name":"header","type":["null",{"type":"record","name":"AnydataUnionHeader","fields":[
            {"name":"entityName","type":"string"}
          ]}]}
        ]}`;

    ClosedOuter typedEvent = {
        header: {entityName: "Account"}
    };

    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(typedEvent);
    anydata decoded = check avro.fromAvro(serializedValue);
    test:assertEquals(decoded.cloneWithType(ClosedOuter), typedEvent);
}
