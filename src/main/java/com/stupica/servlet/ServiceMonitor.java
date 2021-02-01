package com.stupica.servlet;


import com.stupica.ConstGlobal;
import com.stupica.ResultProces;

import java.util.ArrayList;

import static com.stupica.ConstGlobal.DEFINE_STR_NEWLINE;


public class ServiceMonitor {

    //public String   sStatusOk = "OK";

    private ArrayList<MonitorProbeBase> arrProbe = null;

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


    public ServiceMonitor() {
        arrProbe = new ArrayList<MonitorProbeBase>();
    }


    public int addProbe(MonitorProbeBase aobjProbe) {
        arrProbe.add(aobjProbe);
        return ConstGlobal.RETURN_OK;
    }
    public int removeProbe(MonitorProbeBase aobjProbe) {
        arrProbe.remove(aobjProbe);
        return ConstGlobal.RETURN_OK;
    }

    public synchronized int checkSystem(ResultProces aobjResult) {
        // Local variables
        int             iResult;
        MonitorProbeOK  objProbeTest = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        if (iResult == ConstGlobal.RETURN_OK) {
            if (arrProbe.isEmpty()) {
                objProbeTest = new MonitorProbeOK();
                addProbe(objProbeTest);
            }
        }

        if (iResult == ConstGlobal.RETURN_OK) {
            StringBuilder   sTemp = new StringBuilder();

            for (MonitorProbeBase objLoop : arrProbe) {
                sTemp.append(objLoop.getName()).append(": ").append(objLoop.getStatus()).append(DEFINE_STR_NEWLINE);
            }
            aobjResult.sText = sTemp.toString();
            aobjResult.sMsg.append("All Ok");
        }

        if (objProbeTest != null) {
            removeProbe(objProbeTest);
        }
        aobjResult.iResult = iResult;
        return iResult;
    }
}
