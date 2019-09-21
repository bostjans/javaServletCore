package com.stupica.servlet.rest.v1;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.servlet.ResultProcessWeb;
import com.stupica.servlet.ServiceMonitor;
import com.stupica.servlet.rest.ServiceBaseJson;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by bostjans on 16/09/16.
 */
public class MonitorJson extends ServiceBaseJson {

    /**
     * @api {get} /monitorJson/v1/ MonitorJson
     * @apiName MonitorJson
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription REST (JSON) method to monitor system status/accessibility.
     *
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *
     * { "header":{
     *       "resultCode":1,
     *       "resultMsg":"OK",
     *       "resultCount":0,
     *       "msg":"/",
     *       "description":"n/a",
     *       "timestamp":1568812806231,
     *       "timestampStr":"Wed Sep 18 13:20:06 2019",
     *       "errorCode":0,
     *       "errorMsg":"/",
     *       "status":"00",
     *       "application":"LenkoTraderREST",
     *       "version":"1.6.4",
     *       "versionApi":"1.0",
     *       "versionManifest":"/"
     *    },
     *    "pagination":{
     *       "page":0,
     *       "pageSize":0
     *    },
     *    "filter":{
     *       "filter":"/"
     *    },
     *    "sort":{
     *       "sortBy":"/"
     *    },
     *    "data":{
     *       "status":"OK",
     *       "description":"All Ok"
     *    }
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
        int                 iResult;
        ResultProcessWeb    objResult;
        JsonObject          objJsonResponse = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        bVerifyReferal = false;
        bIsJsonEnvelopeMode = true;
        objResult = new ResultProcessWeb();

        super.doGet(request, response);

        // Check ..
        if (response.getStatus() != ConstWeb.HTTP_RESP_OK) {
            iResult = ConstGlobal.RETURN_ERROR;
            objResult.iResult = ConstGlobal.RETURN_ERROR;
            objResult.iResultWeb = response.getStatus();
            objResult.sText = "doGet(): StatusCode indicates error in prior verification! Status: " + objResult.iResultWeb;
            logger.warning(objResult.sText);
            //return;
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = ServiceMonitor.getInstance().checkSystem(objResult);
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doGet(): Error at process checkSystem()! Result: " + iResult);
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objJsonResponse = Json.object().add("status", objResult.sText);
            objJsonResponse.add("description", objResult.sMsg.toString());
        }
        iResult = prepareSendResponse(response, objResult, objJsonResponse);
        if (iResult != ConstGlobal.RETURN_OK) {
            logger.severe("doGet(): Error at prepareSendResponse()! Result: " + iResult);
        }
//        objOut = response.getWriter();
//        {
//            objJsonResponse = Json.object().add("status", objResult.sText);
//            objJsonResponse.add("description", objResult.sMsg.toString());
//            if (bIsJsonEnvelopeMode) {
//                objJsonResponseEnv = getResponseEnvObject(iResult, objResult.sText, objResult.sMsg.toString());
//                objJsonResponseEnv.add("data", objJsonResponse);
//                objOut.print(objJsonResponseEnv.toString());
//            } else {
//                objOut.print(objJsonResponse.toString());
//            }
//        }
//        if (objOut != null) {
//            objOut.close();
//        }
    }
}
