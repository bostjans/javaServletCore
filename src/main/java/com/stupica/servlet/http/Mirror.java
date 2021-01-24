package com.stupica.servlet.http;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.ResultProces;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;
import com.stupica.servlet.ServiceBase;
import com.stupica.servlet.ServiceMirror;

import javax.security.auth.x500.X500Principal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

import static com.stupica.ConstGlobal.DEFINE_STR_NEWLINE;


/**
 * Created by bostjans on 16/09/16.
 */
public class Mirror extends ServiceBase {

    private static Logger logger = Logger.getLogger(Mirror.class.getName());


    /**
     * @api {get} /mirror/v1/ Mirror
     * @apiName Mirror
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription Web method to monitor system status/accessibility.
     *
     * @apiSuccessExample {text} Success-Response:
     *     HTTP/1.1 200 OK
     *
     *     ..
     *
     * @apiSampleRequest /mirror/v1/
     *
     * @apiExample {curl} Example usage:
     *     curl -i --insecure https://localhost:11443/mirror/v1
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest("GET", request, response);
    }

    /**
     * @api {put} /mirror/v1/ Mirror
     * @apiName Mirror
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription Web method to monitor system status/accessibility.
     *
     * @apiSuccessExample {text} Success-Response:
     *     HTTP/1.1 200 OK
     *
     *     ..
     *
     * @apiSampleRequest /mirror/v1/
     *
     * @apiExample {curl} Example usage:
     *     curl -i -X PUT --insecure https://localhost:11443/mirror/v1
     */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest("PUT", request, response);
    }

    /**
     * @api {post} /mirror/v1/ Mirror
     * @apiName Mirror
     * @apiGroup Monitor
     * @apiVersion 1.0.0
     * @apiPermission none
     * @apiDescription Web method to monitor system status/accessibility.
     *
     * @apiSuccessExample {text} Success-Response:
     *     HTTP/1.1 200 OK
     *
     *     ..
     *
     * @apiSampleRequest /mirror/v1/
     *
     * @apiExample {curl} Example usage:
     *     curl -i -X POST --insecure https://localhost:11443/mirror/v1
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doRequest("POST", request, response);
    }

    protected void doRequest(String asMethod, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        String          sHttpData = null;
        PrintWriter     objOut = null;
        ResultProces    objResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        bVerifyReferal = false;
        objResult = new ResultProces();

        if (asMethod.contentEquals("GET")) {
            super.doGet(request, response);
        } else if (asMethod.contentEquals("PUT")) {
            super.doPut(request, response);
        } else {
            iResult = response.getStatus();
            objResult.sText = "doRequest(): Unknown method! asMethod: " + asMethod;
            logger.warning(objResult.sText);
            response.setStatus(ConstWeb.HTTP_RESP_NOT_FOUND);
        }

        // Check ..
        if (response.getStatus() != ConstWeb.HTTP_RESP_OK) {
            iResult = response.getStatus();
            objResult.sText = "doGet(): StatusCode indicates error in prior verification! Status: " + response.getStatus();
            logger.warning(objResult.sText);
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            response.setContentType("text/plain; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            sHttpData = getReportText(request, "GET");
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objOut = response.getWriter();
            if (objOut == null) {
                iResult = ConstGlobal.RETURN_ERROR;
            }
            //objOut.println("<pre>");
        }
        //objOut.println("</pre>");
        if (objOut != null) {
            objOut.write(DEFINE_STR_NEWLINE);
            objOut.write(sHttpData);
        }
        if (objOut != null) {
            objOut.close();
        }
    }


    protected String getReportText(HttpServletRequest request, String asMethod) {
        // Local variables
        int             iResult;
        String              sMsgLog = null;
        String              headers = "";
        StringBuilder       headersLong = new StringBuilder();
        Enumeration<String> headerNames = null;
        StringBuilder       sHttpData = new StringBuilder();

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            headers = "Header(s): \n";
            headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers = headers + "  " + headerName + " :\t";
                String headerValue = request.getHeader(headerName);
                if (headerValue.length() > 80) {
                    headersLong.append("\nHeader Name: ").append(headerName);
                    headersLong.append("\n\tValue: ").append(headerValue);
                    headers = headers + headerValue.substring(0, 77) + " ..";
                } else {
                    headers = headers + headerValue;
                }
                headers = headers + "\n";
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            sHttpData.append("--").append(DEFINE_STR_NEWLINE);
            sHttpData.append(".. HTTP Mirror").append(DEFINE_STR_NEWLINE);
            sHttpData.append("--").append(DEFINE_STR_NEWLINE);

            //long currentTimeMillis = System.currentTimeMillis();
            Date    dtNow = new Date();
            String requestIdent = "IdAccess/IdDostopa: " + dtNow.getTime();
            logger.info("doGet(): " + requestIdent);
            logger.fine(headers);
            sHttpData.append(requestIdent);
            sHttpData.append("\t\t-> TimeStamp: ").append(UtilDate.toUniversalString(dtNow)).append(DEFINE_STR_NEWLINE);
            sHttpData.append("Method: ").append(asMethod).append(DEFINE_STR_NEWLINE);
            sHttpData.append(DEFINE_STR_NEWLINE);
            sHttpData.append("--").append(DEFINE_STR_NEWLINE);
            sHttpData.append("AuthType:\t").append(request.getAuthType()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("ContextPath:\t").append(request.getContextPath()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("PathInfo:\t").append(request.getPathInfo()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("RemoteUser:\t").append(request.getRemoteUser()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("RequestedSessionId:\t").append(request.getRequestedSessionId()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("RequestURI:\t").append(request.getRequestURI()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("RemoteAddr:\t").append(request.getRemoteAddr()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("RemoteHost:\t").append(request.getRemoteHost()).append("\t\t");
            sHttpData.append("RemotePort:\t").append(request.getRemotePort()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("Scheme:\t").append(request.getScheme()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("ServerName:\t").append(request.getServerName()).append("\t\t");
            sHttpData.append("ServerPort:\t").append(request.getServerPort()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("SessionId:\t").append(request.getSession().getId()).append(DEFINE_STR_NEWLINE);
            sHttpData.append("\t").append(request.getSession()).append(DEFINE_STR_NEWLINE);

            sHttpData.append(DEFINE_STR_NEWLINE).append("--").append(DEFINE_STR_NEWLINE);
            sHttpData.append(headers);
            if (headersLong.length() > 0) {
                sHttpData.append("--").append(DEFINE_STR_NEWLINE);
                sHttpData.append(headersLong.toString()).append(DEFINE_STR_NEWLINE);
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            sHttpData.append("--").append(DEFINE_STR_NEWLINE);
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
            if (certs != null) {
                X509Certificate x509Certificate = certs[0];
                X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
                String dn = subjectX500Principal.getName();
                x509Certificate.getSerialNumber();
                sMsgLog = "javax.servlet.request.X509Certificate (serial): " + x509Certificate.getSerialNumber().toString(16)
                        + "\n\t" + dn;
            } else {
                sMsgLog = "No (client) certificate received. / Nismo prejeli klient (client) certifikata.";
            }
            logger.info(sMsgLog);
            sHttpData.append(DEFINE_STR_NEWLINE).append(sMsgLog).append(DEFINE_STR_NEWLINE);
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            String clientCertString = request.getHeader("SSL_CLIENT_CERT");
            X509Certificate clientCert = createCertificateFromString(clientCertString);
            if (clientCert != null) {
                String sslClientSerial = clientCert.getSerialNumber().toString(16);
                sHttpData.append("--").append(DEFINE_STR_NEWLINE);
                sHttpData.append("SSL_CLIENT_CERT: ").append(sslClientSerial).append(DEFINE_STR_NEWLINE);
                sHttpData.append("\t").append(clientCert.getSubjectX500Principal().getName()).append(DEFINE_STR_NEWLINE);
                logger.info("SSL_CLIENT_CERT:" + sslClientSerial);
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            String clientCertVSString = request.getHeader("SSL_CLIENT_CERT_VS");
            X509Certificate clientCertVS = createCertificateFromString(clientCertVSString);
            if (clientCertVS != null) {
                String sslClientSerial = clientCertVS.getSerialNumber().toString(16);
                sHttpData.append("--").append(DEFINE_STR_NEWLINE);
                sHttpData.append("SSL_CLIENT_CERT_VS: ").append(sslClientSerial).append(DEFINE_STR_NEWLINE);
                sHttpData.append("\t").append(clientCertVS.getSubjectX500Principal().getName()).append(DEFINE_STR_NEWLINE);
            }
        }

        sHttpData.append(DEFINE_STR_NEWLINE).append("--").append(DEFINE_STR_NEWLINE);
        sHttpData.append(DEFINE_STR_NEWLINE).append(DEFINE_STR_NEWLINE);
        sHttpData.append("-- End of report. --<");

        // Notify Service
        ServiceMirror.getInstance().notifyOnRequest(sHttpData.toString());
        return sHttpData.toString();
    }

    private X509Certificate createCertificateFromString(String certFromSSLClientCertHeader) {
        X509Certificate userCert = null;
        CertificateFactory cf;

        try {
            if (!UtilString.isEmptyTrim(certFromSSLClientCertHeader)) {
                String strcert1 = certFromSSLClientCertHeader.replace(' ', '\n'); // replace .. presledkov damo newline
                String strcert2 = strcert1.substring(28, strcert1.length() - 26); // remove -----BEGIN CERTIFICATE----- in
                // -----END CERTIFICATE----- del, ker sta
                // imela presledke, ki so zdaj newline
                String strcert3 = new String(ConstGlobal.sCertStart + "\n"); // add -----BEGIN CERTIFICATE-----
                String strcert4 = strcert3.concat(strcert2); // dodamo vsebino certa
                String rebuildedCert = strcert4.concat("\n" + ConstGlobal.sCertStop + "\n"); // add -----END CERTIFICATE-----

                try {
                    cf = CertificateFactory.getInstance("X.509");
                    userCert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(rebuildedCert.getBytes("UTF-8")));
                } catch (CertificateException e) {
                    logger.severe("createCertificateFromString(): Error .."
                            + " Msg.: " + e.getMessage());
                    // throw createSOAPFault("VSInvalidParameter.", "VSInvalidParameter");
                } catch (UnsupportedEncodingException e) {
                    logger.severe("createCertificateFromString(): Error .."
                            + " Msg.: " + e.getMessage());
                    // throw createSOAPFault("VSInvalidParameter.", "VSInvalidParameter");
                }
            }
        } catch (Exception e) {
            logger.severe("createCertificateFromString(): Error .."
                    + " Msg.: " + e.getMessage());
        }
        return userCert;
    }
}
