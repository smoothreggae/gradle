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

package org.gradle.language.nativeplatform.internal.toolchains;

import org.gradle.language.cpp.CppPlatform;
import org.gradle.language.cpp.internal.DefaultCppPlatform;
import org.gradle.language.swift.SwiftPlatform;
import org.gradle.language.swift.internal.DefaultSwiftPlatform;
import org.gradle.model.internal.registry.ModelRegistry;
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainRegistryInternal;
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider;

import javax.inject.Inject;

public class DefaultToolChainSelector implements ToolChainSelector {
    private final ModelRegistry modelRegistry;

    @Inject
    public DefaultToolChainSelector(ModelRegistry modelRegistry) {
        this.modelRegistry = modelRegistry;
    }

    @Override
    public <T extends NativePlatform> Result<T> select(Class<T> platformType) {
        DefaultNativePlatform targetPlatform = new DefaultNativePlatform("current");
        NativeToolChainInternal toolChain = (NativeToolChainInternal) modelRegistry.realize("toolChains", NativeToolChainRegistryInternal.class).getForPlatform(targetPlatform);
        PlatformToolProvider toolProvider = toolChain.select(targetPlatform);

        T t = null;
        if (CppPlatform.class.isAssignableFrom(platformType)) {
            t = platformType.cast(new DefaultCppPlatform("current"));
        } else if (SwiftPlatform.class.isAssignableFrom(platformType)) {
            t = platformType.cast(new DefaultSwiftPlatform("current"));
        }
        return new DefaultResult<T>(toolChain, t, toolProvider);
    }

    class DefaultResult<T extends NativePlatform> implements Result<T> {
        private final NativeToolChainInternal toolChain;
        private final T targetPlatform;
        private final PlatformToolProvider platformToolProvider;

        public DefaultResult(NativeToolChainInternal toolChain, T targetPlatform, PlatformToolProvider platformToolProvider) {
            this.toolChain = toolChain;
            this.targetPlatform = targetPlatform;
            this.platformToolProvider = platformToolProvider;
        }

        @Override
        public NativeToolChainInternal getToolChain() {
            return toolChain;
        }

        @Override
        public T getTargetPlatform() {
            return targetPlatform;
        }

        @Override
        public PlatformToolProvider getPlatformToolProvider() {
            return platformToolProvider;
        }
    }
}
