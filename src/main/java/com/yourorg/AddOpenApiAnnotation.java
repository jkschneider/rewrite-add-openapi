/*
 * Copyright 2022 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yourorg;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.search.FindAnnotations;
import org.openrewrite.java.tree.J;

import java.util.Comparator;

@Value
@EqualsAndHashCode(callSuper = true)
public class AddOpenApiAnnotation extends Recipe {
    @Override
    public String getDisplayName() {
        return "Add OpenAPI annotation";
    }

    @Override
    public String getDescription() {
        return "This adds the annotation to every method, but a real recipe would be more selective.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            private final JavaTemplate addOpenApi = JavaTemplate.builder(this::getCursor, "@Operation(hidden = true)")
                    .imports("io.swagger.v3.oas.annotations.Operation")
                    .javaParser(() -> JavaParser.fromJavaVersion().classpath("swagger-annotations").build())
                    .build();

            @Override
            public J.MethodDeclaration visitMethodDeclaration(J.MethodDeclaration method, ExecutionContext executionContext) {
                J.MethodDeclaration m = super.visitMethodDeclaration(method, executionContext);
                if (FindAnnotations.find(method, "@io.swagger.v3.oas.annotations.Operation").isEmpty()) {
                    maybeAddImport("io.swagger.v3.oas.annotations.Operation");
                    return m.withTemplate(addOpenApi, m.getCoordinates().addAnnotation(
                            Comparator.comparing(J.Annotation::getSimpleName)));
                }
                return m;
            }
        };
    }
}
