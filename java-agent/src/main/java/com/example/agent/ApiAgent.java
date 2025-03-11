package com.example.agent;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

/**
 * Java Agent 类，用于获取 API 信息并保存为 JSON 文件
 */
public class ApiAgent {

    // 启动时加载
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent loaded at startup!");
        getApiInfo(inst, "startup");
    }

    // 运行时加载
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("Agent loaded dynamically!");
        getApiInfo(inst, "runtime");
    }

    /**
     * 获取所有加载类的 API 信息
     *
     * @param inst     Instrumentation 对象
     * @param loadMode 加载模式（startup 或 runtime）
     */
    private static void getApiInfo(Instrumentation inst, String loadMode) {
        List<ApiInfo> apiList = new ArrayList<>();
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            // 获取类的方法信息
            for (Method method : clazz.getDeclaredMethods()) {
                ApiInfo apiInfo = new ApiInfo(
                        clazz.getName(), // 类名
                        method.getName(), // 方法名
                        method.getReturnType().getName(), // 返回类型
                        getParameterTypes(method.getParameterTypes()), // 参数类型
                        getHttpMethod(method), // HTTP 方法
                        getApiPath(clazz, method), // API 路径
                        getRequestParameters(method), // 请求参数
                        getReturnValue(method) // 返回值
                );
                apiList.add(apiInfo);
            }
        }
        // 将 API 信息保存为 JSON 文件
        saveToJson(apiList, loadMode);
    }

    // 获取参数类型
    private static String[] getParameterTypes(Class<?>[] parameterTypes) {
        String[] types = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            types[i] = parameterTypes[i].getName();
        }
        return types;
    }

    // 获取 HTTP 方法
    private static String getHttpMethod(Method method) {
        // 检查是否存在 @GetMapping 注解
        if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        }
        // 检查是否存在 @PostMapping 注解
        if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        }
        // 检查是否存在 @PutMapping 注解
        if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        }
        // 检查是否存在 @DeleteMapping 注解
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        }
        // 检查是否存在 @RequestMapping 注解
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            // 获取 method 属性
            RequestMethod[] methods = requestMapping.method();
            if (methods.length > 0) {
                return methods[0].name();
            }
        }
        // 默认返回 POST
        return "POST";
    }

    // 获取 API 路径
    private static String getApiPath(Class<?> clazz, Method method) {
        StringBuilder path = new StringBuilder();

        // 获取类级别的 @RequestMapping 注解
        if (clazz.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
            String[] classPaths = classRequestMapping.value();
            if (classPaths.length > 0) {
                path.append(classPaths[0]);
            }
        }

        // 获取方法级别的 @RequestMapping 注解
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
            String[] methodPaths = methodRequestMapping.value();
            if (methodPaths.length > 0) {
                path.append(methodPaths[0]);
            }
        }

        // 获取方法级别的其他注解（如 @GetMapping, @PostMapping 等）
        if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            String[] paths = getMapping.value();
            if (paths.length > 0) {
                path.append(paths[0]);
            }
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            String[] paths = postMapping.value();
            if (paths.length > 0) {
                path.append(paths[0]);
            }
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            String[] paths = putMapping.value();
            if (paths.length > 0) {
                path.append(paths[0]);
            }
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            String[] paths = deleteMapping.value();
            if (paths.length > 0) {
                path.append(paths[0]);
            }
        }

        // 如果路径为空，返回默认路径
        if (path.length() == 0) {
            return "/default/path";
        }

        return path.toString();
    }

    // 获取请求参数
    private static List<Map<String, Object>> getRequestParameters(Method method) {
        List<Map<String, Object>> parameters = new ArrayList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 遍历方法参数
        for (int i = 0; i < parameterTypes.length; i++) {
            Map<String, Object> param = new HashMap<>();
            param.put("name", "param" + i); // 参数名
            param.put("type", parameterTypes[i].getName()); // 参数类型

            // 检查是否存在 @RequestParam 注解
            if (method.getParameterAnnotations()[i].length > 0) {
                for (Annotation annotation : method.getParameterAnnotations()[i]) {
                    if (annotation instanceof RequestParam) {
                        RequestParam requestParam = (RequestParam) annotation;
                        param.put("in", "query");
                        param.put("name", requestParam.value());
                        param.put("required", requestParam.required());
                    }
                }
            }

            // 检查是否存在 @PathVariable 注解
            if (method.getParameterAnnotations()[i].length > 0) {
                for (Annotation annotation : method.getParameterAnnotations()[i]) {
                    if (annotation instanceof PathVariable) {
                        PathVariable pathVariable = (PathVariable) annotation;
                        param.put("in", "path");
                        param.put("name", pathVariable.value());
                        param.put("required", true);
                    }
                }
            }

            // 检查是否存在 @RequestBody 注解
            if (method.getParameterAnnotations()[i].length > 0) {
                for (Annotation annotation : method.getParameterAnnotations()[i]) {
                    if (annotation instanceof RequestBody) {
                        param.put("in", "body");
                        param.put("required", true);
                    }
                }
            }

            parameters.add(param);
        }

        return parameters;
    }

    // 获取返回值
    private static Map<String, Map<String, Object>> getReturnValue(Method method) {
        Map<String, Map<String, Object>> returnValue = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", method.getReturnType().getName());
        content.put("*/*", schema);
        returnValue.put("200", content);
        return returnValue;
    }

    /**
     * 将 API 信息保存为 JSON 文件
     *
     * @param apiList  API 信息列表
     * @param loadMode 加载模式（startup 或 runtime）
     */
    private static void saveToJson(List<ApiInfo> apiList, String loadMode) {
        Gson gson = new Gson();
        String json = gson.toJson(apiList);
        String fileName = "api_info_" + loadMode + ".json";
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
            writer.write(json);
            System.out.println("API info saved to " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}