package com.dali.autonote;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseShowDiffAction {
    @NotNull
    protected static MutableDiffRequestChain createMutableChainFromFiles(@Nullable Project project,
                                                                         @NotNull String context1,
                                                                         @NotNull String context2) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();

        DiffContent content1 = contentFactory.create(project, context1);
        DiffContent content2 = contentFactory.create(project, context2);

        MutableDiffRequestChain chain;
        chain = new MutableDiffRequestChain(content1, null, content2);

        chain.setWindowTitle("compare");
        return chain;
    }
}
