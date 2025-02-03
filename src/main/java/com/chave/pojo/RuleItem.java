package com.chave.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RuleItem implements Serializable {
    private String name;
    private Boolean loaded;
    private String regex;
    private String scope;
}
