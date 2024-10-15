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
import ballerina/io;

@test:Config {
    groups: ["record"]
}
public isolated function testRecords() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "subject", "type": "string"}
            ]
        }`;

    Student student = {
        name: "Liam",
        subject: "geology"
    };
    return verifyOperation(Student, student, schema);
}

@test:Config {
    groups: ["record"]
}
public isolated function testRecordsWithDifferentTypeOfFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "age", "type": "int"}
            ]
        }`;

    Person student = {
        name: "Liam",
        age: 52
    };
    return verifyOperation(Person, student, schema);
}

@test:Config {
    groups: ["record"]
}
public isolated function testNestedRecords() returns error? {
    string jsonFileName = string `tests/resources/schema_record_nested.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();

    Lecturer3 lecturer = {
        name: {"John": 1, "Sam": 2, "Liam": 3},
        age: 11,
        instructor: {
            name: "Liam",
            student: {
                name: "Sam",
                subject: "geology"
            }
        }
    };

    return verifyOperation(Lecturer3, lecturer, schema);
}

@test:Config {
    groups: ["record", "array"]
}
public isolated function testArraysInRecords() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "colors", "type": {"type": "array", "items": "string"}}
            ]
        }`;

    Color colors = {
        name: "Red",
        colors: ["maroon", "dark red", "light red"]
    };

    return verifyOperation(Color, colors, schema);
}

@test:Config {
    groups: ["record", "array"]
}
public isolated function testArraysInReadOnlyRecords() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "colors", "type": {"type": "array", "items": "string"}}
            ]
        }`;

    ReadOnlyColors colors = {
        name: "Red",
        colors: ["maroon", "dark red", "light red"]
    };

    return verifyOperation(ReadOnlyColors, colors, schema);
}

@test:Config {
    groups: ["record", "errors"]
}
public isolated function testArraysInRecordsWithInvalidSchema() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "colors", "type": "bytes"}
            ]
        }`;

    Color1 colors = {
        name: "Red",
        colors: "ss".toBytes()
    };

    Schema avroProducer = check new (schema);
    byte[] serialize = check avroProducer.toAvro(colors);
    string schema2 = string `
    {
        "namespace": "example.avro",
        "type": "record",
        "name": "Student",
        "fields": [
            {"name": "name", "type": "string"},
            {"name": "colors", "type": {"type": "array", "items": "int"}}
        ]
    }`;
    Schema avroConsumer = check new (schema2);
    Color1|Error deserializedValue = avroConsumer.fromAvro(serialize);
    test:assertTrue(deserializedValue is Error);
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testRecordsWithStringRecordUnionType() returns error? {
    string jsonFileName = string `tests/resources/schema_record_string_record_union.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();

    MultipleUnionRecord course = {
        name: "data",
        value: 0.0,
        credits: 5,
        student: string `{name: "Jon", subject: "geo"}adsk`
    };

    return verifyOperation(MultipleUnionRecord, course, schema);
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testRecordsWithUnionTypes() returns error? {
    string jsonFileName = string `tests/resources/schema_record_union.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();

    UnionRecord course = {
        name: "data",
        value: 0.0,
        credits: 5,
        student: {name: "Jon", subject: "geo"}
    };
    return verifyOperation(UnionRecord, course, schema);
}

@test:Config {
    groups: ["record", "primitive", "int"]
}
public isolated function testRecordsWithIntFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "age", "type": "int"}
            ]
        }`;

    Person student = {
        name: "Liam",
        age: 52
    };
    return verifyOperation(Person, student, schema);
}

@test:Config {
    groups: ["record", "primitive", "long"]
}
public isolated function testRecordsWithLongFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "age", "type": "long"}
            ]
        }`;

    Person student = {
        name: "Liam",
        age: 52
    };
    return verifyOperation(Person, student, schema);
}

@test:Config {
    groups: ["record", "primitive", "float"]
}
public isolated function testRecordsWithFloatFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "age", "type": "float"}
            ]
        }`;

    Students student = {
        name: "Liam",
        age: 52.656
    };
    return verifyOperation(Students, student, schema);
}

@test:Config {
    groups: ["record", "primitive", "double"]
}
public isolated function testRecordsWithDoubleFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "age", "type": "double"}
            ]
        }`;

    Students student = {
        name: "Liam",
        age: 52.656
    };
    return verifyOperation(Students, student, schema);
}

@test:Config {
    groups: ["record", "primitive", "boolean"]
}
public isolated function testRecordsWithBooleanFields() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "under19", "type": "boolean"}
            ]
        }`;

    StudentRec student = {
        name: "Liam",
        under19: false
    };
    return verifyOperation(StudentRec, student, schema);
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testOptionalValuesInRecords() returns error? {
    string jsonFileName = string `tests/resources/schema_record_optional_fields.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();

    Instructor instructor = {
        name: "John",
        student: {
            name: "Alice",
            subject: "Math"
        }
    };

    Lecturer5 lecturer5 = {
        name: {
            "John": 1, 
            "Sam": 2, 
            "Liam": 3
        },
        bytes: "ss".toBytes(),
        instructorClone: instructor.cloneReadOnly(),
        instructors: instructor
    };
    return verifyOperation(Lecturer5, lecturer5, schema);
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testOptionalMultipleFieldsInRecords() returns error? {
    string jsonFileName = string `tests/resources/schema_record_multiple_optional_fields.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();
    Instructor instructor = {
        name: "John",
        student: {
            name: "Alice",
            subject: "Math"
        }
    };

    Numbers number = ONE;

    Lecturer6 lecturer6 = {
        temporary: false,
        maps: {
            "1": 100,
            "2": 200
        },
        bytes: "ss".toBytes(),
        age: 30,
        number: number,
        name: "Lecturer Name",
        floatNumber: 123.45,
        colors: [number, number, number],
        instructorClone: instructor.cloneReadOnly(),
        instructors: instructor
    };
    return verifyOperation(Lecturer6, lecturer6, schema);
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testTypeCastingInRecords() returns error? {
    string jsonFileName = string `tests/resources/schema_complex.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();
    Record recordValue = {
        hrdCasinoGgrLt: 4999.01,
        hrdAccountCreationTimestamp: "2024-02-25T20:50:37.891782",
        hrdSportsBetCountLifetime: 77,
        hrdSportsFreeBetAmountLifetime: 0,
        hriUnityId: "807612199",
        hrdLastRealMoneySportsbookBetTs: "2024-04-24T20:15:51.932",
        hrdLoyaltyTier: "MEMBER_TIER",
        rowInsertTimestampEst: "2024-09-23T03:29:43.431",
        ltvSports365Total: 0,
        hrdFirstDepositTimestamp: "2024-02-25T20:56:24.109021",
        hrdVipStatus: true,
        hrdLastRealMoneyCasinoWagerTs: null,
        hrdSportsCashBetAmountLifetime: 731,
        hrdAccountStatus: "ACTIVE",
        hrdAccountId: "1362347172559585332",
        ltvAllVerticals365Total: 0,
        hrdOptInSms: false,
        ltvCasino365Total: null,
        hrdAccountSubStatus: null,
        hrdCasinoTotalWagerLifetime: null,
        hrdSportsGgrLt: 4999.01,
        currentGeoSegment: "HRD_FLORIDA",
        kycStatus: "VERIFIED",
        signupGeoSegment: "HRD_FLORIDA",
        hrdSportsBetAmountLifetime: 731,
        hrdOptInEmail: false,
        hrdOptInPush: false
    };
    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(recordValue);
    Record deserializedValue = check avro.fromAvro(serializedValue);
    
    json expected = {
        "hrdCasinoGgrLt":4999.01,
        "hrdAccountCreationTimestamp":"2024-02-25T20:50:37.891782",
        "hrdSportsFreeBetAmountLifetime":0.0,
        "hriUnityId":"807612199",
        "hrdLastRealMoneySportsbookBetTs":"2024-04-24T20:15:51.932",
        "hrdLoyaltyTier":"MEMBER_TIER",
        "rowInsertTimestampEst":"2024-09-23T03:29:43.431",
        "ltvSports365Total":0.0,
        "hrdVipStatus":true,
        "hrdSportsBetCountLifetime":77.0,
        "hrdLastRealMoneyCasinoWagerTs":null,
        "hrdSportsCashBetAmountLifetime":731.0,
        "hrdAccountStatus":"ACTIVE",
        "hrdAccountId":"1362347172559585332",
        "ltvAllVerticals365Total":0.0,
        "ltvCasino365Total":null,
        "hrdAccountSubStatus":null,
        "hrdCasinoTotalWagerLifetime":null,
        "hrdSportsGgrLt":4999.01,
        "currentGeoSegment":"HRD_FLORIDA",
        "kycStatus":"VERIFIED",
        "hrdOptInSms":false,
        "hrdFirstDepositTimestamp":"2024-02-25T20:56:24.109021",
        "signupGeoSegment":"HRD_FLORIDA",
        "hrdSportsBetAmountLifetime":731.0,
        "hrdOptInEmail":false,
        "hrdOptInPush":false
    };
    test:assertEquals(deserializedValue.toJson(), expected.toJson());
}

@test:Config {
    groups: ["record", "union"]
}
public isolated function testUnionsWithRecords() returns error? {
    string schema = string `{
        "type": "record",
        "name": "DataRecord",
        "namespace": "data",
        "fields": [
            {
            "name": "shortValue",
            "type": ["null", "int", "double"]
            }
        ]
    }`;
    int:Signed16 short = 5555;
    json value = {
        "shortValue": short
    };
    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(value);
    DataRecord deserializedValue = check avro.fromAvro(serializedValue);
    test:assertEquals(deserializedValue, value);
}
