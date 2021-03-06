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
package org.gradle.api.internal.artifacts.repositories.metadata;

import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ComponentResolvers;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.ModuleMetadataParser;
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceArtifactResolver;
import org.gradle.internal.component.external.model.DefaultModuleComponentArtifactMetadata;
import org.gradle.internal.component.external.model.MutableModuleComponentResolveMetadata;
import org.gradle.internal.component.model.ComponentOverrideMetadata;
import org.gradle.internal.component.model.DefaultIvyArtifactName;
import org.gradle.internal.resolve.result.BuildableModuleComponentMetaDataResolveResult;
import org.gradle.internal.resource.local.LocallyAvailableExternalResource;

import javax.inject.Inject;

/**
 * TODO: This class sources Gradle metadata files, but there's no corresponding ModuleComponentResolveMetadata for this metadata yet.
 * Because of this, we will generate an empty instance (either a Ivy or Maven) based on the repository type.
 */
public class DefaultGradleModuleMetadataSource extends AbstractMetadataSource<MutableModuleComponentResolveMetadata> {
    private final ModuleMetadataParser metadataParser;
    private final MutableModuleMetadataFactory<? extends MutableModuleComponentResolveMetadata> mutableModuleMetadataFactory;

    @Inject
    public DefaultGradleModuleMetadataSource(ModuleMetadataParser metadataParser, MutableModuleMetadataFactory<? extends MutableModuleComponentResolveMetadata> mutableModuleMetadataFactory) {
        this.metadataParser = metadataParser;
        this.mutableModuleMetadataFactory = mutableModuleMetadataFactory;
    }

    @Override
    public MutableModuleComponentResolveMetadata create(String repositoryName, ComponentResolvers componentResolvers, ModuleComponentIdentifier moduleComponentIdentifier, ComponentOverrideMetadata prescribedMetaData, ExternalResourceArtifactResolver artifactResolver, BuildableModuleComponentMetaDataResolveResult result) {
        DefaultIvyArtifactName moduleMetadataArtifact = new DefaultIvyArtifactName(moduleComponentIdentifier.getModule(), "module", "module");
        LocallyAvailableExternalResource gradleMetadataArtifact = artifactResolver.resolveArtifact(new DefaultModuleComponentArtifactMetadata(moduleComponentIdentifier, moduleMetadataArtifact), result);
        if (gradleMetadataArtifact != null) {
            MutableModuleComponentResolveMetadata metaDataFromResource = mutableModuleMetadataFactory.create(moduleComponentIdentifier);
            metadataParser.parse(gradleMetadataArtifact, metaDataFromResource);
            return metaDataFromResource;
        }
        return null;
    }

}
