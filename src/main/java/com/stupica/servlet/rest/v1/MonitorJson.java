package com.stupica.servlet.rest.v1;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.servlet.rest.ServiceBaseJson;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by bostjans on 16/09/16.
 */
public class MonitorJson extends ServiceBaseJson {

    /**
     * @api {get} /monitorJson/v1/ Request Monitor status in JSON
     * @apiName MonitorJson
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription REST (JSON) method to monitor system accessibility.
     *
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *     "Header": {
     *         "ResultCode": 1,
     *         "ResultMsg": "OK",
     *         "Msg": "/",
     *         "ErrorCode": 0,
     *         "ErrorMsg": "/",
     *         "Status": "00",
     *         "Application": "LenkoTraderREST",
     *         "Version": "1.6.4",
     *         "VersionManifest": "/"
     *     },
     *     "Data": {
     *         "Status": "OK"
     *     }
     * }
     *
     * @apiSampleRequest /monitorJson/v1/
     *
     * @apiExample {curl} Example usage:
     *     curl -i http://localhost:8080/lenkoRest/monitorJson/v1/
     *     curl -i http://localhost:8080/lenkoTrRest/monitorJson/v1/
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        PrintWriter     objOut = null;
        JsonObject      objJsonResponse = null;
        JsonObject      objJsonResponseEnv = null;

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
        {
            objJsonResponse = Json.object().add("status", "OK");
            if (bIsJsonEnvelopeMode) {
                objJsonResponseEnv = getResponseEnvObject(iResult);
                objJsonResponseEnv.add("data", objJsonResponse);
                objOut.print(objJsonResponseEnv.toString());
            } else {
                objOut.print(objJsonResponse.toString());
            }
        }
        if (objOut != null) {
            objOut.close();
        }
    }
}
