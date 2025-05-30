// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.openapi.roots;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.EmptyQuery;
import com.intellij.util.Query;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides a possibility to query the directories corresponding to a specific Java package name.
 *
 * @see ModulePackageIndex
 */
public abstract class PackageIndex {
  public static PackageIndex getInstance(@NotNull Project project) {
    return project.getService(PackageIndex.class);
  }

  /**
   * Returns all directories in content sources and libraries (and optionally library sources)
   * corresponding to the given package name.
   *
   * @param packageName           the name of the package for which directories are requested.
   * @param includeLibrarySources if true, directories under library sources are included in the returned list.
   * @return the array of directories.
   */
  public abstract VirtualFile @NotNull [] getDirectoriesByPackageName(@NotNull @NlsSafe String packageName, boolean includeLibrarySources);

  /**
   * @return all directories in the given scope corresponding to the given package name. Note that package may also contain
   * single file source roots. Use {@link #getFilesByPackageName(String)} to get them.
   */
  public Query<VirtualFile> getDirsByPackageName(@NotNull @NlsSafe String packageName, @NotNull GlobalSearchScope scope) {
    return getDirsByPackageName(packageName, true).filtering(scope::contains);
  }

  /**
   * @return Returns a query producing single file source root files which correspond to {@code packageName}.
   */
  @ApiStatus.Experimental
  public Query<VirtualFile> getFilesByPackageName(@NotNull @NlsSafe String packageName) { 
    return EmptyQuery.getEmptyQuery();
  }

  /**
   * Returns all directories in content sources and libraries (and optionally library sources)
   * corresponding to the given package name as a query object (allowing to perform partial iteration of the results).
   *
   * @param packageName           the name of the package for which directories are requested.
   * @param includeLibrarySources if true, directories under library sources are included in the returned list.
   * @return the query returning the list of directories.
   */
  public abstract @NotNull Query<VirtualFile> getDirsByPackageName(@NotNull @NlsSafe String packageName, boolean includeLibrarySources);

  /**
   * Returns the name of the package corresponding to the specified directory or a specific file if the file is a single-file root.
   *
   * @return the package name, or null if the supplied directory does not correspond to any package, 
   * or the supplied file is not a single-file root.
   */
  public abstract @Nullable String getPackageNameByDirectory(@NotNull VirtualFile dir);
}
