// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.refactoring.inline;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.find.FindBundle;
import com.intellij.lang.Language;
import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.lang.refactoring.InlineHandlers;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import com.intellij.refactoring.util.NonCodeUsageInfo;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

import static com.intellij.openapi.util.NlsContexts.DialogMessage;

@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
public final class GenericInlineHandler {
  private static final Logger LOG = Logger.getInstance(GenericInlineHandler.class);

  public static boolean invoke(final PsiElement element, @Nullable Editor editor, final InlineHandler languageSpecific) {
    final PsiReference invocationReference = editor != null ? TargetElementUtil.findReference(editor) : null;
    final InlineHandler.Settings settings = languageSpecific.prepareInlineElement(element, editor, invocationReference != null);
    if (settings == null || settings == InlineHandler.Settings.CANNOT_INLINE_SETTINGS) {
      return settings != null;
    }

    Project project = element.getProject();

    final Collection<? extends PsiReference> allReferences;

    if (settings.isOnlyOneReferenceToInline()) {
      allReferences = Collections.singleton(invocationReference);
    }
    else {
      allReferences = ProgressManager.getInstance().runProcessWithProgressSynchronously(
        () -> ReferencesSearch.search(element).findAll(),
        FindBundle.message("find.usages.progress.title"), true, project);
    }

    final MultiMap<PsiElement, String> conflicts = new MultiMap<>();
    final Map<Language, InlineHandler.Inliner> inliners = initializeInliners(element, settings, allReferences);

    for (PsiReference reference : allReferences) {
      collectConflicts(reference, element, inliners, conflicts);
    }

    if (!BaseRefactoringProcessor.processConflicts(project, conflicts)) return true;

    HashSet<PsiElement> elements = new HashSet<>();
    for (PsiReference reference : allReferences) {
      elements.add(reference.getElement());
    }
    if (!settings.isOnlyOneReferenceToInline()) {
      elements.add(element);
    }

    if (!CommonRefactoringUtil.checkReadOnlyStatusRecursively(project, elements, true)) {
      return true;
    }
    String subj = element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : "element";
    String commandName = RefactoringBundle.message("inline.command", StringUtil.notNullize(subj, "<nameless>"));
    WriteCommandAction.runWriteCommandAction(
      project, commandName, null, () -> {
        final PsiReference[] references = sortDepthFirstRightLeftOrder(allReferences);


        final UsageInfo[] usages = new UsageInfo[references.length];
        for (int i = 0; i < references.length; i++) {
          usages[i] = new UsageInfo(references[i]);
        }

        Consumer<ProgressIndicator> perform = indicator -> {
          indicator.setIndeterminate(false);
          for (int i = 0; i < usages.length; i++) {
            indicator.setFraction((double) i / usages.length);
            inlineReference(usages[i], element, inliners);
          }

          if (!settings.isOnlyOneReferenceToInline()) {
            languageSpecific.removeDefinition(element, settings);
          }
        };
        ApplicationManagerEx.getApplicationEx()
          .runWriteActionWithNonCancellableProgressInDispatchThread(commandName, project, null, perform);
      });
    return true;
  }

  public static Map<Language, InlineHandler.Inliner> initializeInliners(PsiElement element,
                                                                        InlineHandler.Settings settings,
                                                                        Collection<? extends PsiReference> allReferences) {
    final Map<Language, InlineHandler.Inliner> inliners = new HashMap<>();
    for (PsiReference ref : allReferences) {
      if (ref == null) {
        LOG.error("element: " + element.getClass()+ ", allReferences contains null!");
        continue;
      }
      PsiElement refElement = ref.getElement();

      final Language language = refElement.getLanguage();
      if (inliners.containsKey(language)) continue;

      final List<InlineHandler> handlers = InlineHandlers.getInlineHandlers(language);
      for (InlineHandler handler : handlers) {
        InlineHandler.Inliner inliner = handler.createInliner(element, settings);
        if (inliner != null) {
          inliners.put(language, inliner);
          break;
        }
      }
    }
    return inliners;
  }

  public static Map<Language, InlineHandler.Inliner> initInliners(PsiElement elementToInline,
                                                                  UsageInfo[] usagesIn,
                                                                  InlineHandler.Settings settings,
                                                                  MultiMap<PsiElement, @DialogMessage String> conflicts,
                                                                  Language... emptyInliners) {
    ArrayList<PsiReference> refs = new ArrayList<>();
    for (UsageInfo info : usagesIn) {
      if (info instanceof NonCodeUsageInfo) continue;
      PsiElement element = info.getElement();
      if (element != null) {
        PsiReference[] references = element.getReferences();
        if (references.length > 0) {
          refs.add(references[0]);
        }
      }
    }

    Map<Language, InlineHandler.Inliner> inliners = initializeInliners(elementToInline, settings, refs);
    for (Language language : emptyInliners) {
      inliners.put(language, new InlineHandler.Inliner() {
        @Override
        public @Nullable MultiMap<PsiElement, String> getConflicts(@NotNull PsiReference reference, @NotNull PsiElement referenced) {
          return null;
        }

        @Override
        public void inlineUsage(@NotNull UsageInfo usage, @NotNull PsiElement referenced) { }
      });
    }

    for (PsiReference ref : refs) {
      collectConflicts(ref, elementToInline, inliners, conflicts);
    }

    return inliners;
  }

  public static void collectConflicts(PsiReference reference,
                                      PsiElement element,
                                      Map<Language, InlineHandler.Inliner> inliners,
                                      MultiMap<PsiElement, @DialogMessage String> conflicts) {
    final PsiElement referenceElement = reference.getElement();
    final Language language = referenceElement.getLanguage();
    final InlineHandler.Inliner inliner = inliners.get(language);
    if (inliner != null) {
      final MultiMap<PsiElement, String> refConflicts = inliner.getConflicts(reference, element);
      if (refConflicts != null) {
        for (PsiElement psiElement : refConflicts.keySet()) {
          conflicts.putValues(psiElement, refConflicts.get(psiElement));
        }
      }
    }
    else {
      conflicts.putValue(referenceElement,
                         RefactoringBundle.message("dialog.message.cannot.inline.reference.from.0", language.getDisplayName()));
    }
  }

  public static void inlineReference(final UsageInfo usage,
                                     final PsiElement element,
                                     final Map<Language, InlineHandler.Inliner> inliners) {
    PsiElement usageElement = usage.getElement();
    if (usageElement == null) return;
    final Language language = usageElement.getLanguage();
    final InlineHandler.Inliner inliner = inliners.get(language);
    if (inliner != null) {
      inliner.inlineUsage(usage, element);
    }
  }

  //order of usages across different files is irrelevant
  public static PsiReference[] sortDepthFirstRightLeftOrder(final Collection<? extends PsiReference> allReferences) {
    final PsiReference[] usages = allReferences.toArray(PsiReference.EMPTY_ARRAY);
    Arrays.sort(usages, (usage1, usage2) -> {
      final PsiElement element1 = usage1.getElement();
      final PsiElement element2 = usage2.getElement();
      return element2.getTextRange().getStartOffset() - element1.getTextRange().getStartOffset();
    });
    return usages;
  }
}
