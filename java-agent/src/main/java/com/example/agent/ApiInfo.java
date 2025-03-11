package com.example.agent;

import java.util.List;
import java.util.Map;

/**
 * API 信息封装类
 */
public class ApiInfo {
    private String className; // 类名
    private String methodName; // 方法名
    private String returnType; // 返回类型
    private String[] parameterTypes; // 参数类型
    private String httpMethod; // HTTP 方法
    private String apiPath; // API 路径
    private List<Map<String, Object>> requestParameters; // 请求参数
    private Map<String, Map<String, Object>> returnValue; // 返回值

    // 构造方法
    public ApiInfo(String className, String methodName, String returnType, String[] parameterTypes, String httpMethod, String apiPath, List<Map<String, Object>> requestParameters, Map<String, Map<String, Object>> returnValue) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.httpMethod = httpMethod;
        this.apiPath = apiPath;
        this.requestParameters = requestParameters;
        this.returnValue = returnValue;
    }

    // Getters and Setters
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public List<Map<String, Object>> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(List<Map<String, Object>> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public Map<String, Map<String, Object>> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Map<String, Map<String, Object>> returnValue) {
        this.returnValue = returnValue;
    }
}