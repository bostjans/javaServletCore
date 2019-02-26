package com.stupica.servlet;


import com.stupica.ConstGlobal;
import com.stupica.core.UtilString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Created by bostjans on 22/06/16.
 */
public class Setting {

    // Define: common filename for App. configuration
    public static final String DEFINE_CONF_FILENAME = "properties.xml";
    // Default Define: env. variable name for App. configuration
    public static final String DEFINE_CONF_FILENAME_ENV = "APP_CONF_FILE";
    public String  sConfFilenameEnv = DEFINE_CONF_FILENAME_ENV;

    // ?
    public static final String PROJECT_NAME = "App.Implementation";
    public static final String PROJECT_NAME_DEF_VAL = "Generic";

    // ?
    public static final String IMPLEMENTATION_TYPE = "App.EnvType";
    public static final String IMPLEMENTATION_TYPE_TEST = "TEST";

    // Define: ..
    public static final String DEFINE_CONF_APP_VERSION = "App.VersionInfo";

    private Properties properties;

    private static Setting objInstance      = null;

    private static Logger logger = Logger.getLogger(Setting.class.getName());


    public static Setting getConfig() {
        if (objInstance == null) {
            objInstance = new Setting();
        }
        return objInstance;
    }


    public Setting() {
        String      s_file_conf = "/" + DEFINE_CONF_FILENAME;
        String      s_temp = null;
        boolean     b_is_from_file_ext = false;
        InputStream is = null;

        // Get conf. from env. variable .. if exists
        try {
            s_temp = System.getenv(sConfFilenameEnv);
            if (UtilString.isEmpty(s_temp)) {
                logger.warning("Setting(): Env. variable " + sConfFilenameEnv
                        + " could NOT be retrieved! Continuing ..");
            } else {
                s_file_conf = s_temp;
                b_is_from_file_ext = true;
            }
        } catch (Exception e) {
            logger.warning("Setting(): Env. variable " + sConfFilenameEnv
                    + " could NOT be retrieved!"
                    + " Msg.: " + e.getMessage());
        }

        // .. open IS
        if (s_file_conf != null) {
            try {
                if (b_is_from_file_ext) {
                    is = new FileInputStream(s_file_conf);
                } else {
                    is = getClass().getResourceAsStream(s_file_conf);
                }
                if (is == null) {
                    logger.severe("Setting(): Error at opening InputStream!"
                            + " File: " + s_file_conf
                            + "; Msg.: NO Application configuration file available!");
                    //throw new Exception("Msg.: NO ePero configuration file available!");
                }
            } catch (IOException e) {
                logger.severe("Setting(): Error at reading InputStream!"
                        + " Reading of Application configuration file NOT successful!"
                        + " File: " + s_file_conf
                        + " Msg.: " + e.getMessage());
                //throw new Exception(" Msg.: " + e.getMessage());
            }
        }

        // .. read Properties ..
        readFromXML(is);

        final String implementation = getString(PROJECT_NAME, PROJECT_NAME_DEF_VAL);
        final String environmentType = getString(IMPLEMENTATION_TYPE, IMPLEMENTATION_TYPE_TEST);
        final String versionInfo = getString(DEFINE_CONF_APP_VERSION, "/");

        logger.info("\t********************************************************************************\n"
                + "\t*** App.: " + implementation
                + "    Env.: " + environmentType
                + "   Version " + versionInfo + " ***"
                + "\n\t********************************************************************************");
    }


    private int readFromXML(InputStream aobj_is) {
        // .. read Properties ..
        if (aobj_is != null) {
            try {
                properties = new Properties();
                properties.loadFromXML(aobj_is);
            } catch (IOException e) {
                logger.severe("Config.readFromXML(): Error at reading configuration properties!"
                        + " Msg.: " + e.getMessage());
            }
        }

        // .. and close IS
        if (aobj_is != null) {
            try {
                aobj_is.close();
            } catch(IOException e) {
                logger.severe("Config.readFromXML(): Exc. at closing InputStream!"
                        + " Msg.: " + e.getMessage());
            }
        }
        return ConstGlobal.RETURN_OK;
    }


    public String getString(String key) {
        String val = null;

        if (properties == null) {
            logger.severe("getString(): Settings were not read! Check logs!");
            return val;
        }
        if (key != null)
            if (!key.isEmpty())
                if (properties.containsKey(key))
                    val = properties.getProperty(key);
        if (val == null) {
            logger.severe("getString(): Entry missing for key: " + key);
        }
        return val;
    }

    public String getString(String key, String def) {
        String val = getString(key);
        if (val == null) return def;
        return val;
    }

    public boolean getBoolean(String key, boolean def) {
        String val = getString(key);
        if (val == null) return def;
        if (val.toLowerCase().contentEquals("true")) {
            return true;
        }
        return false;
    }
}
