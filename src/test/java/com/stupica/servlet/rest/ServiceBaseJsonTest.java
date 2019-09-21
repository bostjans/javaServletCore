package com.stupica.servlet.rest;


import com.stupica.ConstGlobal;
import com.stupica.ConstWeb;
import com.stupica.GlobalVar;
import com.stupica.servlet.ResultProcessWeb;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ServiceBaseJsonTest {

    ServiceBaseJson     objService;
    HttpServletResponse objResponse;


    @Before
    public void setUp() {
        System.out.println("Setup: ..");
        GlobalVar.bIsModeVerbose = true;

        objService = new ServiceBaseJson();
        objService.bIsJsonEnvelopeMode = true;
        objService.bIsJsonPrettyPrintMode = true;

        objResponse = new HttpServletResponse() {
            public void addCookie(Cookie cookie) {
            }

            public boolean containsHeader(String s) {
                return false;
            }

            public String encodeURL(String s) {
                return null;
            }

            public String encodeRedirectURL(String s) {
                return null;
            }

            public String encodeUrl(String s) {
                return null;
            }

            public String encodeRedirectUrl(String s) {
                return null;
            }

            public void sendError(int i, String s) throws IOException {

            }

            public void sendError(int i) throws IOException {

            }

            public void sendRedirect(String s) throws IOException {

            }

            public void setDateHeader(String s, long l) {

            }

            public void addDateHeader(String s, long l) {

            }

            public void setHeader(String s, String s1) {

            }

            public void addHeader(String s, String s1) {

            }

            public void setIntHeader(String s, int i) {

            }

            public void addIntHeader(String s, int i) {

            }

            public void setStatus(int i) {

            }

            public void setStatus(int i, String s) {

            }

            public int getStatus() {
                return 0;
            }

            public String getHeader(String s) {
                return null;
            }

            public Collection<String> getHeaders(String s) {
                return null;
            }

            public Collection<String> getHeaderNames() {
                return null;
            }

            public String getCharacterEncoding() {
                return null;
            }

            public String getContentType() {
                return null;
            }

            public ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            public PrintWriter getWriter() throws IOException {
                PrintWriter objWritter = new PrintWriter ("testfile.txt");
                //return null;
                return objWritter;
            }

            public void setCharacterEncoding(String s) {

            }

            public void setContentLength(int i) {

            }

            public void setContentType(String s) {

            }

            public void setBufferSize(int i) {

            }

            public int getBufferSize() {
                return 0;
            }

            public void flushBuffer() throws IOException {

            }

            public void resetBuffer() {

            }

            public boolean isCommitted() {
                return false;
            }

            public void reset() {

            }

            public void setLocale(Locale locale) {

            }

            public Locale getLocale() {
                return null;
            }
        };
    }


    @Test
    public void test_prepareSendResponse() {
        // Local variables
        int             iResult;
        ResultProcessWeb    objResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;
        objResult = new ResultProcessWeb();
        System.out.println("--");
        System.out.println("Test: test_prepareSendResponse() - " + this.getClass().getName());

        objResult.iResult = ConstGlobal.RETURN_NODATA;
        objResult.iResultWeb = ConstWeb.HTTP_RESP_NO_CONTENT;
        objResult.bIsDone = true;
        objResult.sMsg.append("No User for/by Username!"
                + "\t sUser: " + "test"
                + ";\t iResult: " + iResult);

        iResult = objService.prepareSendResponse(objResponse, objResult);
        // Error ..
        if (iResult != ConstGlobal.RETURN_OK) {
            objResult.sMsg.append("\n\tError at checkAuthorizeRequest() operation!");
            System.err.println("doGet(): " + objResult.sMsg.toString());
        }
        assertEquals(ConstGlobal.RETURN_OK, iResult);
    }
}
