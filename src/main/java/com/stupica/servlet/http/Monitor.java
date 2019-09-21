package com.stupica.servlet.http;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.ResultProces;
import com.stupica.servlet.ServiceBase;
import com.stupica.servlet.ServiceMonitor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by bostjans on 16/09/16.
 */
public class Monitor extends ServiceBase {

    /**
     * @api {get} /monitor/v1/ Monitor
     * @apiName Monitor
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription REST method to monitor system status/accessibility.
     *
     * @apiSuccessExample {text} Success-Response:
     *     HTTP/1.1 200 OK
     *
     *     OK
     *
     * @apiSampleRequest /monitor/v1/
     *
     * @apiExample {curl} Example usage:
     *     curl -i http://localhost:8080/lenkoRest/monitor/v1/
     *     curl -i http://localhost:8080/lenkoTrRest/monitor/v1/
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        PrintWriter     objOut = null;
        ResultProces    objResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        bVerifyReferal = false;
        objResult = new ResultProces();

        super.doGet(request, response);

        // Check ..
        if (response.getStatus() != ConstWeb.HTTP_RESP_OK) {
            iResult = response.getStatus();
            objResult.sText = "doGet(): StatusCode indicates error in prior verification! Status: " + response.getStatus();
            logger.warning(objResult.sText);
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = ServiceMonitor.getInstance().checkSystem(objResult);
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doGet(): Error at process checkSystem()! Result: " + iResult);
            }
        }

        // Check previous step
        //if (iResult == ConstGlobal.RETURN_OK) {
            objOut = response.getWriter();
        //}
        // Check previous step
        //if (iResult == ConstGlobal.RETURN_OK) {
            if (objResult.sMsg.length() == 0) {
                objOut.print(objResult.sText);
            } else {
                objOut.println(objResult.sText);
                objOut.println("--");
                objOut.print(objResult.sMsg.toString() + " < ");
            }
        //}
        if (objOut != null) {
            objOut.close();
        }
    }
}
