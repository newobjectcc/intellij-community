// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package org.jetbrains.kotlin.idea.fir.copyPaste;

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
@TestRoot("fir/tests")
@TestDataPath("$CONTENT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
@TestMetadata("../../idea/tests/testData/copyPaste/plainTextLiteral")
public class FirLiteralTextToKotlinCopyPasteTestGenerated extends AbstractFirLiteralTextToKotlinCopyPasteTest {
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public final KotlinPluginMode getPluginMode() {
        return KotlinPluginMode.K2;
    }

    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    @TestMetadata("AlreadyPrefixed.txt")
    public void testAlreadyPrefixed() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/AlreadyPrefixed.txt");
    }

    @TestMetadata("BrokenEntries.txt")
    public void testBrokenEntries() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/BrokenEntries.txt");
    }

    @TestMetadata("CustomTrimIndent.txt")
    public void testCustomTrimIndent() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/CustomTrimIndent.txt");
    }

    @TestMetadata("IsolatedDollarsToPrefixedString.txt")
    public void testIsolatedDollarsToPrefixedString() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/IsolatedDollarsToPrefixedString.txt");
    }

    @TestMetadata("MultiLine.txt")
    public void testMultiLine() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/MultiLine.txt");
    }

    @TestMetadata("MultiLineToTripleQuotes.txt")
    public void testMultiLineToTripleQuotes() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/MultiLineToTripleQuotes.txt");
    }

    @TestMetadata("MultiQuotesToTripleQuotes.txt")
    public void testMultiQuotesToTripleQuotes() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/MultiQuotesToTripleQuotes.txt");
    }

    @TestMetadata("NoSpecialCharsToSingleQuote.txt")
    public void testNoSpecialCharsToSingleQuote() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/NoSpecialCharsToSingleQuote.txt");
    }

    @TestMetadata("RawNoPrefixTwoDollars1.txt")
    public void testRawNoPrefixTwoDollars1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawNoPrefixTwoDollars1.txt");
    }

    @TestMetadata("RawNoPrefixTwoDollars2.txt")
    public void testRawNoPrefixTwoDollars2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawNoPrefixTwoDollars2.txt");
    }

    @TestMetadata("RawNoPrefixTwoDollars3.txt")
    public void testRawNoPrefixTwoDollars3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawNoPrefixTwoDollars3.txt");
    }

    @TestMetadata("RawNoPrefixTwoDollars4.txt")
    public void testRawNoPrefixTwoDollars4() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawNoPrefixTwoDollars4.txt");
    }

    @TestMetadata("RawNoPrefixTwoDollars5.txt")
    public void testRawNoPrefixTwoDollars5() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawNoPrefixTwoDollars5.txt");
    }

    @TestMetadata("RawPrefix2EntriesAndSurroundingDollars1.txt")
    public void testRawPrefix2EntriesAndSurroundingDollars1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2EntriesAndSurroundingDollars1.txt");
    }

    @TestMetadata("RawPrefix2EntriesAndSurroundingDollars2.txt")
    public void testRawPrefix2EntriesAndSurroundingDollars2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2EntriesAndSurroundingDollars2.txt");
    }

    @TestMetadata("RawPrefix2EntriesAndSurroundingDollars3.txt")
    public void testRawPrefix2EntriesAndSurroundingDollars3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2EntriesAndSurroundingDollars3.txt");
    }

    @TestMetadata("RawPrefix2OneDollar1.txt")
    public void testRawPrefix2OneDollar1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar1.txt");
    }

    @TestMetadata("RawPrefix2OneDollar2.txt")
    public void testRawPrefix2OneDollar2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar2.txt");
    }

    @TestMetadata("RawPrefix2OneDollar3.txt")
    public void testRawPrefix2OneDollar3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar3.txt");
    }

    @TestMetadata("RawPrefix2OneDollar4.txt")
    public void testRawPrefix2OneDollar4() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar4.txt");
    }

    @TestMetadata("RawPrefix2OneDollar5.txt")
    public void testRawPrefix2OneDollar5() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar5.txt");
    }

    @TestMetadata("RawPrefix2OneDollar6.txt")
    public void testRawPrefix2OneDollar6() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar6.txt");
    }

    @TestMetadata("RawPrefix2OneDollar7.txt")
    public void testRawPrefix2OneDollar7() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2OneDollar7.txt");
    }

    @TestMetadata("RawPrefix2PastedDollarsBeforeAfter1.txt")
    public void testRawPrefix2PastedDollarsBeforeAfter1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2PastedDollarsBeforeAfter1.txt");
    }

    @TestMetadata("RawPrefix2PastedDollarsBeforeAfter2.txt")
    public void testRawPrefix2PastedDollarsBeforeAfter2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2PastedDollarsBeforeAfter2.txt");
    }

    @TestMetadata("RawPrefix2PastedDollarsBeforeAfter3.txt")
    public void testRawPrefix2PastedDollarsBeforeAfter3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2PastedDollarsBeforeAfter3.txt");
    }

    @TestMetadata("RawPrefix2TwoDollars1.txt")
    public void testRawPrefix2TwoDollars1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2TwoDollars1.txt");
    }

    @TestMetadata("RawPrefix2TwoDollars2.txt")
    public void testRawPrefix2TwoDollars2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2TwoDollars2.txt");
    }

    @TestMetadata("RawPrefix2TwoDollarsSelection1.txt")
    public void testRawPrefix2TwoDollarsSelection1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2TwoDollarsSelection1.txt");
    }

    @TestMetadata("RawPrefix2TwoDollarsSelection2.txt")
    public void testRawPrefix2TwoDollarsSelection2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix2TwoDollarsSelection2.txt");
    }

    @TestMetadata("RawPrefix3TwoDollars1.txt")
    public void testRawPrefix3TwoDollars1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoDollars1.txt");
    }

    @TestMetadata("RawPrefix3TwoDollars2.txt")
    public void testRawPrefix3TwoDollars2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoDollars2.txt");
    }

    @TestMetadata("RawPrefix3TwoDollars3.txt")
    public void testRawPrefix3TwoDollars3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoDollars3.txt");
    }

    @TestMetadata("RawPrefix3TwoSeparatedDollars1.txt")
    public void testRawPrefix3TwoSeparatedDollars1() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoSeparatedDollars1.txt");
    }

    @TestMetadata("RawPrefix3TwoSeparatedDollars2.txt")
    public void testRawPrefix3TwoSeparatedDollars2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoSeparatedDollars2.txt");
    }

    @TestMetadata("RawPrefix3TwoSeparatedDollars3.txt")
    public void testRawPrefix3TwoSeparatedDollars3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoSeparatedDollars3.txt");
    }

    @TestMetadata("RawPrefix3TwoSeparatedDollars4.txt")
    public void testRawPrefix3TwoSeparatedDollars4() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoSeparatedDollars4.txt");
    }

    @TestMetadata("RawPrefix3TwoSeparatedDollars5.txt")
    public void testRawPrefix3TwoSeparatedDollars5() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/RawPrefix3TwoSeparatedDollars5.txt");
    }

    @TestMetadata("Stacktrace.txt")
    public void testStacktrace() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/Stacktrace.txt");
    }

    @TestMetadata("StacktraceDoublePrefix.txt")
    public void testStacktraceDoublePrefix() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/StacktraceDoublePrefix.txt");
    }

    @TestMetadata("StacktraceTriplePrefix.txt")
    public void testStacktraceTriplePrefix() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/StacktraceTriplePrefix.txt");
    }

    @TestMetadata("TrailingLines.txt")
    public void testTrailingLines() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrailingLines.txt");
    }

    @TestMetadata("TrimIndent.txt")
    public void testTrimIndent() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndent.txt");
    }

    @TestMetadata("TrimIndent2.txt")
    public void testTrimIndent2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndent2.txt");
    }

    @TestMetadata("TrimIndent3.txt")
    public void testTrimIndent3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndent3.txt");
    }

    @TestMetadata("TrimIndentAndDollarSign.txt")
    public void testTrimIndentAndDollarSign() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndentAndDollarSign.txt");
    }

    @TestMetadata("TrimIndentAndDollarSign2.txt")
    public void testTrimIndentAndDollarSign2() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndentAndDollarSign2.txt");
    }

    @TestMetadata("TrimIndentAndDollarSign3.txt")
    public void testTrimIndentAndDollarSign3() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndentAndDollarSign3.txt");
    }

    @TestMetadata("TrimIndentAndDollarSign4.txt")
    public void testTrimIndentAndDollarSign4() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/TrimIndentAndDollarSign4.txt");
    }

    @TestMetadata("WithBackslashes.txt")
    public void testWithBackslashes() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/WithBackslashes.txt");
    }

    @TestMetadata("WithDollarSignToTripleQuotes.txt")
    public void testWithDollarSignToTripleQuotes() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/WithDollarSignToTripleQuotes.txt");
    }

    @TestMetadata("WithEntries.txt")
    public void testWithEntries() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/WithEntries.txt");
    }

    @TestMetadata("WithQuotes.txt")
    public void testWithQuotes() throws Exception {
        runTest("../../idea/tests/testData/copyPaste/plainTextLiteral/WithQuotes.txt");
    }
}
