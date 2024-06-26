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

package io.ballerina.lib.avro.serialize;

import org.apache.avro.Schema;

public class MessageFactory {

    public static Serializer createMessage(Schema schema) {
        return switch (schema.getType()) {
            case ARRAY -> new ArraySerializer(schema);
            case FIXED -> new FixedSerializer(schema);
            case ENUM -> new EnumSerializer(schema);
            case MAP -> new MapSerializer(schema);
            case RECORD -> new RecordSerializer(schema);
            case BYTES -> new ByteSerializer();
            default -> new PrimitiveSerializer(schema);
        };
    }
}
