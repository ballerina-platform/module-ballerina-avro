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

import io.ballerina.lib.avro.serialize.visitor.SerializeVisitor;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.utils.TypeUtils;
import org.apache.avro.Schema;

public abstract class Serializer {

    private final Schema schema;
    private final Type type;

    public Serializer() {
        this.type = null;
        this.schema = null;
    }

    public Serializer(Schema schema) {
        this.type = null;
        this.schema = schema;
    }

    public Serializer(Schema schema, Type type) {
        this.type = TypeUtils.getImpliedType(type);
        this.schema = schema;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public Type getType() {
        return this.type;
    }

    public abstract Object convert(SerializeVisitor serializeVisitor, Object data) throws AvroSerializationException;
}
