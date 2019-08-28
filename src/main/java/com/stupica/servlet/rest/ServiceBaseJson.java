package com.stupica.servlet.rest;


import javax.servlet.http.HttpServletRequest;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import com.stupica.ConstGlobal;
import com.stupica.servlet.ServiceBase;
import com.stupica.servlet.Setting;


public class ServiceBaseJson extends ServiceBase {

    protected boolean   bIsJsonEnvelopeMode = true;


    protected JsonObject getResponseEnvObject(int aiResult) {
        JsonObject      objJsonResponseEnv = null;
        JsonObject      objJsonResponseHeader = null;

        objJsonResponseHeader = Json.object().add("ResultCode", aiResult);
        objJsonResponseHeader.add("ResultMsg", "OK");
        objJsonResponseHeader.add("Msg", "/");
        objJsonResponseHeader.add("ErrorCode", 0);
        objJsonResponseHeader.add("ErrorMsg", "/");
        objJsonResponseHeader.add("Status", "00");
        objJsonResponseHeader.add("Application", Setting.getConfig().getString(Setting.PROJECT_NAME, "/"));
        objJsonResponseHeader.add("Version", Setting.getConfig().getString(Setting.DEFINE_CONF_APP_VERSION, "/"));
        objJsonResponseHeader.add("VersionManifest", "/");

        objJsonResponseEnv = Json.object().add("Header", objJsonResponseHeader);
        return objJsonResponseEnv;
    }


    protected JsonObject readRequestDataParse(HttpServletRequest request) {
        // Local variables
        int             iResult;
        int             iLenData = 0;
        //int             iLenDataProbe = 0;
        StringBuilder   sData = new StringBuilder();
        JsonObject      objJson = null;

        // Initialization
        //iResult = Constant.i_func_return_OK;

        iResult = readRequestData(request, sData);
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iLenData = sData.length();
            if (iLenData > 0) {
                logger.info("readRequestDataParse():"
                        + " dataLength: " + sData.length()
                        + " sData: " + sData.toString());
                //if (iLenData < 10) {
                //    iLenDataProbe = iLenData;
                //} else {
                //    iLenDataProbe = 10;
                //}
                try {
                    //if (sData.substring(0, iLenDataProbe).startsWith("[")) {
                    //    objJsonArr = Json.parse(sData.toString()).asArray();
                    //} else {
                    objJson = Json.parse(sData.toString()).asObject();
                    //}
                } catch (Exception e) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    StringBuilder sMsg = new StringBuilder();
                    sMsg.append("readRequestDataParse(): Error at data parsing!");
                    if (sData.length() < 1024) {
                        sMsg.append(" sData: ");
                        sMsg.append(sData);
                    }
                    sMsg.append("\n\tsMsg.: ");
                    sMsg.append(e.getMessage());
                    logger.severe(sMsg.toString());
                }
            } else {
                logger.warning("readRequestDataParse(): No input data! sData: /");
            }
        }
        return objJson;
    }

    protected JsonArray readRequestDataArrParse(HttpServletRequest request) {
        // Local variables
        int             iResult;
        int             iLenData = 0;
        StringBuilder   sData = new StringBuilder();
        JsonArray       objJsonArr = null;

        // Initialization
        //iResult = Constant.i_func_return_OK;

        iResult = readRequestData(request, sData);
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iLenData = sData.length();
            if (iLenData > 0) {
                logger.info("readRequestDataArrParse():"
                        + " dataLength: " + iLenData
                        + " sData: " + sData.toString());
                try {
                    objJsonArr = Json.parse(sData.toString()).asArray();
                } catch (Exception e) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    StringBuilder sMsg = new StringBuilder();
                    sMsg.append("readRequestDataArrParse(): Error at data parsing!");
                    if (sData.length() < 1024) {
                        sMsg.append(" sData: ");
                        sMsg.append(sData);
                    }
                    sMsg.append("\n\tsMsg.: ");
                    sMsg.append(e.getMessage());
                    logger.severe(sMsg.toString());
                }
            } else {
                logger.warning("readRequestDataArrParse(): No input data! sData: /");
            }
        }
        return objJsonArr;
    }
}
