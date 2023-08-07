package com.dali.autonote;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AnalysisMethodCodeConfig implements Configurable {
    private JTextField apiKeyField;
    private JTextField proxyHostField;
    private JTextField proxyPortField;

    private static String apiKey = null;
    private static String proxyHost = null;
    private static Integer proxyPort = null;
    private final static String testApiKey = "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";

    @Override
    public String getDisplayName() {
        return "Auto Note";
    }

    @Override
    public JComponent createComponent() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        JLabel apiKeyLabel = new JLabel("ChatGPT API Key：", JLabel.LEFT);
        apiKeyField = new JTextField(20);
        if (apiKey != null) {
            apiKeyField.setText(apiKey);
        }
        jPanel.add(apiKeyLabel);
        jPanel.add(apiKeyField);

        JLabel proxyHostLabel = new JLabel("Proxy Host：", JLabel.LEFT);
        proxyHostField = new JTextField(20);
        if (proxyHost != null) {
            proxyHostField.setText(proxyHost);
        }
        jPanel.add(proxyHostLabel);
        jPanel.add(proxyHostField);

        JLabel proxyPortLabel = new JLabel("Proxy Port：", JLabel.LEFT);
        proxyPortField = new JTextField(20);
        if (proxyPort != null) {
            proxyPortField.setText(proxyPort.toString());
        }
        jPanel.add(proxyPortLabel);
        jPanel.add(proxyPortField);

        return jPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        apiKey = apiKeyField.getText();
        proxyHost = proxyHostField.getText();
        proxyPort = Integer.parseInt(proxyPortField.getText());
    }

    public static String getApiKey() {
        apiKey = testApiKey;
        if (apiKey == null || apiKey.length() == 0) {
            Messages.showMessageDialog("Please set ChatGPT API Key", "Error", Messages.getErrorIcon());
        }
        return apiKey;
    }

    public static String getProxyHost() {
        proxyHost = "127.0.0.1";
        if (proxyHost == null || proxyHost.length() == 0) {
            Messages.showMessageDialog("Please set proxy host", "Error", Messages.getErrorIcon());
        }

        return proxyHost;
    }

    public static Integer getProxyPort() {
        proxyPort = 18083;
        if (proxyPort == null) {
            Messages.showMessageDialog("Please set proxy port", "Error", Messages.getErrorIcon());
        }
        return proxyPort;
    }
}
