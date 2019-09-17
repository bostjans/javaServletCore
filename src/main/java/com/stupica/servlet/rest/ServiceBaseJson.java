package com.stupica.servlet.rest;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import com.stupica.ConstGlobal;
import com.stupica.core.UtilDate;
import com.stupica.servlet.ServiceBase;
import com.stupica.servlet.Setting;


public class ServiceBaseJson extends ServiceBase {

    protected boolean   bIsJsonEnvelopeMode = true;


    protected void setContentType(HttpServletResponse aobjResponse) {
        aobjResponse.setContentType("application/json");        // JSON
    }


    protected JsonObject getResponseEnvObject(int aiResult) {
        Date            dtNow = new Date();

        JsonObject      objJsonResponseEnv = null;
        JsonObject      objJsonResponseHeader = null;
        //JsonObject      objJsonResponseErrorDetail = null;
        JsonObject      objJsonResponsePagination = null;
        JsonObject      objJsonResponseFilter = null;
        JsonObject      objJsonResponseSort = null;

        //objJsonResponseErrorDetail = Json.object().add("location", "?");
        //objJsonResponseErrorDetail.add("trace", "");
        //objJsonResponseErrorDetail.add("moreInfo", "");
        //objJsonResponseErrorDetail.add("msgDev", "");

        objJsonResponseHeader = Json.object().add("resultCode", aiResult);
        objJsonResponseHeader.add("resultMsg", "OK");
        objJsonResponseHeader.add("resultCount", 0);
        objJsonResponseHeader.add("msg", "/");
        objJsonResponseHeader.add("description", "n/a");
        objJsonResponseHeader.add("timestamp", dtNow.getTime());
        objJsonResponseHeader.add("timestampStr", UtilDate.toUTCString(dtNow));
        objJsonResponseHeader.add("errorCode", 0);
        objJsonResponseHeader.add("errorMsg", "/");
        //objJsonResponseHeader.add("errorDetail", objJsonResponseErrorDetail);
        objJsonResponseHeader.add("status", "00");
        objJsonResponseHeader.add("application", Setting.getConfig().getString(Setting.PROJECT_NAME, "/"));
        objJsonResponseHeader.add("version", Setting.getConfig().getString(Setting.DEFINE_CONF_APP_VERSION, "/"));
        objJsonResponseHeader.add("versionApi", "1.0");
        objJsonResponseHeader.add("versionManifest", "/");

        objJsonResponsePagination = Json.object().add("page", 0);
        objJsonResponsePagination.add("pageSize", 0);

        objJsonResponseFilter = Json.object().add("filter", "/");

        objJsonResponseSort = Json.object().add("sortBy", "/");

        objJsonResponseEnv = Json.object().add("header", objJsonResponseHeader);
        if (objJsonResponsePagination != null)
            objJsonResponseEnv = Json.object().add("pagination", objJsonResponsePagination);
        if (objJsonResponseFilter != null)
            objJsonResponseEnv = Json.object().add("filter", objJsonResponseFilter);
        if (objJsonResponseSort != null)
            objJsonResponseEnv = Json.object().add("sort", objJsonResponseSort);
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
