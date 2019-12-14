package com.stupica.servlet.http;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.ResultProces;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;
import com.stupica.servlet.ServiceBase;

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
     *     curl -i http://localhost:8080/lenkoRest/mirror/v1/
     *     curl -i http://localhost:8080/lenkoTrRest/mirror/v1/
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Local variables
        int             iResult;
        String              sMsgLog = null;
        String              headers = "";
        StringBuilder       headersLong = new StringBuilder();
        Enumeration<String> headerNames = null;
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
            response.setContentType("text/plain; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers = headers + "Header Name: " + headerName;
                String headerValue = request.getHeader(headerName);
                if (headerValue.length() > 80) {
                    headersLong.append("\nHeader Name: ").append(headerName);
                    headersLong.append("\n\tValue: ").append(headerValue);
                    headers = headers + "\n\tValue: " + headerValue.substring(0, 77) + " ..";
                } else {
                    headers = headers + "\n\tValue: " + headerValue;
                }
                headers = headers + "\n";
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objOut = response.getWriter();
            //objOut.println("<pre>");
            objOut.println("--");
            objOut.println(".. simple HTTP Mirror");
            objOut.println("--");

            //long currentTimeMillis = System.currentTimeMillis();
            Date    dtNow = new Date();
            String requestIdent = "IdDostopa: " + dtNow.getTime();
            logger.info("doGet(): " + requestIdent);
            logger.fine(headers);
            objOut.println("doGet(): " + requestIdent + "\t\t-> Datum: " + UtilDate.toUniversalString(dtNow) + " \n");
            objOut.println("--");
            objOut.println("AuthType:\t" + request.getAuthType());
            objOut.println("ContextPath:\t" + request.getContextPath());
            objOut.println("PathInfo:\t" + request.getPathInfo());
            objOut.println("RemoteUser:\t" + request.getRemoteUser());
            objOut.println("RequestedSessionId: " + request.getRequestedSessionId());
            objOut.println("RequestURI:\t" + request.getRequestURI());
            objOut.println("RemoteAddr:\t" + request.getRemoteAddr());
            objOut.println("RemoteHost:\t" + request.getRemoteHost());
            objOut.println("RemotePort:\t" + request.getRemotePort());
            objOut.println("Scheme:\t" + request.getScheme());
            objOut.println("ServerName:\t" + request.getServerName());
            objOut.println("ServerPort:\t" + request.getServerPort());
            objOut.println("SessionId:\t" + request.getSession().getId());

            objOut.println("\n--");
            objOut.println(headers);
            if (headersLong.length() > 0) {
                objOut.println("\n--");
                objOut.println(headersLong.toString());
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objOut.println("--");
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
            if (certs != null) {
                X509Certificate x509Certificate = certs[0];
                X500Principal subjectX500Principal = x509Certificate.getSubjectX500Principal();
                String dn = subjectX500Principal.getName();
                x509Certificate.getSerialNumber();
                sMsgLog = "javax.servlet.request.X509Certificate (serial): " + x509Certificate.getSerialNumber().toString(16)
                        + "\n\t" + dn;
            } else {
                sMsgLog = "Nismo prejeli klient (client) certifikata.";
            }
            logger.info(sMsgLog);
            objOut.println(sMsgLog);
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            String clientCertString = request.getHeader("SSL_CLIENT_CERT");
            X509Certificate clientCert = createCertificateFromString(clientCertString);
            if (clientCert != null) {
                String sslClientSerial = clientCert.getSerialNumber().toString(16);
                objOut.println("--");
                objOut.println("SSL_CLIENT_CERT: " + sslClientSerial
                        + "\n\t" + clientCert.getSubjectX500Principal().getName());
                logger.info("SSL_CLIENT_CERT:" + sslClientSerial);
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            String clientCertVSString = request.getHeader("SSL_CLIENT_CERT_VS");
            X509Certificate clientCertVS = createCertificateFromString(clientCertVSString);
            if (clientCertVS != null) {
                String sslClientSerial = clientCertVS.getSerialNumber().toString(16);
                objOut.println("--");
                objOut.println("SSL_CLIENT_CERT_VS: " + sslClientSerial
                        + "\n\t" + clientCertVS.getSubjectX500Principal().getName());
            }
        }

        objOut.write("\n");
        objOut.println("--");
        //objOut.println("</pre>");
        objOut.write("\n\nThis is addOn info. ..");

        if (objOut != null) {
            objOut.close();
        }
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
