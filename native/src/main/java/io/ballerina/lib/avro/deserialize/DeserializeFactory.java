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

package io.ballerina.lib.avro.deserialize;

import io.ballerina.runtime.api.types.Type;
import org.apache.avro.Schema;

public class DeserializeFactory {

    public static Deserializer generateDeserializer(Schema schema, Type type) {
        return switch (schema.getType()) {
            case ARRAY -> new ArrayDeserializer(type, schema);
            case FIXED -> new FixedDeserializer(type, schema);
            case MAP -> new MapDeserializer(schema, type);
            case RECORD -> new RecordDeserializer(type, schema);
            default -> new PrimitiveDeserializer(type, schema);
        };
    }
}
