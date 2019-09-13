package com.stupica.servlet.rest.v1;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.servlet.ServiceBase;

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
     * @api {get} /monitor/v1/ Request Monitor status
     * @apiName Monitor
     * @apiGroup Monitor
     * @apiDescription REST method to monitor system accessibility.
     * @apiVersion 1.0.0
     * @apiPermission none
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
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

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        bVerifyReferal = false;

        super.doGet(request, response);

        // Check ..
        if (response.getStatus() != ConstWeb.HTTP_RESP_OK) {
            logger.warning("doGet(): StatusCode indicates error in prior verification! Status: " + response.getStatus());
            return;
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objOut = response.getWriter();
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objOut.print("OK");
        }
        if (objOut != null) {
            objOut.close();
        }
    }
}
