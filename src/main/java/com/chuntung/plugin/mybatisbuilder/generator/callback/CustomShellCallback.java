/*
 * Copyright (c) 2019 Tony Ho. Some rights reserved.
 */

package com.chuntung.plugin.mybatisbuilder.generator.callback;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Custom shell callback to support auto merge.
 *
 * @author Tony Ho
 */
public class CustomShellCallback extends DefaultShellCallback {
    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite the overwrite
     */
    public CustomShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource,
                                File existingFile, String[] javadocTags, String fileEncoding)
            throws ShellException {
        // only support interface file
        if (!newFileSource.contains("public interface")) {
            return newFileSource;
        }

        ParseResult<CompilationUnit> newParseResult = new JavaParser().parse(newFileSource);
        ParseResult<CompilationUnit> oldParseResult = null;
        try {
            oldParseResult = new JavaParser().parse(existingFile);
            if (oldParseResult.getProblems().size() > 0) {
                throw new ShellException(Arrays.toString(oldParseResult.getProblems().toArray()));
            }
        } catch (FileNotFoundException e) {
            throw new ShellException(e.getMessage());
        }

        return mergerFile(newParseResult.getResult().get(), oldParseResult.getResult().get());
    }

    public String mergerFile(CompilationUnit newCompilationUnit, CompilationUnit oldCompilationUnit) {
        NodeList<ImportDeclaration> oldImports = oldCompilationUnit.getImports();
        NodeList<ImportDeclaration> newImports = newCompilationUnit.getImports();

        LinkedHashSet<ImportDeclaration> customImportSet = new LinkedHashSet<>(oldImports);
        customImportSet.removeAll(newImports);
        for (ImportDeclaration importDeclaration : customImportSet) {
            newCompilationUnit.addImport(importDeclaration);
        }

        List<MethodDeclaration> oldMethods = oldCompilationUnit.getType(0).getMethods();
        TypeDeclaration<?> typeDeclaration = newCompilationUnit.getType(0);
        List<MethodDeclaration> newMethods = typeDeclaration.getMethods();

        LinkedHashSet<MethodDeclaration> customMethodSet = new LinkedHashSet<>(oldMethods);
        customMethodSet.removeAll(newMethods);
        for (MethodDeclaration methodDeclaration : customMethodSet) {
            if (methodDeclaration.getComment().isPresent()) {
                if (methodDeclaration.getComment().get().getContent().contains(MergeConstants.NEW_ELEMENT_TAG)) {
                    // skip generated methods
                    continue;
                }
            }

            typeDeclaration.addMember(methodDeclaration);
        }

        return newCompilationUnit.toString();
    }
}