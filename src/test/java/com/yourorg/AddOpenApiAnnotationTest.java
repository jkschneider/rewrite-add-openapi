package com.yourorg;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class AddOpenApiAnnotationTest implements RewriteTest {

  @Override
  public void defaults(RecipeSpec spec) {
    spec.recipe(new AddOpenApiAnnotation())
      .parser(JavaParser.fromJavaVersion()
        .logCompilationWarningsAndErrors(true)
        .classpath("swagger-annotations"));
  }

  @Test
  void addAnnotation() {
    //language=java
    rewriteRun(
      java(
        """
              class Test {
                  void test() {
                  }
              }
          """,
        """
             import io.swagger.v3.oas.annotations.Operation;
             
             class Test {
                 @Operation(hidden = true)
                 void test() {
                 }
             }
         """
      )
    );
  }
}
