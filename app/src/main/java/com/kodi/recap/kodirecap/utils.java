package com.kodi.recap.kodirecap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class utils {
    public static String getInterceptorHtml(){
        return "<html>\n" +
                " <head>\n" +
                "  <title>reCAPTCHA</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <script src=\"https://www.google.com/recaptcha/api.js\" async defer></script>\n" +
                "  <script>\n" +
                "      var onloadCallback = function() {\n" +
                "          grecaptcha.render('captcha', {\n" +
                "          'sitekey' : '{site-key}',\n" +
                "          'callback' : sendToken,\n" +
                "          'size' : 'invisible'\n" +
                "          });\n" +
                "          grecaptcha.execute ();\n" +
                "      };\n" +
                "           \n" +
                "      function sendToken(t){\n" +
                "          Android.sendToken(String(t));\n" +
                "      }\n" +
                "   </script>\n" +
                "   </head>\n" +
                "<body>\n" +
                "<h1>Lade...</h1>\n" +
                "  <div id=\"captcha\"></div>\n" +
                "  <script src=\"https://www.google.com/recaptcha/api.js?onload=onloadCallback&amp;render=explicit\" async=\"\" defer=\"\">\n" +
                "  </script>\n" +
                "  </body></html>";
    }
}
