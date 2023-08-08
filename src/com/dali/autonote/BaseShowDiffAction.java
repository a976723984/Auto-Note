package com.dali.autonote;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.DiffUserDataKeysEx;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseShowDiffAction {
    @NotNull
    protected static MutableDiffRequestChain createMutableChainFromFiles(@Nullable Project project,
                                                                         @NotNull String context1,
                                                                         @NotNull String context2,
                                                                         @NotNull PsiFile baseFile) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();

        DiffContent content1 = contentFactory.create(project, context1);
        DiffContent content2 = contentFactory.create(project, context2);

        Document document = PsiDocumentManager.getInstance(project).getDocument(baseFile);
        DiffContent diffContent = DiffContentFactory.getInstance().create(project, document, baseFile.getFileType());

        MutableDiffRequestChain chain = new MutableDiffRequestChain(content1, diffContent, content2);

//        chain.putUserData(DiffUserDataKeysEx.SCROLL_TO_LINE, );

        chain.setWindowTitle("compare");
        return chain;
    }
}
