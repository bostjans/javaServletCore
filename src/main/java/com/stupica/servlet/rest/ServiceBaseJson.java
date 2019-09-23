package com.stupica.servlet.rest;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;

import com.stupica.ConstGlobal;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;
import com.stupica.servlet.ResultProcessWeb;
import com.stupica.servlet.ServiceBase;
import com.stupica.servlet.Setting;


public class ServiceBaseJson extends ServiceBase {

    protected boolean   bIsJsonEnvelopeMode = false;
    protected boolean   bIsJsonPrettyPrintMode = false;


    protected void setContentType(HttpServletResponse aobjResponse) {
        aobjResponse.setContentType("application/json");        // JSON
    }


    protected JsonObject getResponseEnvObject(int aiResult, String asStatus, String asMsg, String asDesc, Date adtTrasfer) {
        String          sTemp;

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
        switch (aiResult) {
            case ConstGlobal.RETURN_OK: sTemp = "OK"; break;
            case ConstGlobal.RETURN_WARN: sTemp = "WARNING"; break;
            case ConstGlobal.RETURN_ERROR: sTemp = "ERROR"; break;
            case ConstGlobal.RETURN_NA: sTemp = "NA"; break;
            default: sTemp = "unKnown";
        }
        objJsonResponseHeader.add("resultMsg", sTemp);
        objJsonResponseHeader.add("resultCount", 0);
        if (!UtilString.isEmpty(asMsg))
            objJsonResponseHeader.add("msg", asMsg);
        if (!UtilString.isEmpty(asDesc))
            objJsonResponseHeader.add("description", asDesc);
        objJsonResponseHeader.add("timestamp", adtTrasfer.getTime());
        objJsonResponseHeader.add("timestampStr", UtilDate.toUniversalString(adtTrasfer));
        if (aiResult != ConstGlobal.RETURN_OK) {
            objJsonResponseHeader.add("errorCode", aiResult);
            objJsonResponseHeader.add("errorMsg", "/");
        }
        //objJsonResponseHeader.add("errorDetail", objJsonResponseErrorDetail);
        objJsonResponseHeader.add("status", asStatus);
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
            objJsonResponseEnv.add("pagination", objJsonResponsePagination);
        if (objJsonResponseFilter != null)
            objJsonResponseEnv.add("filter", objJsonResponseFilter);
        if (objJsonResponseSort != null)
            objJsonResponseEnv.add("sort", objJsonResponseSort);
        return objJsonResponseEnv;
    }


    protected int prepareSendResponse(HttpServletResponse aobjResponse, ResultProcessWeb aobjResult, JsonArray aarrData) {
        // Local variables
        int             iResult;
        boolean         bShouldSendContent = true;
        String          sTemp = null;
        StringBuilder   sData = null;
        Date            dtNow = new Date();
        JsonObject      objJsonResponseEnv = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        //logger.info("prepareSendResponse(): Set response status ..");
        if (aobjResult.iResultWeb == HttpServletResponse.SC_NO_CONTENT) {
            bShouldSendContent = false;
            if ((aarrData != null) || (bIsJsonEnvelopeMode)) {
                sTemp = "(HTTP)ResponseCode is set to: " + aobjResult.iResultWeb
                        + " and response data is provided, which will NOT be send to client as Code indicate: NO DATA!";
                aobjResponse.setHeader("responseMsg", sTemp);
                logger.warning("prepareSendResponse(): " + sTemp);
            }
        }
        aobjResponse.setStatus(aobjResult.iResultWeb);
//        if (aobjResult.iResultWeb >= HttpServletResponse.SC_BAD_REQUEST) {
//            try {
//                logger.info("prepareSendResponse(): Set response error status ..");
//                aobjResponse.sendError(aobjResult.iResultWeb, aobjResult.sMsg.toString());
//            } catch (IOException ex) {
//                iResult = ConstGlobal.RETURN_ERROR;
//                logger.severe("prepareSendResponse(): Can NOT send response Error!"
//                        + " Msg.: " + ex.getMessage());
//            }
//        }
        //aobjResponse.setDateHeader("dtTransfer", dtNow.getTime());

        if (aobjResponse.isCommitted()) {
            logger.warning("prepareSendResponse(): The response cache data has already been send!");
        }

        if (bShouldSendContent) {
            sData = new StringBuilder();
            if (bIsJsonEnvelopeMode) {
                //logger.info("prepareSendResponse(): .. it is Envelope response ..");
                if (aobjResult.sText.toUpperCase().contentEquals(ConstGlobal.DEFINE_STR_OK)) {
                    objJsonResponseEnv = getResponseEnvObject(iResult, aobjResult.sText, null, aobjResult.sMsg.toString(), dtNow);
                } else {
                    objJsonResponseEnv = getResponseEnvObject(iResult, aobjResult.sText, aobjResult.sMsg.toString(), null, dtNow);
                }
                if (aarrData == null) {
                    //objJsonResponseEnv.add("data", "{}");
                    objJsonResponseEnv.add("data", Json.object());
                } else {
                    objJsonResponseEnv.add("data", aarrData);
                }

                if (bIsJsonPrettyPrintMode) {
                    sData.append(objJsonResponseEnv.toString(WriterConfig.PRETTY_PRINT));
                } else {
                    sData.append(objJsonResponseEnv.toString());
                }
            } else {
                if (bIsJsonPrettyPrintMode) {
                    if (aarrData != null) sData.append(aarrData.toString(WriterConfig.PRETTY_PRINT));
                } else {
                    if (aarrData != null) sData.append(aarrData.toString());
                }
            }
            sData.append("  ");
        }
        iResult = sendResponse(aobjResponse, sData.toString());
        return iResult;
    }

    protected int prepareSendResponse(HttpServletResponse aobjResponse, ResultProcessWeb aobjResult) {
        return prepareSendResponse(aobjResponse, aobjResult, (JsonObject) null);
    }
    protected int prepareSendResponse(HttpServletResponse aobjResponse, ResultProcessWeb aobjResult, JsonObject aobjData) {
        // Local variables
        int             iResult;
        boolean         bShouldSendContent = true;
        String          sTemp = null;
        StringBuilder   sData = null;
        Date            dtNow = new Date();
        JsonObject      objJsonResponseEnv = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        //logger.info("prepareSendResponse(): Set response status ..");
        if (aobjResult.iResultWeb == HttpServletResponse.SC_NO_CONTENT) {
            bShouldSendContent = false;
            if ((aobjData != null) || (bIsJsonEnvelopeMode)) {
                sTemp = "(HTTP)ResponseCode is set to: " + aobjResult.iResultWeb
                        + " and response data is provided, which will NOT be send to client as Code indicate: NO DATA!";
                aobjResponse.setHeader("responseMsg", sTemp);
                logger.warning("prepareSendResponse(): " + sTemp);
            }
        }
        aobjResponse.setStatus(aobjResult.iResultWeb);

        if (aobjResponse.isCommitted()) {
            logger.warning("prepareSendResponse(): The response cache data has already been send!");
        }

        if (bShouldSendContent) {
            sData = new StringBuilder();
            if (bIsJsonEnvelopeMode) {
                //logger.info("prepareSendResponse(): .. it is Envelope response ..");
                if (aobjResult.sText.toUpperCase().contentEquals(ConstGlobal.DEFINE_STR_OK)) {
                    objJsonResponseEnv = getResponseEnvObject(iResult, aobjResult.sText, null, aobjResult.sMsg.toString(), dtNow);
                } else {
                    objJsonResponseEnv = getResponseEnvObject(iResult, aobjResult.sText, aobjResult.sMsg.toString(), null, dtNow);
                }
                if (aobjData == null) {
                    //objJsonResponseEnv.add("data", "{}");
                    objJsonResponseEnv.add("data", Json.object());
                } else {
                    objJsonResponseEnv.add("data", aobjData);
                }

                if (bIsJsonPrettyPrintMode) {
                    sData.append(objJsonResponseEnv.toString(WriterConfig.PRETTY_PRINT));
                } else {
                    sData.append(objJsonResponseEnv.toString());
                }
            } else {
                if (bIsJsonPrettyPrintMode) {
                    if (aobjData != null) sData.append(aobjData.toString(WriterConfig.PRETTY_PRINT));
                } else {
                    if (aobjData != null) sData.append(aobjData.toString());
                }
            }
            sData.append("  ");
        }
        iResult = sendResponse(aobjResponse, sData.toString());
        return iResult;
    }

    protected int sendResponse(HttpServletResponse aobjResponse, String asData) {
        // Local variables
        int             iResult;
        String          sData = asData;
        PrintWriter     objOut = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        //logger.info("sendResponse(): Start ..  -  Data: " + sData);

        //sData = "test";
        if (!UtilString.isEmptyTrim(sData)) {
            aobjResponse.setContentLength(sData.length());
        } else {
            aobjResponse.setContentLength(0);
        }

        try {
            objOut = aobjResponse.getWriter();
        } catch (IOException ex) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("sendResponse(): Can NOT send response!"
                    + " Msg.: " + ex.getMessage());
        }
        if (objOut != null) {
            //aobjResponse.resetBuffer();
            if (!UtilString.isEmpty(sData)) {
                if (bIsJsonPrettyPrintMode) {
                    //logger.info("sendResponse(): .. it is PrettyPrint Mode .."
                    //        + "\n\tData: " + sData);
                    objOut.println(sData);
                } else {
                    //logger.info("sendResponse(): .. it is Compact Mode ..");
                    objOut.print(sData);
                }
            }
        }

        if (objOut != null) {
            objOut.flush();
            //if (objOut.checkError()) {
            //    String sTemp = "Error(s) detected at data send! Data: ";
            //    if (sData.length() > 32)    sTemp += sData.substring(0, 32) + " ..";
            //    else                        sTemp += sData;
            //    logger.warning("sendResponse(): " + sTemp);
            //}
            objOut.close();
        }
        //logger.info("sendResponse(): isCommitted: " + aobjResponse.isCommitted());
        return iResult;
    }


    protected JsonObject readRequestDataParse(HttpServletRequest request) {
        // Local variables
        int             iResult;
        int             iLenData = 0;
        //int             iLenDataProbe = 0;
        StringBuilder   sData = new StringBuilder();
        JsonObject      objJson = null;

        // Initialization

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
