package com.chave.ui;

import burp.api.montoya.MontoyaApi;
import lombok.Data;

import javax.swing.*;

@Data
public class MainUI {
    private JPanel root;

    public MainUI(MontoyaApi api) {
        this.root = new JPanel();
    }
}
