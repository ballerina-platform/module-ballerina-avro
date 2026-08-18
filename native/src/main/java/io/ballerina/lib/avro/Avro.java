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

package io.ballerina.lib.avro;

import io.ballerina.lib.avro.deserialize.DeserializeFactory;
import io.ballerina.lib.avro.deserialize.Deserializer;
import io.ballerina.lib.avro.deserialize.visitor.DeserializeVisitor;
import io.ballerina.lib.avro.serialize.MessageFactory;
import io.ballerina.lib.avro.serialize.Serializer;
import io.ballerina.lib.avro.serialize.visitor.SerializeVisitor;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.api.values.BTypedesc;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import static io.ballerina.lib.avro.Utils.AVRO_SCHEMA;
import static io.ballerina.lib.avro.Utils.DESERIALIZATION_ERROR;
import static io.ballerina.lib.avro.Utils.SERIALIZATION_ERROR;
import static io.ballerina.lib.avro.Utils.createError;

public final class Avro {

    private Avro() {}

    public static Object generateSchema(BObject schemaObject, BString schema) {
        try {
            Schema.Parser parser = new Schema.Parser();
            Schema nativeSchema = parser.parse(schema.getValue());
            schemaObject.addNativeData(AVRO_SCHEMA, nativeSchema);
            return null;
        } catch (Exception e) {
            return createError("Avro schema generation error", e);
        }
    }

    public static Object toAvro(BObject schemaObject, Object data) {
        Schema schema = (Schema) schemaObject.getNativeData(AVRO_SCHEMA);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SerializeVisitor serializeVisitor = new SerializeVisitor();
            Serializer serializer = MessageFactory.createMessage(schema);
            Object avroData = Objects.requireNonNull(serializer).convert(serializeVisitor, data);
            DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(avroData, encoder);
            encoder.flush();
            return ValueCreator.createArrayValue(outputStream.toByteArray());
        } catch (Exception e) {
            return Utils.createError(SERIALIZATION_ERROR, e);
        }
    }

    public static Object fromAvro(BObject schemaObject, BArray payload, BTypedesc typeParam) {
        Schema schema = (Schema) schemaObject.getNativeData(AVRO_SCHEMA);
        DatumReader<Object> datumReader = new GenericDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(payload.getBytes(), null);
        try {
            Object data = datumReader.read(payload, decoder);
            DeserializeVisitor deserializeVisitor = new DeserializeVisitor();
            Deserializer deserializer = DeserializeFactory.generateDeserializer(schema, typeParam.getDescribingType());
            return Objects.requireNonNull(deserializer).accept(deserializeVisitor, data);
        } catch (Exception e) {
            return createError(DESERIALIZATION_ERROR, e);
        }
    }
}
