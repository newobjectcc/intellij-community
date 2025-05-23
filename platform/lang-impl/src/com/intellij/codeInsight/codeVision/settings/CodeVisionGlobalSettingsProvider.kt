// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.codeInsight.codeVision.settings

import com.intellij.codeInsight.codeVision.CodeVisionAnchorKind
import com.intellij.codeInsight.codeVision.CodeVisionBundle
import com.intellij.codeInsight.hints.InlayGroup
import com.intellij.codeInsight.hints.settings.InlayGroupSettingProvider
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class CodeVisionGlobalSettingsProvider : InlayGroupSettingProvider {

  companion object {
    val defaultAnchors: List<CodeVisionAnchorKind> = listOf(CodeVisionAnchorKind.Top, CodeVisionAnchorKind.Right)
    val supportedAnchors: List<CodeVisionAnchorKind> = defaultAnchors + CodeVisionAnchorKind.Default
  }

  private val settings = CodeVisionSettings.getInstance()

  override var isEnabled: Boolean
    get() = settings.codeVisionEnabled
    set(value) {
      settings.codeVisionEnabled = value
    }

  private lateinit var defaultPositionComboBox: ComboBox<CodeVisionAnchorKind>
  private lateinit var visibleMetricsAbove: JBIntSpinner
  private lateinit var visibleMetricsNext: JBIntSpinner

  override val group: InlayGroup = InlayGroup.CODE_VISION_GROUP_NEW

  override val component: DialogPanel = panel {
    row(CodeVisionBundle.message("CodeLensGlobalSettingsProvider.defaultPosition.description")) {
      defaultPositionComboBox = comboBox(defaultAnchors, renderer = textListCellRenderer("") { CodeVisionBundle.message(it.key) })
        .component
    }
    row(CodeVisionBundle.message("CodeLensGlobalSettingsProvider.visibleMetricsAbove.description")) {
      visibleMetricsAbove = spinner(1..10, 1).component
    }
    row(CodeVisionBundle.message("CodeLensGlobalSettingsProvider.visibleMetricsNext.description")) {
      visibleMetricsNext = spinner(1..10, 1).component
    }
  }

  override fun isModified(): Boolean {
    return isEnabled != settings.codeVisionEnabled
           || settings.defaultPosition != defaultPositionComboBox.item
           || settings.visibleMetricsAboveDeclarationCount != visibleMetricsAbove.number
           || settings.visibleMetricsNextToDeclarationCount != visibleMetricsNext.number
  }

  override fun apply() {
    settings.codeVisionEnabled = isEnabled
    settings.defaultPosition = defaultPositionComboBox.item
    settings.visibleMetricsAboveDeclarationCount = visibleMetricsAbove.number
    settings.visibleMetricsNextToDeclarationCount = visibleMetricsNext.number
  }

  override fun reset() {
    settings.codeVisionEnabled = isEnabled
    defaultPositionComboBox.item = settings.defaultPosition
    visibleMetricsAbove.number = settings.visibleMetricsAboveDeclarationCount
    visibleMetricsNext.number = settings.visibleMetricsNextToDeclarationCount
  }

}