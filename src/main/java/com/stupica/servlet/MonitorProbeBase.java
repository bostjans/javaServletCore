package com.stupica.servlet;


public abstract class MonitorProbeBase {

    public static String   sStatusOk = "OK";
    public static String   sStatusWarn = "Warn";
    public static String   sStatusError = "ERROR";

    public String   sProbeName = "Base";

    public abstract String getName();
    public abstract String getStatus();
}
