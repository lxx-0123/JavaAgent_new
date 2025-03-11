package com.example;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author lxx
 * @create 2025-03-10-17:25
 */
public class DynamicAttach {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java DynamicAttach <PID> <AgentJarPath>");
            return;
        }

        String pid = args[0]; // 目标 JVM 进程的 PID
        String agentPath = args[1]; // Java Agent 的路径

        // 动态加载 Agent
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentPath);
        vm.detach();

        System.out.println("Agent loaded successfully!");
    }
}

