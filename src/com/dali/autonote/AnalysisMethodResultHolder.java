package com.dali.autonote;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AnalysisMethodResultHolder extends AnAction {
    private final static Map<String, String> analysisResultMap = new HashMap<>();

    public static Map<String, String> getInstance() {
        return analysisResultMap;
    }

    public static void clear() {
        analysisResultMap.clear();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        clear();
    }
}
