package com.chave.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIItem {
    private String method;
    private String path;
    private String result;
    private Boolean state = Boolean.FALSE;
    private String note;
    private URL domain;

    public APIItem(String method, String path) {
        this.method = method;
        this.path = path;
    }
}
