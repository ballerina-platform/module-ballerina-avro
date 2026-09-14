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
import io.ballerina.runtime.api.values.BMap;
import org.apache.avro.Schema;

public class MapSerializer extends Serializer {

    public MapSerializer(Schema schema) {
        super(schema);
    }

    @Override
    public Object convert(SerializeVisitor serializeVisitor, Object data) throws AvroSerializationException {
        return serializeVisitor.visit(this, (BMap<?, ?>) data);
    }
}
