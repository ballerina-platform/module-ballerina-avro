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
import io.ballerina.lib.avro.deserialize.Deserializer;
import io.ballerina.lib.avro.deserialize.EnumDeserializer;
import io.ballerina.lib.avro.deserialize.FixedDeserializer;
import io.ballerina.lib.avro.deserialize.GenericDeserializer;
import io.ballerina.lib.avro.deserialize.MapDeserializer;
import io.ballerina.lib.avro.deserialize.RecordDeserializer;
import io.ballerina.lib.avro.deserialize.StringDeserializer;
import io.ballerina.lib.avro.deserialize.UnionDeserializer;
import io.ballerina.runtime.api.TypeTags;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.ArrayType;
import io.ballerina.runtime.api.types.Field;
import io.ballerina.runtime.api.types.IntersectionType;
import io.ballerina.runtime.api.types.MapType;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.types.ReferenceType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.utils.TypeUtils;
import io.ballerina.runtime.api.utils.ValueUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.ballerina.lib.avro.Utils.ARRAY_TYPE;
import static io.ballerina.lib.avro.Utils.BOOLEAN_TYPE;
import static io.ballerina.lib.avro.Utils.FLOAT_TYPE;
import static io.ballerina.lib.avro.Utils.INTEGER_TYPE;
import static io.ballerina.lib.avro.Utils.RECORD_TYPE;
import static io.ballerina.lib.avro.Utils.REFERENCE_TYPE;
import static io.ballerina.lib.avro.Utils.STRING_TYPE;
import static io.ballerina.lib.avro.Utils.getMutableType;
import static io.ballerina.lib.avro.deserialize.visitor.UnionRecordUtils.visitUnionRecords;
import static io.ballerina.runtime.api.utils.StringUtils.fromString;

public class DeserializeVisitor implements IDeserializeVisitor {

    public static Deserializer createDeserializer(Schema schema, Type type) {
        return switch (schema.getElementType().getType()) {
            case UNION ->
                    new UnionDeserializer(schema, type);
            case ARRAY ->
                    new ArrayDeserializer(schema, type);
            case ENUM ->
                    new EnumDeserializer(type);
            case RECORD ->
                    new RecordDeserializer(schema, type);
            case FIXED ->
                    new FixedDeserializer(type);
            default ->
                    new GenericDeserializer(schema, type);
        };
    }

    public BMap<BString, Object> visit(RecordDeserializer recordDeserializer, GenericRecord rec) throws Exception {
        Type originalType = recordDeserializer.getType();
        Type type = recordDeserializer.getType();
        Schema schema = recordDeserializer.getSchema();
        BMap<BString, Object> avroRecord = createAvroRecord(type);
        for (Schema.Field field : schema.getFields()) {
            Object fieldData = rec.get(field.name());
            switch (field.schema().getType()) {
                case MAP ->
                        processMapField(avroRecord, field, fieldData);
                case ARRAY ->
                        processArrayField(avroRecord, field, fieldData);
                case BYTES ->
                        processBytesField(avroRecord, field, fieldData);
                case RECORD ->
                        processRecordField(avroRecord, field, fieldData);
                case STRING ->
                        processStringField(avroRecord, field, fieldData);
                case INT ->
                        avroRecord.put(fromString(field.name()), Long.parseLong(fieldData.toString()));
                case FLOAT ->
                        avroRecord.put(fromString(field.name()), Double.parseDouble(fieldData.toString()));
                case UNION ->
                        processUnionField(type, avroRecord, field, fieldData);
                default ->
                        avroRecord.put(fromString(field.name()), fieldData);
            }
        }

        if (originalType.isReadOnly()) {
            avroRecord.freezeDirect();
        }
        return avroRecord;
    }

    public BMap<BString, Object> visit(MapDeserializer mapDeserializer, Map<String, Object> data) throws Exception {
        BMap<BString, Object> avroRecord = ValueCreator.createMapValue();
        Object[] keys = data.keySet().toArray();
        Schema schema = mapDeserializer.getSchema();
        Type type = mapDeserializer.getType();
        for (Object key : keys) {
            Object value = data.get(key);
            Schema.Type valueType = schema.getValueType().getType();
            switch (valueType) {
                case ARRAY ->
                        processMapArray(avroRecord, schema, (MapType) type, key, (GenericData.Array<Object>) value);
                case BYTES ->
                        avroRecord.put(fromString(key.toString()),
                                ValueCreator.createArrayValue(((ByteBuffer) value).array()));
                case FIXED ->
                        avroRecord.put(fromString(key.toString()),
                                ValueCreator.createArrayValue(((GenericFixed) value).bytes()));
                case ENUM, STRING ->
                        avroRecord.put(fromString(key.toString()),
                                fromString(value.toString()));
                case RECORD ->
                        processMapRecord(avroRecord, schema, (MapType) type, key, (GenericRecord) value);
                case FLOAT ->
                        avroRecord.put(fromString(key.toString()), Double.parseDouble(value.toString()));
                case MAP ->
                        processMaps(avroRecord, schema, (MapType) type, key, (Map<String, Object>) value);
                default ->
                        avroRecord.put(fromString(key.toString()), value);
            }
        }
        return (BMap<BString, Object>) ValueUtils.convert(avroRecord, type);
    }

    public Object visit(GenericDeserializer genericDeserializer, GenericData.Array<Object> data) {
        Schema schema = genericDeserializer.getSchema();
        switch (schema.getElementType().getType()) {
            case STRING -> {
                return visitStringArray(data);
            }
            case INT -> {
                return visitIntArray(data);
            }
            case LONG -> {
                return visitLongArray(data);
            }
            case FLOAT, DOUBLE -> {
                return visitDoubleArray(data);
            }
            case BOOLEAN -> {
                return visitBooleanArray(data);
            }
            default -> {
                return visitBytesArray(data, genericDeserializer.getType());
            }
        }
    }

    public BArray visit(UnionDeserializer unionDeserializer, GenericData.Array<Object> data) throws Exception {
        Type type = unionDeserializer.getType();
        Schema schema = unionDeserializer.getSchema();
        switch (((ArrayType) type).getElementType().getTag()) {
            case STRING_TYPE -> {
                return visitStringArray(data);
            }
            case FLOAT_TYPE -> {
                return visitDoubleArray(data);
            }
            case BOOLEAN_TYPE -> {
                return visitBooleanArray(data);
            }
            case INTEGER_TYPE -> {
                return visitIntegerArray(data, schema);
            }
            case RECORD_TYPE -> {
                RecordDeserializer recordDeserializer = new RecordDeserializer(schema.getElementType(), type);
                return (BArray) recordDeserializer.visit(this, data);
            }
            case ARRAY_TYPE -> {
                Object[] objects = new Object[data.size()];
                Type elementType = ((ArrayType) type).getElementType();
                ArrayDeserializer arrayDeserializer = new ArrayDeserializer(schema.getElementType(), elementType);
                int index = 0;
                for (Object currentData : data) {
                    Object deserializedObject = arrayDeserializer.visit(this, (GenericData.Array<Object>) currentData);
                    objects[index++] = deserializedObject;
                }
                return ValueCreator.createArrayValue(objects, (ArrayType) type);

            }
            default -> {
                return visitBytes(data);
            }
        }
    }

    public BArray visit(RecordDeserializer recordDeserializer, GenericData.Array<Object> data) throws Exception {
        List<Object> recordList = new ArrayList<>();
        Type type = recordDeserializer.getType();
        Schema schema = recordDeserializer.getSchema();
        switch (type.getTag()) {
            case ARRAY_TYPE -> {
                for (Object datum : data) {
                    Type fieldType = ((ArrayType) type).getElementType().getCachedReferredType();
                    RecordDeserializer recordDes = new RecordDeserializer(schema.getElementType(), fieldType);
                    recordList.add(recordDes.visit(this, (GenericRecord) datum));
                }
            }
            case REFERENCE_TYPE -> {
                for (Object datum : data) {
                    Type fieldType = ((ReferenceType) type).getReferredType();
                    RecordDeserializer recordDes = new RecordDeserializer(schema.getElementType(), fieldType);
                    recordList.add(recordDes.visit(this, (GenericRecord) datum));
                }
            }
        }
        assert type instanceof ArrayType;
        return ValueCreator.createArrayValue(recordList.toArray(new Object[data.size()]), (ArrayType) type);
    }

    private BMap<BString, Object> createAvroRecord(Type type) {
        if (type instanceof IntersectionType) {
            type = getMutableType((IntersectionType) type);
        }
        return ValueCreator.createRecordValue((RecordType) type);
    }

    private void processMapField(BMap<BString, Object> avroRecord,
                                 Schema.Field field, Object fieldData) throws Exception {
        Type mapType = extractMapType(avroRecord.getType());
        MapDeserializer mapDeserializer = new MapDeserializer(field.schema(), mapType);
        Object fieldValue = mapDeserializer.visit(this, (Map<String, Object>) fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    private void processArrayField(BMap<BString, Object> avroRecord,
                                   Schema.Field field, Object fieldData) throws Exception {
        ArrayDeserializer arrayDes = new ArrayDeserializer(field.schema(), avroRecord.getType());
        Object fieldValue = arrayDes.visit(this, (GenericData.Array<Object>) fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    private void processBytesField(BMap<BString, Object> avroRecord, Schema.Field field, Object fieldData) {
        ByteBuffer byteBuffer = (ByteBuffer) fieldData;
        Object fieldValue = ValueCreator.createArrayValue(byteBuffer.array());
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    private void processRecordField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) throws Exception {
        Type recType = extractRecordType((RecordType) avroRecord.getType());
        RecordDeserializer recordDes = new RecordDeserializer(field.schema(), recType);
        Object fieldValue = recordDes.visit(this, (GenericRecord) fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    private void processStringField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) throws Exception {
        StringDeserializer stringDes = new StringDeserializer();
        Object fieldValue = stringDes.visit(this, fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    private void processUnionField(Type type, BMap<BString, Object> avroRecord,
                                   Schema.Field field, Object fieldData) throws Exception {
        visitUnionRecords(type, avroRecord, field, fieldData);
    }

    private void processMaps(BMap<BString, Object> avroRecord, Schema schema,
                             MapType type, Object key, Map<String, Object> value) throws Exception {
        Schema fieldSchema = schema.getValueType();
        Type fieldType = type.getConstrainedType();
        MapDeserializer mapDes = new MapDeserializer(fieldSchema, fieldType);
        Object fieldValue = mapDes.visit(this, value);
        avroRecord.put(fromString(key.toString()), fieldValue);
    }

    private void processMapRecord(BMap<BString, Object> avroRecord, Schema schema,
                                  MapType type, Object key, GenericRecord value) throws Exception {
        Type fieldType = type.getConstrainedType().getCachedReferredType();
        RecordDeserializer recordDes = new RecordDeserializer(schema.getValueType(), fieldType);
        Object fieldValue = recordDes.visit(this, value);
        avroRecord.put(fromString(key.toString()), fieldValue);
    }

    private void processMapArray(BMap<BString, Object> avroRecord, Schema schema,
                                 MapType type, Object key, GenericData.Array<Object> value) throws Exception {
        Type fieldType = type.getConstrainedType();
        ArrayDeserializer arrayDeserializer = new ArrayDeserializer(schema.getValueType(), fieldType);
        Object fieldValue = visit(arrayDeserializer, value);
        avroRecord.put(fromString(key.toString()), fieldValue);
    }

    public Object visit(ArrayDeserializer arrayDeserializer, GenericData.Array<Object> data) throws Exception {
        Deserializer deserializer = createDeserializer(arrayDeserializer.getSchema(), arrayDeserializer.getType());
        return deserializer.visit(new DeserializeArrayVisitor(), data);
    }

    public BArray visit(EnumDeserializer enumDeserializer, GenericData.Array<Object> data) {
        Object[] enums = new Object[data.size()];
        for (int i = 0; i < data.size(); i++) {
            enums[i] = visitString(data.get(i));
        }
        return ValueCreator.createArrayValue(enums, (ArrayType) enumDeserializer.getType());
    }

    public BArray visit(FixedDeserializer fixedDeserializer, GenericData.Array<Object> data) {
        Type type = fixedDeserializer.getType();
        List<BArray> values = new ArrayList<>();
        for (Object datum : data) {
            values.add(visitFixed(datum));
        }
        return ValueCreator.createArrayValue(values.toArray(new BArray[data.size()]), (ArrayType) type);
    }

    private static BArray visitIntegerArray(GenericData.Array<Object> data, Schema schema) {
        for (Schema schemaInstance : schema.getElementType().getTypes()) {
            if (schemaInstance.getType().equals(Schema.Type.INT)) {
                return visitIntArray(data);
            }
        }
        return visitLongArray(data);
    }

    private BArray visitBytesArray(GenericData.Array<Object> data, Type type) {
        List<BArray> values = new ArrayList<>();
        for (Object datum : data) {
            values.add(visitBytes(datum));
        }
        return ValueCreator.createArrayValue(values.toArray(new BArray[data.size()]), (ArrayType) type);
    }

    private static BArray visitBooleanArray(GenericData.Array<Object> data) {
        boolean[] booleanArray = new boolean[data.size()];
        int index = 0;
        for (Object datum : data) {
            booleanArray[index++] = (boolean) datum;
        }
        return ValueCreator.createArrayValue(booleanArray);
    }

    private BArray visitDoubleArray(GenericData.Array<Object> data) {
        List<Double> doubleList = new ArrayList<>();
        for (Object datum : data) {
            doubleList.add(visitDouble(datum));
        }
        double[] doubleArray = doubleList.stream().mapToDouble(Double::doubleValue).toArray();
        return ValueCreator.createArrayValue(doubleArray);
    }

    private static BArray visitLongArray(GenericData.Array<Object> data) {
        List<Long> longList = new ArrayList<>();
        for (Object datum : data) {
            longList.add((Long) datum);
        }
        long[] longArray = longList.stream().mapToLong(Long::longValue).toArray();
        return ValueCreator.createArrayValue(longArray);
    }

    private static BArray visitIntArray(GenericData.Array<Object> data) {
        List<Long> longList = new ArrayList<>();
        for (Object datum : data) {
            longList.add(((Integer) datum).longValue());
        }
        long[] longArray = longList.stream().mapToLong(Long::longValue).toArray();
        return ValueCreator.createArrayValue(longArray);
    }

    private BArray visitStringArray(GenericData.Array<Object> data) {
        BString[] stringArray = new BString[data.size()];
        for (int i = 0; i < data.size(); i++) {
            stringArray[i] = visitString(data.get(i));
        }
        return ValueCreator.createArrayValue(stringArray);
    }


    public double visitDouble(Object data) {
        if (data instanceof Float) {
            return Double.parseDouble(data.toString());
        }
        return (double) data;
    }

    public BArray visitBytes(Object data) {
        return ValueCreator.createArrayValue(((ByteBuffer) data).array());
    }

    public BArray visitFixed(Object data) {
        GenericData.Fixed fixed = (GenericData.Fixed) data;
        return ValueCreator.createArrayValue(fixed.bytes());
    }

    public BString visitString(Object data) {
        return fromString(data.toString());
    }

    public static Type extractMapType(Type type) {
        Type mapType = type;
        assert type instanceof RecordType;
        for (Map.Entry<String, Field> entry : ((RecordType) type).getFields().entrySet()) {
            Field fieldValue = entry.getValue();
            if (fieldValue != null) {
                Type fieldType = fieldValue.getFieldType();
                switch (fieldType.getTag()) {
                    case TypeTags.MAP_TAG:
                        mapType = fieldType;
                        break;
                    case TypeTags.INTERSECTION_TAG:
                        Type referredType = getMutableType((IntersectionType) fieldType);
                        if (referredType.getTag() == TypeTags.MAP_TAG) {
                            mapType = referredType;
                        }
                        break;
                    default:
                        Type referType = TypeUtils.getReferredType(fieldType);
                        if (referType.getTag() == TypeTags.MAP_TAG) {
                            mapType = referType;
                        }
                        break;
                }
            }
        }
        return mapType;
    }

    public static RecordType extractRecordType(RecordType type) {
        Map<String, Field> fieldsMap = type.getFields();
        RecordType recType = type;
        for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            Field fieldValue = entry.getValue();
            if (fieldValue != null) {
                Type fieldType = fieldValue.getFieldType();
                switch (fieldType.getTag()) {
                    case TypeTags.RECORD_TYPE_TAG:
                        recType = (RecordType) fieldType;
                        break;
                    case TypeTags.INTERSECTION_TAG:
                        Type getType = getMutableType((IntersectionType) fieldType);
                        if (getType.getTag() == TypeTags.RECORD_TYPE_TAG) {
                            recType = (RecordType) getType;
                        }
                        break;
                    default:
                        Type referredType = TypeUtils.getReferredType(fieldType);
                        if (referredType.getTag() == TypeTags.RECORD_TYPE_TAG) {
                            recType = (RecordType) referredType;
                        }
                        break;
                }
            }
        }
        return recType;
    }
}