package com.stupica.servlet;


import com.stupica.ConstGlobal;
import com.stupica.ResultProces;


public class ServiceMonitor {

    public String   sStatusOk = "OK";

    /**
     * Object instance variable;
     */
    private static ServiceMonitor objInstance;


    public static ServiceMonitor getInstance() {
        if (objInstance == null) {
            objInstance = new ServiceMonitor();
        }
        return objInstance;
    }


    public int checkSystem(ResultProces aobjResult) {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        if (iResult == ConstGlobal.RETURN_OK) {
            aobjResult.sText = sStatusOk;
            aobjResult.sMsg.append("All Ok");
        }
        aobjResult.iResult = iResult;
        return iResult;
    }
}
