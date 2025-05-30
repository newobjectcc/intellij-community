// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.kotlin.idea.completion.impl.k2.contributors

import com.intellij.codeInsight.completion.PrefixMatcher
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.registry.RegistryManager
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.signatures.KaCallableSignature
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaNamedSymbol
import org.jetbrains.kotlin.idea.base.analysis.api.utils.KtSymbolFromIndexProvider
import org.jetbrains.kotlin.idea.base.analysis.api.utils.shortenReferencesInRange
import org.jetbrains.kotlin.idea.base.facet.platform.platform
import org.jetbrains.kotlin.idea.completion.KOTLIN_CAST_REQUIRED_COLOR
import org.jetbrains.kotlin.idea.completion.KotlinFirCompletionParameters
import org.jetbrains.kotlin.idea.completion.checkers.CompletionVisibilityChecker
import org.jetbrains.kotlin.idea.completion.contributors.helpers.CallableMetadataProvider
import org.jetbrains.kotlin.idea.completion.contributors.helpers.CompletionSymbolOrigin
import org.jetbrains.kotlin.idea.completion.contributors.helpers.KtSymbolWithOrigin
import org.jetbrains.kotlin.idea.completion.doPostponedOperationsAndUnblockDocument
import org.jetbrains.kotlin.idea.completion.impl.k2.ImportStrategyDetector
import org.jetbrains.kotlin.idea.completion.impl.k2.LookupElementSink
import org.jetbrains.kotlin.idea.completion.lookups.CallableInsertionOptions
import org.jetbrains.kotlin.idea.completion.lookups.ImportStrategy
import org.jetbrains.kotlin.idea.completion.lookups.factories.ClassifierLookupObject
import org.jetbrains.kotlin.idea.completion.lookups.factories.FunctionCallLookupObject
import org.jetbrains.kotlin.idea.completion.lookups.factories.KotlinFirLookupElementFactory
import org.jetbrains.kotlin.idea.completion.weighers.CallableWeigher.callableWeight
import org.jetbrains.kotlin.idea.completion.weighers.Weighers.applyWeighs
import org.jetbrains.kotlin.idea.completion.weighers.WeighingContext
import org.jetbrains.kotlin.idea.util.positionContext.KotlinExpressionNameReferencePositionContext
import org.jetbrains.kotlin.idea.util.positionContext.KotlinNameReferencePositionContext
import org.jetbrains.kotlin.idea.util.positionContext.KotlinRawPositionContext
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.renderer.render
import org.jetbrains.kotlin.util.OperatorNameConventions

internal abstract class FirCompletionContributorBase<C : KotlinRawPositionContext>(
    protected val parameters: KotlinFirCompletionParameters,
    sink: LookupElementSink,
    priority: Int,
) : FirCompletionContributor<C> {

    protected open val prefixMatcher: PrefixMatcher
        get() = sink.prefixMatcher

    protected val visibilityChecker = CompletionVisibilityChecker(parameters)

    protected val sink: LookupElementSink = sink
        .withPriority(priority)
        .withContributorClass(this@FirCompletionContributorBase.javaClass)

    protected val originalKtFile: KtFile // todo inline
        get() = parameters.originalFile

    protected val project: Project // todo remove entirely
        get() = originalKtFile.project

    protected val targetPlatform = originalKtFile.platform
    protected val symbolFromIndexProvider = KtSymbolFromIndexProvider(parameters.completionFile)
    protected val importStrategyDetector = ImportStrategyDetector(originalKtFile, project)

    protected val scopeNameFilter: (Name) -> Boolean =
        { name -> !name.isSpecial && prefixMatcher.prefixMatches(name.identifier) }

    context(KaSession)
    protected fun createCallableLookupElements(
        context: WeighingContext,
        signature: KaCallableSignature<*>,
        options: CallableInsertionOptions,
        symbolOrigin: CompletionSymbolOrigin,
        presentableText: @NlsSafe String? = null, // TODO decompose
        withTrailingLambda: Boolean = false, // TODO find a better solution
    ): Sequence<LookupElementBuilder> {
        val namedSymbol = when (val symbol = signature.symbol) {
            is KaNamedSymbol -> symbol
            is KaConstructorSymbol -> symbol.containingDeclaration as? KaNamedClassSymbol
            else -> null
        } ?: return emptySequence()

        val shortName = namedSymbol.name

        return sequence {
            KotlinFirLookupElementFactory.createCallableLookupElement(
                name = shortName,
                signature = signature,
                options = options,
                expectedType = context.expectedType,
            ).let { yield(it) }

            if (withTrailingLambda) {
                KotlinFirLookupElementFactory.createCallableLookupElementWithTrailingLambda(
                    name = shortName,
                    signature = signature,
                    options = options,
                )?.let { yield(it) }
            }

            if (namedSymbol is KaNamedFunctionSymbol &&
                namedSymbol.isOperator &&
                namedSymbol.name == OperatorNameConventions.GET &&
                // Only offer the get operator after dot, not for safe access or implicit receivers
                parameters.position.parent?.parent is KtDotQualifiedExpression
            ) {
                KotlinFirLookupElementFactory.createGetOperatorLookupElement(
                    signature = signature,
                    options = options,
                    expectedType = context.expectedType
                ).let { yield(it) }
            }
        }.map { builder ->
            if (presentableText == null) builder
            else builder.withPresentableText(presentableText)
        }.map { lookup ->
            if (!context.isPositionInsideImportOrPackageDirective) {
                lookup.callableWeight = CallableMetadataProvider.getCallableMetadata(
                    signature = signature,
                    symbolOrigin = symbolOrigin,
                    actualReceiverTypes = context.actualReceiverTypes,
                    isFunctionalVariableCall = signature.symbol is KaVariableSymbol
                            && lookup.`object` is FunctionCallLookupObject,
                )
            }

            lookup.applyWeighs(context, KtSymbolWithOrigin(signature.symbol, symbolOrigin))
            lookup.applyKindToPresentation()
        }
    }

    protected fun runChainCompletion(
        positionContext: KotlinNameReferencePositionContext,
        explicitReceiver: KtElement,
        createLookupElements: KaSession.(
            receiverExpression: KtDotQualifiedExpression,
            positionContext: KotlinExpressionNameReferencePositionContext,
            importingStrategy: ImportStrategy.AddImport,
        ) -> Sequence<LookupElement>,
    ) {
        if (!RegistryManager.getInstance().`is`("kotlin.k2.chain.completion.enabled")) return

        sink.runRemainingContributors(parameters.delegate) { completionResult ->
            val lookupElement = completionResult.lookupElement
            val (_, importStrategy) = lookupElement.`object` as? ClassifierLookupObject
                ?: return@runRemainingContributors

            val nameToImport = when (importStrategy) {
                is ImportStrategy.AddImport -> importStrategy.nameToImport
                is ImportStrategy.InsertFqNameAndShorten -> importStrategy.fqName
                ImportStrategy.DoNothing -> null
            } ?: return@runRemainingContributors

            val expression = KtPsiFactory.contextual(explicitReceiver)
                .createExpression(nameToImport.render() + "." + positionContext.nameExpression.text) as KtDotQualifiedExpression

            val receiverExpression = expression.receiverExpression as? KtDotQualifiedExpression
                ?: return@runRemainingContributors

            val nameExpression = expression.selectorExpression as? KtNameReferenceExpression
                ?: return@runRemainingContributors

            analyze(nameExpression) {
                createLookupElements(
                    /* receiverExpression = */ receiverExpression,
                    /* positionContext = */ KotlinExpressionNameReferencePositionContext(nameExpression),
                    /* importingStrategy = */ ImportStrategy.AddImport(nameToImport),
                ).forEach(sink::addElement)
            }
        }
    }

    // todo move out
    // todo move to the corresponding assignment
    protected fun LookupElementBuilder.adaptToExplicitReceiver(
        receiver: KtElement,
        typeText: String,
    ): LookupElement = withInsertHandler { context, item ->
        // Insert type cast if the receiver type does not match.

        val explicitReceiverRange = context.document
            .createRangeMarker(receiver.textRange)
        insertHandler?.handleInsert(context, item)

        val newReceiver = "(${receiver.text} as $typeText)"
        context.document.replaceString(explicitReceiverRange.startOffset, explicitReceiverRange.endOffset, newReceiver)
        context.commitDocument()

        shortenReferencesInRange(
            file = context.file as KtFile,
            selection = explicitReceiverRange.textRange.grown(newReceiver.length),
        )
        context.doPostponedOperationsAndUnblockDocument()
    }

    // todo move to the corresponding assignment
    private fun LookupElementBuilder.applyKindToPresentation(): LookupElementBuilder = when (callableWeight?.kind) {
        // Make the text bold if it's an immediate member of the receiver
        CallableMetadataProvider.CallableKind.THIS_CLASS_MEMBER,
        CallableMetadataProvider.CallableKind.THIS_TYPE_EXTENSION -> bold()

        // Make the text gray
        CallableMetadataProvider.CallableKind.RECEIVER_CAST_REQUIRED -> {
            val presentation = LookupElementPresentation().apply {
                renderElement(this)
            }

            withTailText(presentation.tailText, true)
                .withItemTextForeground(KOTLIN_CAST_REQUIRED_COLOR)
        }

        else -> this
    }
}
