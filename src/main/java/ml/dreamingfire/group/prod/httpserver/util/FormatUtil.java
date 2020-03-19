package ml.dreamingfire.group.prod.httpserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
    public static void info(String msg) {
        System.out.println(new SimpleDateFormat("HH:mm:ss yyyy/MM/dd").format(new Date()) + " [INFO] " + msg);
    }
}
