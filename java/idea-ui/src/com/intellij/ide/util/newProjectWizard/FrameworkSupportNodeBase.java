// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.ide.util.newProjectWizard;

import com.intellij.diagnostic.PluginException;
import com.intellij.framework.FrameworkOrGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.util.ui.EmptyIcon;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class FrameworkSupportNodeBase<T extends FrameworkOrGroup> extends CheckedTreeNode {
  private static final Logger LOG = Logger.getInstance(FrameworkSupportNodeBase.class);
  private final FrameworkSupportNodeBase<?> myParentNode;

  public FrameworkSupportNodeBase(T userObject, final FrameworkSupportNodeBase<?> parentNode) {
    super(userObject);
    setChecked(false);
    myParentNode = parentNode;
    if (parentNode != null) {
      parentNode.add(this);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getUserObject() {
    return (T)super.getUserObject();
  }

  @Contract(mutates = "param1")
  public static void sortByName(@Nullable List<? extends FrameworkSupportNodeBase<?>> nodes, final @Nullable Comparator<? super FrameworkSupportNodeBase<?>> comparator) {
    if (nodes == null) return;

    nodes.sort((o1, o2) -> {
      if (comparator != null) {
        int compare = comparator.compare(o1, o2);
        if (compare != 0) return compare;
      }
      if (o1 instanceof FrameworkGroupNode && !(o2 instanceof FrameworkGroupNode)) return -1;
      if (o2 instanceof FrameworkGroupNode && !(o1 instanceof FrameworkGroupNode)) return 1;
      if (o1.getChildCount() < o2.getChildCount()) return 1;
      if (o1.getChildCount() > o2.getChildCount()) return -1;
      return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    });
    for (FrameworkSupportNodeBase<?> node : nodes) {
      @SuppressWarnings("unchecked") 
      List<FrameworkSupportNodeBase<?>> children = (List<FrameworkSupportNodeBase<?>>)(List<?>)node.children;
      sortByName(children, null);
    }
  }

  protected final @NotNull @NlsContexts.Label String getTitle() {
    return getUserObject().getPresentableName();
  }

  public final @NotNull Icon getIcon() {
    Icon icon = getUserObject().getIcon();
    //noinspection ConstantConditions
    if (icon == null) {
      Class<?> aClass = getUserObject().getClass();
      PluginException.logPluginError(LOG, "FrameworkOrGroup::getIcon returns null for " + aClass, null, aClass);
      return EmptyIcon.ICON_16;
    }
    return icon;
  }

  public final @NotNull String getId() {
    return getUserObject().getId();
  }

  @SuppressWarnings("unchecked")
  public @NotNull List<FrameworkSupportNodeBase<?>> getChildren() {
    return children != null ? (List<FrameworkSupportNodeBase<?>>)(List<?>)children : Collections.emptyList();
  }

  public FrameworkSupportNodeBase<?> getParentNode() {
    return myParentNode;
  }
}
