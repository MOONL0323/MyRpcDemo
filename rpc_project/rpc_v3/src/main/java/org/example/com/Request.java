package org.example.com;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Request implements Serializable {
    private String InterfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
}
