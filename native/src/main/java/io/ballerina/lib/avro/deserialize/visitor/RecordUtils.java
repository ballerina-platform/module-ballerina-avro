/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.lib.avro.deserialize.visitor;

import io.ballerina.lib.avro.deserialize.ArrayDeserializer;
import io.ballerina.lib.avro.deserialize.AvroDeserializationException;
import io.ballerina.lib.avro.deserialize.MapDeserializer;
import io.ballerina.lib.avro.deserialize.PrimitiveDeserializer;
import io.ballerina.lib.avro.deserialize.RecordDeserializer;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.nio.ByteBuffer;

import static io.ballerina.lib.avro.deserialize.visitor.DeserializeVisitor.extractMapType;
import static io.ballerina.lib.avro.deserialize.visitor.DeserializeVisitor.extractRecordType;
import static io.ballerina.lib.avro.deserialize.visitor.UnionRecordUtils.visitUnionRecords;
import static io.ballerina.runtime.api.utils.StringUtils.fromString;

public class RecordUtils {

    public static void processMapField(BMap<BString, Object> avroRecord,
                                 Schema.Field field, Object fieldData) throws AvroDeserializationException {
        Type mapType = extractMapType(avroRecord.getType());
        MapDeserializer mapDeserializer = new MapDeserializer(field.schema(), mapType);
        Object fieldValue = mapDeserializer.accept(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processArrayField(BMap<BString, Object> avroRecord,
                                         Schema.Field field, Object fieldData, Type type)
            throws AvroDeserializationException {
        ArrayDeserializer arrayDes = new ArrayDeserializer(type, field.schema());
        Object fieldValue = arrayDes.accept(new DeserializeVisitor(), (GenericData.Array<Object>) fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processBytesField(BMap<BString, Object> avroRecord, Schema.Field field, Object fieldData) {
        ByteBuffer byteBuffer = (ByteBuffer) fieldData;
        Object fieldValue = ValueCreator.createArrayValue(byteBuffer.array());
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processRecordField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) throws AvroDeserializationException {
        Type recType = extractRecordType((RecordType) avroRecord.getType());
        RecordDeserializer recordDes = new RecordDeserializer(recType, field.schema());
        Object fieldValue = recordDes.accept(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processStringField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) throws AvroDeserializationException {
        PrimitiveDeserializer stringDes = new PrimitiveDeserializer(null, field.schema());
        Object fieldValue = stringDes.accept(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processUnionField(Type type, BMap<BString, Object> avroRecord,
                                   Schema.Field field, Object fieldData) throws AvroDeserializationException {
        visitUnionRecords(type, avroRecord, field, fieldData);
    }
}
