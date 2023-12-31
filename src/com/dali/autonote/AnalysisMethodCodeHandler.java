package com.dali.autonote;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.InvalidDiffRequestException;
import com.intellij.diff.actions.impl.MutableDiffRequestChain;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AnalysisMethodCodeHandler extends AnAction {

    private final static String BASE_PROMPT = "Analyze the function of the code line by line and add comments line by line based on the text content. " +
            "Your answer needs to follow the following rules: 1. The annotation content needs to be in Chinese. 2. Except for the source code and adding comments, " +
            "no additional content should be replied to. 3. The annotation content needs to be as detailed and clear as possible.";

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Waiting", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Waiting for analysis...");
                ApplicationManager.getApplication().runReadAction(() -> {
                    String value = analysis(anActionEvent);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        try {
                            show(anActionEvent, value);
                        } catch (InvalidDiffRequestException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    });
                });
            }
        });
    }

    private void show(AnActionEvent anActionEvent, String contentStr) throws InvalidDiffRequestException {
        Project project = anActionEvent.getProject();
        if (project == null) {
            Messages.showMessageDialog("Project is null", "Error", Messages.getErrorIcon());
            return;
        }
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        if (psiFileFactory == null) {
            Messages.showMessageDialog("PsiFileFactory is null", "Error", Messages.getErrorIcon());
            return;
        }
        Editor editor = (Editor) anActionEvent.getDataContext().getData("editor");
        if (editor == null) {
            Messages.showMessageDialog("Editor is null", "Error", Messages.getErrorIcon());
            return;
        }
        PsiFile psiFile = (PsiFile) anActionEvent.getDataContext().getData("psi.File");
        if (psiFile == null) {
            Messages.showMessageDialog("PsiFile is null", "Error", Messages.getErrorIcon());
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method == null) {
            Messages.showMessageDialog("Method is null", "Error", Messages.getErrorIcon());
            return;
        }

        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            Messages.showMessageDialog("Document is null", "Error", Messages.getErrorIcon());
            return;
        }

        MutableDiffRequestChain chain = BaseShowDiffAction.createMutableChainFromFiles(project, method.getText(), contentStr, psiFile);

        DiffManager.getInstance().showDiff(project, chain, DiffDialogHints.DEFAULT);
    }

    /**
     * 获取唯一标识名称
     *
     * @param psiMethod
     * @return
     */
    private String getMethodName(PsiMethod psiMethod) {
        // 获取包含该方法的类的完全限定名
        // 应用IntelliJ IDEA的程序结构接口（PSI），获取该方法所在类的全限定名称，例如：com.example.MyClass
        String qualifiedClassName = psiMethod.getContainingClass().getQualifiedName();

        // 获取方法名
        // 获取当前psiMethod对象代表的方法的名称
        String methodName = psiMethod.getName();

        // 获取参数类型列表
        // 使用getParameters()方法获取该方法的参数列表，并将其转化为数组形式
        PsiParameter[] parameters = psiMethod.getParameterList().getParameters();

        // 让参数类型列表流，并获取其类型的全名，最后将其连接成一个以逗号分隔的字符串
        // 例如："int,String"
        String parameterTypes = Arrays.stream(parameters)
                .map(p -> p.getType().getCanonicalText())
                .collect(Collectors.joining(","));

        // 组合以上信息生成唯一标识
        // 将全限定类名，方法名和参数类型列表以特定格式拼接在一起，形成一个唯一的方法签名字符串，例如：
        // "com.example.MyClass.methodName(int,String)"
        return qualifiedClassName + "." + methodName + "(" + parameterTypes + ")";
    }

    public String analysis(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            throw new RuntimeException("project is null");
        }
        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
        if (psiFileFactory == null) {
            throw new RuntimeException("psiFileFactory is null");
        }
        Editor editor = (Editor) anActionEvent.getDataContext().getData("editor");
        if (editor == null) {
            throw new RuntimeException("editor is null");
        }
        PsiFile psiFile = (PsiFile) anActionEvent.getDataContext().getData("psi.File");
        if (psiFile == null) {
            throw new RuntimeException("psiFile is null");
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method == null) {
            throw new RuntimeException("method is null");
        }
        String methodName = getMethodName(method);
        String value = AnalysisMethodResultHolder.getInstance().get(methodName);
        if (value == null || value.length() == 0) {
            value = doAnalysis(method.getText());
            AnalysisMethodResultHolder.getInstance().put(methodName, value);
        }
        return value;
    }

    public String doAnalysis(String content) {
        String url = "https://api.openai.com/v1/chat/completions";
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.setHttpConnectionManager(new SimpleHttpConnectionManager());

            // 添加请求头
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Authorization", "Bearer " + AnalysisMethodCodeConfig.getApiKey());
            post.addRequestHeader("Content-Type", "application/json");

            ChatGptMessage systemMessage = ChatGptMessage.of(ChatGptQueryDTO.Role.SYSTEM, BASE_PROMPT);
            ChatGptMessage userMessage = ChatGptMessage.of(ChatGptQueryDTO.Role.USER, content);
            ChatGptQueryDTO query = ChatGptQueryDTO.of(AnalysisMethodCodeConfig.getModule(), Arrays.asList(systemMessage, userMessage));

            post.setRequestEntity(new StringRequestEntity(ObjectMapperUtil.getInstance().writeValueAsString(query), "application/json", "UTF-8"));

            HostConfiguration configuration = new HostConfiguration();
            configuration.setProxy(AnalysisMethodCodeConfig.getProxyHost(), AnalysisMethodCodeConfig.getProxyPort());
            // 执行请求
            int statusCode = httpClient.executeMethod(configuration, post);

            // 获取并处理响应
            if (statusCode == 200) {
                ChatGptResultDTO result = ObjectMapperUtil.getInstance().readValue(IOUtils.toString(post.getResponseBodyAsStream(), "UTF-8"), ChatGptResultDTO.class);
                ChatGptResultDTO.Choice choice = result.getChoices().stream().findAny().orElse(null);
                if (choice == null) {
                    return "分析无结果";
                }
                ChatGptMessage message = choice.getMessage();
                return message == null ? "分析无结果" : message.getContent();
            } else {
                throw new RuntimeException("网络异常" + statusCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("分析失败，" + e.getMessage(), e);
        }
    }
}
