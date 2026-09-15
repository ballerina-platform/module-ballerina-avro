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

package io.ballerina.lib.avro.serialize.visitor.array;

import io.ballerina.lib.avro.serialize.AvroSerializationException;
import io.ballerina.lib.avro.serialize.AvroSerializationRuntimeException;
import io.ballerina.lib.avro.serialize.RecordSerializer;
import io.ballerina.lib.avro.serialize.visitor.SerializeVisitor;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.Arrays;
import java.util.Objects;

public class RecordArrayVisitor implements IArrayVisitor {
    @Override
    public GenericData.Array<Object> visit(BArray data, Schema schema, GenericData.Array<Object> array) {
        Arrays.stream(data.getValues())
                .filter(Objects::nonNull)
                .forEach(record -> {
                    try {
                        array.add(new SerializeVisitor()
                                .visit(new RecordSerializer(schema.getElementType()), (BMap<?, ?>) record));
                    } catch (AvroSerializationException e) {
                        throw new AvroSerializationRuntimeException(e);
                    }
                });
        return array;
    }
}
