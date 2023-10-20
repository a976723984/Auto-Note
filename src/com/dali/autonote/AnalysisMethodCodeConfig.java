package com.dali.autonote;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnalysisMethodCodeConfig implements Configurable {
    private JTextField apiKeyField;
    private JTextField moduleField;
    private JTextField proxyHostField;
    private JTextField proxyPortField;

    private static String apiKey = "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";
    private static String proxyHost = "127.0.0.1";
    private static Integer proxyPort = 18083;
    private static String module = "gpt-3.5-turbo";

    @Override
    public String getDisplayName() {
        return "Auto Note";
    }

    @Override
    public JComponent createComponent() {
        init();

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(2, 4));
        JLabel apiKeyLabel = new JLabel("ChatGPT API Key：", JLabel.LEFT);
        apiKeyField = new JTextField(30);
        if (apiKey != null) {
            apiKeyField.setText(apiKey);
        }
        jPanel.add(apiKeyLabel);
        jPanel.add(apiKeyField);

        JLabel moduleLabel = new JLabel("Module：", JLabel.LEFT);
        moduleField = new JTextField(10);
        if (module != null) {
            moduleField.setText(module);
        }
        jPanel.add(moduleLabel);
        jPanel.add(moduleField);

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
        module = moduleField.getText();
        persist();
    }

    public void init() {
        try {
            String path = PathManager.getConfigPath() + "/auto-note/settings.txt";
            if (FileUtil.exists(path)) {
                BufferedReader reader = new BufferedReader(new FileReader(path));

                for (int i = 0; i < 4; i++) {
                    String value = reader.readLine();
                    switch (i) {
                        case 0: {
                            apiKey = value;
                        }
                        break;
                        case 1: {
                            proxyHost = value;
                        }
                        break;
                        case 2: {
                            proxyPort = Integer.parseInt(value);
                        }
                        break;
                        case 3: {
                            module = value;
                        }
                        break;
                        default:
                    }
                }
                reader.close();
            }
        } catch (Exception e) {
            Messages.showMessageDialog("init configuration error" + e.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    public void persist() {
        try {
            String value = String.join("\n", apiKey, proxyHost, String.valueOf(proxyPort), module);
            String path = PathManager.getConfigPath() + "/auto-note";
            if (!FileUtil.exists(path)) {
                Files.createDirectories(Paths.get(path));
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/settings.txt"));
            writer.write(value, 0, value.length());
            writer.close();
        } catch (Exception e) {
            Messages.showMessageDialog("persist configuration error" + e.getMessage(), "Error", Messages.getErrorIcon());
        }
    }

    public static String getApiKey() {
        if (apiKey == null || apiKey.length() == 0) {
            Messages.showMessageDialog("Please set ChatGPT API Key", "Error", Messages.getErrorIcon());
        }
        return apiKey;
    }

    public static String getProxyHost() {
        if (proxyHost == null || proxyHost.length() == 0) {
            Messages.showMessageDialog("Please set proxy host", "Error", Messages.getErrorIcon());
        }

        return proxyHost;
    }

    public static Integer getProxyPort() {
        if (proxyPort == null) {
            Messages.showMessageDialog("Please set proxy port", "Error", Messages.getErrorIcon());
        }
        return proxyPort;
    }

    public static String getModule() {
        if (module == null) {
            Messages.showMessageDialog("Please set module", "Error", Messages.getErrorIcon());
        }
        return module;
    }
}
