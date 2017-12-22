/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result;

import com.google.common.collect.Maps;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.internal.attributes.AttributeContainerInternal;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.internal.serialize.AbstractSerializer;
import org.gradle.internal.serialize.Decoder;
import org.gradle.internal.serialize.Encoder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * A lossy attribute container serializer. It's lossy because it doesn't preserve the attribute
 * types: it will serialize the contents as strings, and read them as strings, only for reporting
 * purposes.
 */
public class AttributeContainerSerializer extends AbstractSerializer<AttributeContainer> {
    private final ImmutableAttributesFactory attributesFactory;
    private final Map<String, Attribute<String>> attributes = Maps.newHashMap();

    public AttributeContainerSerializer(ImmutableAttributesFactory attributesFactory) {
        this.attributesFactory = attributesFactory;
    }

    @Override
    public AttributeContainer read(Decoder decoder) throws IOException {
        AttributeContainerInternal result = attributesFactory.mutable();
        int cpt = decoder.readSmallInt();
        for (int i = 0; i < cpt; i++) {
            String name = decoder.readString();
            String value = decoder.readString();
            result.attribute(attribute(name), value);
        }
        return result.asImmutable();
    }

    protected Attribute<String> attribute(String name) {
        Attribute<String> stringAttribute = attributes.get(name);
        if (stringAttribute == null) {
            stringAttribute = Attribute.of(name, String.class);
            attributes.put(name, stringAttribute);
        }
        return stringAttribute;
    }

    @Override
    public void write(Encoder encoder, AttributeContainer container) throws IOException {
        Set<Attribute<?>> attributes = container.keySet();
        encoder.writeSmallInt(attributes.size());
        for (Attribute<?> attribute : attributes) {
            encoder.writeString(attribute.getName());
            encoder.writeString(container.getAttribute(attribute).toString());
        }
    }
}
