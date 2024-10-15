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

package io.ballerina.lib.avro.serialize.visitor;

import io.ballerina.lib.avro.serialize.ArraySerializer;
import io.ballerina.lib.avro.serialize.EnumSerializer;
import io.ballerina.lib.avro.serialize.FixedSerializer;
import io.ballerina.lib.avro.serialize.MapSerializer;
import io.ballerina.lib.avro.serialize.MessageFactory;
import io.ballerina.lib.avro.serialize.PrimitiveSerializer;
import io.ballerina.lib.avro.serialize.RecordSerializer;
import io.ballerina.lib.avro.serialize.Serializer;
import io.ballerina.lib.avro.serialize.UnionSerializer;
import io.ballerina.lib.avro.serialize.visitor.array.ArrayVisitorFactory;
import io.ballerina.lib.avro.serialize.visitor.array.IArrayVisitor;
import io.ballerina.runtime.api.TypeTags;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.utils.TypeUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BDecimal;
import io.ballerina.runtime.api.values.BMap;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SerializeVisitor implements ISerializeVisitor {

    public Serializer createSerializer(Schema schema) {
        return switch (schema.getValueType().getType()) {
            case INT, LONG, FLOAT, DOUBLE, BOOLEAN, STRING, BYTES ->
                    new PrimitiveSerializer(schema.getValueType());
            case RECORD ->
                    new RecordSerializer(schema.getValueType());
            case MAP ->
                    new MapSerializer(schema.getValueType());
            case ARRAY ->
                    new ArraySerializer(schema.getValueType());
            case ENUM ->
                    new EnumSerializer(schema.getValueType());
            case FIXED ->
                    new FixedSerializer(schema.getValueType());
            default ->
                    throw new IllegalArgumentException("Unsupported schema type: " + schema.getValueType().getType());
        };
    }

    @Override
    public GenericRecord visit(RecordSerializer recordSerializer, BMap<?, ?> data) throws Exception {
        GenericRecord genericRecord = new GenericData.Record(recordSerializer.getSchema());
        for (Schema.Field field : recordSerializer.getSchema().getFields()) {
            Object fieldData = data.get(StringUtils.fromString(field.name()));
            genericRecord.put(field.name(), serializeField(field.schema(), fieldData));
        }
        return genericRecord;
    }

    private Object serializeField(Schema schema, Object fieldData) throws Exception {
        Schema.Type type = schema.getType();
        return switch (type) {
            case RECORD ->
                    new RecordSerializer(schema).convert(this, fieldData);
            case MAP ->
                    new MapSerializer(schema).convert(this, fieldData);
            case ARRAY ->
                    new ArraySerializer(schema).convert(this, fieldData);
            case ENUM ->
                    new EnumSerializer(schema).convert(this, fieldData);
            case UNION ->
                    new UnionSerializer(schema).convert(this, fieldData);
            default ->
                    new PrimitiveSerializer(schema).convert(this, fieldData);
        };
    }

    @Override
    public Object visit(PrimitiveSerializer primitiveSerializer, Object data) throws Exception {
        return switch (primitiveSerializer.getSchema().getType()) {
            case INT -> {
                if (data instanceof Long longValue) {
                    yield longValue.intValue();
                }
                yield data;
            }
            case FLOAT -> {
                if (data instanceof Double doubleValue) {
                    yield doubleValue.floatValue();
                }
                yield data;
            }
            case DOUBLE -> {
                if (data instanceof Long longValue) {
                    yield longValue.doubleValue();
                } else if (data instanceof BDecimal decimalValue) {
                    yield decimalValue.floatValue();
                }
                yield data;
            }
            case BYTES -> {
                ByteBuffer byteBuffer = ByteBuffer.allocate(((BArray) data).getByteArray().length);
                byteBuffer.put(((BArray) data).getByteArray());
                byteBuffer.position(0);
                yield byteBuffer;
            }
            case STRING -> data.toString();
            case NULL -> {
                if (data != null) {
                    throw new Exception("The value does not match with the null schema");
                }
                yield null;
            }
            default -> data;
        };
    }

    public Map<String, Object> visit(MapSerializer mapSerializer, BMap<?, ?> data) throws Exception {
        Map<String, Object> avroMap = new HashMap<>();
        Schema schema = mapSerializer.getSchema();
        if (schema.getType().equals(Schema.Type.UNION)) {
            for (Schema fieldSchema: schema.getTypes()) {
                if (fieldSchema.getType().equals(Schema.Type.MAP)) {
                    schema = fieldSchema;
                }
            }
        }
        for (Object value : data.getKeys()) {
            Serializer serializer = createSerializer(schema);
            avroMap.put(value.toString(), serializer.convert(this, data.get(value)));
        }
        return avroMap;
    }

    @Override
    public Object visit(EnumSerializer enumSerializer, Object data) {
        return new GenericData.EnumSymbol(enumSerializer.getSchema(), data);
    }

    @Override
    public GenericData.Fixed visit(FixedSerializer fixedSerializer, Object data) {
        return new GenericData.Fixed(fixedSerializer.getSchema(), ((BArray) data).getByteArray());
    }

    public GenericData.Array<Object> visit(ArraySerializer arraySerializer, BArray data) {
        GenericData.Array<Object> array = new GenericData.Array<>(data.size(), arraySerializer.getSchema());
        IArrayVisitor visitor = ArrayVisitorFactory.createVisitor(arraySerializer.getSchema());
        return Objects.requireNonNull(visitor).visit(data, arraySerializer.getSchema(), array);
    }

    public ArrayList<Integer> deriveBallerinaTag(Schema schema) {
        ArrayList<Integer> tags = new ArrayList<>();
        switch (schema.getType()) {
            case STRING, ENUM -> tags.add(TypeTags.STRING_TAG);
            case FLOAT, DOUBLE -> {
                tags.add(TypeTags.FLOAT_TAG);
                tags.add(TypeTags.DECIMAL_TAG);
                tags.add(TypeTags.INT_TAG);
            }
            case LONG, INT -> tags.add(TypeTags.INT_TAG);
            case BOOLEAN -> tags.add(TypeTags.BOOLEAN_TAG);
            case NULL -> tags.add(TypeTags.NULL_TAG);
            case RECORD -> tags.add(TypeTags.RECORD_TYPE_TAG);
            case ARRAY -> tags.add(TypeTags.ARRAY_TAG);
            case MAP -> tags.add(TypeTags.MAP_TAG);
            case BYTES, FIXED -> {
                tags.add(TypeTags.BYTE_TAG);
                tags.add(TypeTags.BYTE_ARRAY_TAG);
                tags.add(TypeTags.ARRAY_TAG);
            }
            default -> tags.add(TypeTags.ANYDATA_TAG);
        }
        return tags;
    }

    public Object visit(UnionSerializer unionSerializer, Object data) throws Exception {
        Schema fieldSchema = unionSerializer.getSchema();
        Type typeName = TypeUtils.getType(data);
        List<Schema> types = fieldSchema.getTypes();
        for (Schema type : types) {
            ArrayList<Integer> tags = deriveBallerinaTag(type);
            if (tags.contains(typeName.getTag())) {
                Serializer serializer = MessageFactory.createMessage(type);
                return Objects.requireNonNull(serializer).convert(this, data);
            }
        }
        throw new Exception("Value does not match with the Avro union types");
    }
}
