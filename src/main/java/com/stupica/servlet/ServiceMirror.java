package com.stupica.servlet;


import com.stupica.ConstGlobal;
import com.stupica.core.UtilString;

import static com.stupica.ConstGlobal.DEFINE_STR_NEWLINE;


public class ServiceMirror {

    /**
     * Object instance variable;
     */
    private static ServiceMirror objInstance;

    public static boolean   isEnabled = false;

    private final int   iCountMaxCache = 11;
    private int         iCountCache = 0;
    String[]            arrCache = null;


    private ServiceMirror() {
        if (isEnabled) {
            arrCache = new String[iCountMaxCache];
        }
    }


    public static ServiceMirror getInstance() {
        if (objInstance == null) {
            objInstance = new ServiceMirror();
        }
        return objInstance;
    }


    public synchronized int notifyOnRequest(String asHttpData) {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        //System.out.println(asHttpData);

        if (arrCache != null) {
            arrCache[iCountCache++] = asHttpData;
            if (iCountCache == iCountMaxCache) iCountCache = 0;
        }
        return iResult;
    }


    public synchronized String getRequest() {
        // Local variables
        int             iCount = 1;
        StringBuilder   sResult = new StringBuilder();

        // Initialization

        if (arrCache != null) {
            for (String asLoop : arrCache) {
                if (!UtilString.isEmpty(asLoop)) {
                    sResult.append(DEFINE_STR_NEWLINE).append("Request: ").append(iCount++).append(DEFINE_STR_NEWLINE);
                    sResult.append(asLoop);
                }
            }
        }
        if (sResult.length() < 1) {
            sResult.append("No Data.");
        }
        return sResult.toString();
    }
}
