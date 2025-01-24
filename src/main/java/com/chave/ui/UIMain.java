package com.chave.ui;

import burp.api.montoya.MontoyaApi;
import lombok.Data;

import javax.swing.*;

@Data
public class UIMain {
    private JPanel root;

    public UIMain(MontoyaApi api) {
        this.root = new JPanel();
    }
}
