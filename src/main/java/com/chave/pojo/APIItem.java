package com.chave.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIItem {
    private String method = null;
    private String path;
    private String result;
    private Boolean state = Boolean.FALSE;
    private String note;
    private String isFound;

    public APIItem(String path) {
        this.path = path;
    }

    public APIItem(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        APIItem item = (APIItem) o;

        if (item.getMethod() == null) {
            if (this.method == null) {
                if (item.getPath().equalsIgnoreCase(this.path)) {
                    return true;
                }

                return false;
            } else {
                return false;
            }
        } else {
            if (this.method == null) {
                return false;
            } else {
                if (item.getMethod().equalsIgnoreCase(this.method) && item.getPath().equalsIgnoreCase(this.path)) {
                    return true;
                }

                return false;
            }
        }
    }

}
