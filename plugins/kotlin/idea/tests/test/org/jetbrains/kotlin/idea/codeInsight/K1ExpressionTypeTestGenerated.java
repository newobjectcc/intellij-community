// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.codeInsight;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.idea.base.plugin.KotlinPluginMode;
import org.jetbrains.kotlin.idea.base.test.TestRoot;
import org.jetbrains.kotlin.idea.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.idea.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

/**
 * This class is generated by {@link org.jetbrains.kotlin.testGenerator.generator.TestGenerator}.
 * DO NOT MODIFY MANUALLY.
 */
@SuppressWarnings("all")
@TestRoot("idea/tests")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("testData/codeInsight/expressionType")
public class K1ExpressionTypeTestGenerated extends AbstractK1ExpressionTypeTest {
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public final KotlinPluginMode getPluginMode() {
        return KotlinPluginMode.K1;
    }

    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("AnonymousObject.kt")
    public void testAnonymousObject() throws Exception {
        runTest("testData/codeInsight/expressionType/AnonymousObject.kt");
    }

    @TestMetadata("ArgumentName.kt")
    public void testArgumentName() throws Exception {
        runTest("testData/codeInsight/expressionType/ArgumentName.kt");
    }

    @TestMetadata("BlockBodyFunction.kt")
    public void testBlockBodyFunction() throws Exception {
        runTest("testData/codeInsight/expressionType/BlockBodyFunction.kt");
    }

    @TestMetadata("ConstructorCall_fromJava_explicit.test")
    public void testConstructorCall_fromJava_explicit() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromJava_explicit.test");
    }

    @TestMetadata("ConstructorCall_fromJava_implicit.test")
    public void testConstructorCall_fromJava_implicit() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromJava_implicit.test");
    }

    @TestMetadata("ConstructorCall_fromKotlin_explicit_1.kt")
    public void testConstructorCall_fromKotlin_explicit_1() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromKotlin_explicit_1.kt");
    }

    @TestMetadata("ConstructorCall_fromKotlin_explicit_2.kt")
    public void testConstructorCall_fromKotlin_explicit_2() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromKotlin_explicit_2.kt");
    }

    @TestMetadata("ConstructorCall_fromKotlin_explicit_3.kt")
    public void testConstructorCall_fromKotlin_explicit_3() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromKotlin_explicit_3.kt");
    }

    @TestMetadata("ConstructorCall_fromKotlin_implicit.kt")
    public void testConstructorCall_fromKotlin_implicit() throws Exception {
        runTest("testData/codeInsight/expressionType/ConstructorCall_fromKotlin_implicit.kt");
    }

    @TestMetadata("IfAsExpression.kt")
    public void testIfAsExpression() throws Exception {
        runTest("testData/codeInsight/expressionType/IfAsExpression.kt");
    }

    @TestMetadata("IfAsExpressionInsideBlock.kt")
    public void testIfAsExpressionInsideBlock() throws Exception {
        runTest("testData/codeInsight/expressionType/IfAsExpressionInsideBlock.kt");
    }

    @TestMetadata("IfAsStatement.kt")
    public void testIfAsStatement() throws Exception {
        runTest("testData/codeInsight/expressionType/IfAsStatement.kt");
    }

    @TestMetadata("ImplicitInvoke_fromCompanionObject.kt")
    public void testImplicitInvoke_fromCompanionObject() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_fromCompanionObject.kt");
    }

    @TestMetadata("ImplicitInvoke_fromJava.kt")
    public void testImplicitInvoke_fromJava() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_fromJava.kt");
    }

    @TestMetadata("ImplicitInvoke_fromKotlin.kt")
    public void testImplicitInvoke_fromKotlin() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_fromKotlin.kt");
    }

    @TestMetadata("ImplicitInvoke_fromKotlin_onJavaSynthethicProperty.kt")
    public void testImplicitInvoke_fromKotlin_onJavaSynthethicProperty() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_fromKotlin_onJavaSynthethicProperty.kt");
    }

    @TestMetadata("ImplicitInvoke_functionalType.kt")
    public void testImplicitInvoke_functionalType() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_functionalType.kt");
    }

    @TestMetadata("ImplicitInvoke_functionalType_onJavaSynthethicProperty.kt")
    public void testImplicitInvoke_functionalType_onJavaSynthethicProperty() throws Exception {
        runTest("testData/codeInsight/expressionType/ImplicitInvoke_functionalType_onJavaSynthethicProperty.kt");
    }

    @TestMetadata("IntersectionTypeWithStarProjection.kt")
    public void testIntersectionTypeWithStarProjection() throws Exception {
        runTest("testData/codeInsight/expressionType/IntersectionTypeWithStarProjection.kt");
    }

    @TestMetadata("Kt11601.kt")
    public void testKt11601() throws Exception {
        runTest("testData/codeInsight/expressionType/Kt11601.kt");
    }

    @TestMetadata("Lambda.kt")
    public void testLambda() throws Exception {
        runTest("testData/codeInsight/expressionType/Lambda.kt");
    }

    @TestMetadata("LambdaParameterWithType.kt")
    public void testLambdaParameterWithType() throws Exception {
        runTest("testData/codeInsight/expressionType/LambdaParameterWithType.kt");
    }

    @TestMetadata("LambdaParameterWithoutType.kt")
    public void testLambdaParameterWithoutType() throws Exception {
        runTest("testData/codeInsight/expressionType/LambdaParameterWithoutType.kt");
    }

    @TestMetadata("LoopVariableWithType.kt")
    public void testLoopVariableWithType() throws Exception {
        runTest("testData/codeInsight/expressionType/LoopVariableWithType.kt");
    }

    @TestMetadata("LoopVariableWithoutType.kt")
    public void testLoopVariableWithoutType() throws Exception {
        runTest("testData/codeInsight/expressionType/LoopVariableWithoutType.kt");
    }

    @TestMetadata("MethodName.kt")
    public void testMethodName() throws Exception {
        runTest("testData/codeInsight/expressionType/MethodName.kt");
    }

    @TestMetadata("MethodName_fromJava.test")
    public void testMethodName_fromJava() throws Exception {
        runTest("testData/codeInsight/expressionType/MethodName_fromJava.test");
    }

    @TestMetadata("MethodReference.kt")
    public void testMethodReference() throws Exception {
        runTest("testData/codeInsight/expressionType/MethodReference.kt");
    }

    @TestMetadata("MultiDeclaration.kt")
    public void testMultiDeclaration() throws Exception {
        runTest("testData/codeInsight/expressionType/MultiDeclaration.kt");
    }

    @TestMetadata("MultiDeclarationInLambda.kt")
    public void testMultiDeclarationInLambda() throws Exception {
        runTest("testData/codeInsight/expressionType/MultiDeclarationInLambda.kt");
    }

    @TestMetadata("MultiDeclarationInLoop.kt")
    public void testMultiDeclarationInLoop() throws Exception {
        runTest("testData/codeInsight/expressionType/MultiDeclarationInLoop.kt");
    }

    @TestMetadata("PropertyAccessor.kt")
    public void testPropertyAccessor() throws Exception {
        runTest("testData/codeInsight/expressionType/PropertyAccessor.kt");
    }

    @TestMetadata("SmartCast.kt")
    public void testSmartCast() throws Exception {
        runTest("testData/codeInsight/expressionType/SmartCast.kt");
    }

    @TestMetadata("SoftSmartCast.kt")
    public void testSoftSmartCast() throws Exception {
        runTest("testData/codeInsight/expressionType/SoftSmartCast.kt");
    }

    @TestMetadata("SoftSmartCastMultipleTypes.kt")
    public void testSoftSmartCastMultipleTypes() throws Exception {
        runTest("testData/codeInsight/expressionType/SoftSmartCastMultipleTypes.kt");
    }

    @TestMetadata("ThisInLambda.kt")
    public void testThisInLambda() throws Exception {
        runTest("testData/codeInsight/expressionType/ThisInLambda.kt");
    }

    @TestMetadata("typeOfLambda.kt")
    public void testTypeOfLambda() throws Exception {
        runTest("testData/codeInsight/expressionType/typeOfLambda.kt");
    }

    @TestMetadata("TypealiasedConstructorCall_implicitTypeArguments.kt")
    public void testTypealiasedConstructorCall_implicitTypeArguments() throws Exception {
        runTest("testData/codeInsight/expressionType/TypealiasedConstructorCall_implicitTypeArguments.kt");
    }

    @TestMetadata("VariableDeclaration.kt")
    public void testVariableDeclaration() throws Exception {
        runTest("testData/codeInsight/expressionType/VariableDeclaration.kt");
    }
}
