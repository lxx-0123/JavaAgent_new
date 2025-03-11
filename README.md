**# JavaAgent**
1、新建一个Maven项目
在 IntelliJ IDEA创建一个 Maven 项目，用于编写 Java Agent
2、项目结构
java-agent-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── example/
│   │   │   │   │   ├── agent/
│   │   │   │   │   │   ├── ApiAgent.java
│   │   │   │   │   │   ├── ApiInfo.java
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── MANIFEST.MF
├── pom.xml
3、关键文件解释
（1）ApiAgent.java中实现premain和agentmain方法，分别用于启动时获取和启动后获取API
（2）MANIFEST.MF指定Premain-Class和Agent-Class，务必与项目结构中的层次和名称保持一致
（3）pom.xml需要配置将依赖库中的文件也打进JAR包
4、打包java-agent，生成JAR包
在 target/ 目录下生成 java-agent-1.0-SNAPSHOT.jar

**#动态加载工具DynamicAttach**
1、创建一个独立的Maven项目dynamic-attach，用于动态加载Java Agent
2、项目结构
dynamic-attacht-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/
│   │   │   │   ├── example/
│   │   │   │   │   ├── DynamicAttach.java/
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   ├── MANIFEST.MF
├── pom.xml
3、关键文件解释
（1）MANIFEST.MF指定Main-Class: com.example.DynamicAttach
4、打包dynamic-attacht，生成dynamic-attach-1.0-SNAPSHOT.jar

**#静态加载**
（1）输入java -javaagent启动命令并加载
java -javaagent:D:/Java_workspcae/mianshi/java-agent/target/java-agent-1.0-SNAPSHOT.jar -jar D:/Java_workspcae/mianshi/javaweb-vuln-master/vuln-springboot2/target/vuln-springboot2-3.0.3.jar
（2）配置成功，运行命令后输出
Agent loaded at startup!
API info saved to api_info_startup.json
（3）查看api_info_runtime.json

**#动态加载**
（1）获取目标JVM进程PID
使用 jps 命令查看目标 JVM 进程的 PID，例如12345 project-a.jar
（2）输入java -jar动态加载命令
java -jar D:/Java_workspcae/mianshi/dynamic-attach/target/dynamic-attach-1.0-SNAPSHOT.jar 12345 D:/Java_workspcae/mianshi/java-agent/target/java-agent-1.0-SNAPSHOT.jar
（3）配置成功，运行命令后输出
Agent loaded successfully!
API info saved to api_info_runtime.json
（4）查看api_info_runtime.json

