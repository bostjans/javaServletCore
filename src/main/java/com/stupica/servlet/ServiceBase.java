package com.stupica.servlet;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.core.UtilString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Created by bostjans on 16/09/16.
 */
public class ServiceBase extends HttpServlet {

    protected boolean     bSetCors = true;

    protected boolean     bVerifyReferal = true;
    protected boolean     bVerifyOrigin = false;
    protected boolean     bVerifyAuthenticHeader = true;
    protected boolean     bVerifyAuthenticUser = true;

    protected String      sRefererHostAllow = "localhost:3000";

    protected static Logger logger = Logger.getLogger(ServiceBase.class.getName());

    private Setting objSetting = Setting.getConfig();


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        ResultProcessWeb objResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        objResult = new ResultProcessWeb();

        logMethod(request, "doGet");

        //super.doGet(request, response);

        setContentType(response);
        request.setCharacterEncoding(ConstGlobal.ENCODING_UTF_8);
        response.setCharacterEncoding(ConstGlobal.ENCODING_UTF_8);             // UTF-8

        // Security verification(s) ..
        if (bVerifyReferal) {
            iResult = checkReferer(request, objResult);
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doGet(): Msg.: Method checkReferer() reported inconsistency!"
                    + " Msg.: " + objResult.sMsg.toString());
                return;
            }
        }
        // Security verification(s) ..
        if (bVerifyOrigin) {
            iResult = checkOrigin(request, objResult);
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doGet(): Msg.: Method checkOrigin() reported inconsistency!"
                        + " Msg.: " + objResult.sMsg.toString());
                return;
            }
        }

        if (bSetCors) {
            // CORS (Access-Control-Allow-Origin)
            response.addHeader("Access-Control-Allow-Origin", "*");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPostPut(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPostPut(request, response);
    }

    private void doPostPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        ResultProcessWeb objResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        objResult = new ResultProcessWeb();

        logMethod(request, "doPostPut");

        //super.doPut(request, response);

        //response.setContentType("application/json");        // JSON
        setContentType(response);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");             // UTF-8

        // Security verification(s) ..
        if (bVerifyReferal) {
            iResult = checkReferer(request, objResult);
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doPostPut(): Msg.: Method checkReferer() reported inconsistency!"
                        + " Msg.: " + objResult.sMsg.toString());
                return;
            }
        }
        // Security verification(s) ..
        if (bVerifyOrigin) {
            iResult = checkOrigin(request, objResult);
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.warning("doPostPut(): Msg.: Method checkOrigin() reported inconsistency!"
                        + " Msg.: " + objResult.sMsg.toString());
                return;
            }
        }

        if (bSetCors) {
            // CORS (Access-Control-Allow-Origin)
            response.addHeader("Access-Control-Allow-Origin", "*");
        }
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        StringBuilder   sMsgLog = new StringBuilder();

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        sMsgLog.append("doOptions(): Start ..  -  ");
        sMsgLog.append("pathInfo: " + request.getPathInfo());
        sMsgLog.append("\n\tgetParameterMap: " + request.getParameterMap());
        logger.info(sMsgLog.toString());

        if (bSetCors) {
            // CORS (Access-Control-Allow-Origin)
            response.addHeader("Access-Control-Allow-Origin", "*");
            //response.addHeader("Access-Control-Allow-Methods", "PUT, POST");
            response.addHeader("Access-Control-Allow-Methods", "GET, PUT");
            //response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Allow-Headers",
                    "Content-Type, " + ConstWeb.HTTP_HEADER_NAME_OECOMP + ", " + ConstWeb.HTTP_HEADER_NAME_USERNAME);
        }
        super.doOptions(request, response);
    }


    protected void logMethod(HttpServletRequest request, String asMethodName) {
        StringBuilder   sMsgLog = new StringBuilder();

        sMsgLog.append(asMethodName);
        sMsgLog.append("(): Start ..  -  ");
        sMsgLog.append("authType: " + request.getAuthType());
        sMsgLog.append("; userPrincipal: " + request.getUserPrincipal());
        sMsgLog.append("\n\treqUri: " + request.getRequestURI());
        sMsgLog.append("\n\tcontextPath: " + request.getContextPath());
        sMsgLog.append("; pathInfo: " + request.getPathInfo());
        sMsgLog.append("\n\tgetParameterMap: " + request.getParameterMap());
        logger.info(sMsgLog.toString());
    }

    protected void logPath(String[] aarrUrlPath) {
        int             iCount = 0;
        StringBuilder   sMsgLog = new StringBuilder();

        sMsgLog.append("logPath");
        sMsgLog.append("(): Start ..  -");
        for (String sLoop : aarrUrlPath) {
            sMsgLog.append("\n\tpathCount: " + iCount);
            sMsgLog.append(";\tpath: " + sLoop);
        }
        logger.info(sMsgLog.toString());
    }


    protected int checkReferer(HttpServletRequest request, ResultProcessWeb aobjResult) {
        // Local variables
        int             iResult;
        String          sReferer;
        String          sRefererHost = null;
        String[]        arrReferer = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        sRefererHostAllow = objSetting.getString("Http.Rest.Referer", sRefererHostAllow);

        sReferer = request.getHeader(ConstWeb.HTTP_HEADER_NAME_REFERER);
        //logger.info("checkReferer(): Referer: " + sReferer);
        if (UtilString.isEmptyTrim(sReferer)) {
            iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;  // Unauthorized (RFC 7235)
            aobjResult.sMsg.append("Request not from trusted source!"
                    + " Referer: " + sReferer);
            logger.warning("checkReferer(): Msg.: " + aobjResult.sMsg.toString());
            //sendResponse(response, iResult);
            return iResult;
        }
        arrReferer = sReferer.split("/");
        if (arrReferer == null) {
            iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;  // Unauthorized (RFC 7235)
            aobjResult.sMsg.append("Request not from trusted source!"
                    + " Referer: " + sReferer);
            logger.warning("checkReferer(): Msg.: " + aobjResult.sMsg.toString());
            return iResult;
        }
        if (arrReferer.length < 2) {
            iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;  // Unauthorized (RFC 7235)
            aobjResult.sMsg.append("Request not from trusted source!"
                    + " Referer: " + sReferer);
            logger.warning("checkReferer(): Msg.: " + aobjResult.sMsg.toString());
            return iResult;
        }
        sRefererHost = arrReferer[2];
        if (!sRefererHost.equalsIgnoreCase(sRefererHostAllow)) {
            iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;  // Unauthorized (RFC 7235)
            aobjResult.sMsg.append("Request not from trusted source!"
                    + " Referer: " + sReferer);
            logger.warning("checkReferer(): Msg.: " + aobjResult.sMsg.toString());
            return iResult;
        }
        return iResult;
    }


    protected int checkOrigin(HttpServletRequest request, ResultProcessWeb aobjResult) {
        // Local variables
        int             iResult;
        String          sOrigin = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        sRefererHostAllow = objSetting.getString("Http.Rest.Origin", sRefererHostAllow);

        sOrigin = request.getHeader(ConstWeb.HTTP_HEADER_NAME_ORIGIN);

        if (UtilString.isEmptyTrim(sOrigin)) {
            iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResult = ConstGlobal.RETURN_ERROR;
            aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;  // Unauthorized (RFC 7235)
            aobjResult.sMsg.append("Request not from trusted source!"
                    + " Origin: " + sOrigin);
            logger.warning("checkOrigin(): Msg.: " + aobjResult.sMsg.toString());
            //return iResult;
        }
        return iResult;
    }


    protected int checkAuthorizeRequest(HttpServletRequest request, HttpServletResponse response, ResultProcessWeb aobjResult) {
        // Local variables
        int             iResult;
        //Integer         iIdOeComp = null;
        //String          sIdComp = null;
        String          sUsername = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        if (!bVerifyAuthenticHeader) {
            return iResult;
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            //sIdComp = request.getHeader(ConstWeb.HTTP_HEADER_NAME_OECOMP);
            sUsername = request.getHeader(ConstWeb.HTTP_HEADER_NAME_USERNAME);

//            if (UtilString.isEmpty(sIdComp)) {
//                iIdOeComp = Integer.valueOf(0);
//            } else {
//                try {
//                    iIdOeComp = Integer.parseInt(sIdComp);
//                } catch (NumberFormatException nfe) {
//                    iIdOeComp = Integer.valueOf(0);
//                    logger.severe("checkAuthorizeRequest(): Incorrect ID(OE) specified!" + " sVal: " + sIdComp);
//                }
//            }

//            if (iIdOeComp == 0) {
//                iResult = ConstWeb.HTTP_RESP_UNAUTHORIZED;
//                sendResponse(response, iResult);
//                logger.warning("checkAuthorizeRequest(): NOT Authorized!"
//                        + " Oe: " + sIdComp
//                        + "; UserName: " + sUsername
//                        + "; Msg.: No OE specified.");
//            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (UtilString.isEmpty(sUsername)) {
                iResult = ConstGlobal.RETURN_SEC_ERROR;
                aobjResult.iResult = ConstGlobal.RETURN_SEC_ERROR;
                aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;
                aobjResult.bIsDone = true;
                aobjResult.sMsg.append("NOT Authorized!"
                        //+ " Oe: " + sIdComp
                        + "; UserName: " + sUsername
                        + "; Msg.: No Username specified.");
                //sendResponse(response, iResult);
                logger.warning("checkAuthorizeRequest(): " + aobjResult.sMsg.toString());
            } else if (sUsername.contentEquals(ConstGlobal.DEFINE_STR_NOVALUE)) {
                iResult = ConstGlobal.RETURN_SEC_ERROR;
                aobjResult.iResult = ConstGlobal.RETURN_SEC_ERROR;
                aobjResult.iResultWeb = ConstWeb.HTTP_RESP_UNAUTHORIZED;
                aobjResult.bIsDone = true;
                aobjResult.sMsg.append("NOT Authorized!"
                        //+ " Oe: " + sIdComp
                        + "; UserName: " + sUsername
                        + "; Msg.: No Username specified.");
                //sendResponse(response, iResult);
                logger.warning("checkAuthorizeRequest(): " + aobjResult.sMsg.toString());
            } else {
                aobjResult.sText = sUsername;
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (bVerifyAuthenticUser) {
                iResult = verifyUserAuth(response, sUsername, aobjResult);
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    logger.severe("checkAuthorizeRequest(): Error at verifyUserAuth() operation!"
                            + "\t sUser: " + sUsername
                            + ";\t iResult: " + iResult);
                }
            }
        }
        if (aobjResult.iResult != iResult)
            aobjResult.iResult = iResult;
        return iResult;
    }


    protected int verifyUserAuth(HttpServletResponse response, String asUser, ResultProcessWeb aobjResult) {
        // Local variables
        int             iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        return iResult;
    }


    protected void setContentType(HttpServletResponse aobjResponse) {
        aobjResponse.setContentType("text/html");        // TEXT
    }


    /*
    protected void sendResponse(HttpServletResponse response, int aiRespCode) {
        PrintWriter     objOut = null;

        try {
            objOut = response.getWriter();
        } catch (IOException ex) {
            logger.severe("sendResponse(): Could NOT get writer for response!"
                    + " Msg.: " + ex.getMessage());
        }
        sendResponse(response, objOut, aiRespCode);
        objOut.close();
    }

    protected void sendResponse(HttpServletResponse response, PrintWriter aobjOut, int aiRespCode) {
        int iRespCode = aiRespCode;

        if (iRespCode < ConstWeb.HTTP_RESP_CONTINUE) {
            iRespCode = ConstWeb.HTTP_RESP_INTERNAL_SRV_ERR;
        }
        try {
            response.setStatus(iRespCode);
        } catch (IllegalArgumentException ex) {
            logger.severe("sendResponse(): Could NOT set response!"
                    + " Code: " + aiRespCode
                    + "; Msg.: " + ex.getMessage());
        }
        response.resetBuffer();
        if (aiRespCode != ConstGlobal.RETURN_OK) {
            aobjOut.println("Error: " + aiRespCode);
        }
    } */


    protected int readRequestData(HttpServletRequest request, StringBuilder asData) {
        // Local variables
        int             iResult;
        StringBuilder   sData = asData;
        String          sTempLine = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        try {
            BufferedReader reader = request.getReader();
            while ((sTempLine = reader.readLine()) != null)
                sData.append(sTempLine);
        } catch (IOException e) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("readRequestData(): IO Error at data retrieval!"
                    + " Msg.: " + e.getMessage()
                    + "; sVal: " + sTempLine
                    + "\n\tsData: " + sData.toString());
        } catch (Exception e) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("readRequestData(): Error at data retrieval!"
                    + " Msg.: " + e.getMessage()
                    + "; sVal: " + sTempLine
                    + "\n\tsData: " + sData.toString());
        }
        return iResult;
    }
}
