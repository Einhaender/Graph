package misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogFormatter
  extends Formatter
{
  private static final DateFormat date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  public static Logger log;
  
  public String format(LogRecord logRec)
  {
    String s = "";
    s = s + date.format(new Date(System.currentTimeMillis()));
    String methName = logRec.getSourceMethodName();
    String methClas = logRec.getSourceClassName();
    int lengthTot = methName.length() + methClas.length();
    if (lengthTot > 28)
    {
      methName = methName.substring(0, methName.length() - (lengthTot - 28) / 2 - 1) + "…";
      methClas = methClas.substring(0, methClas.length() - (lengthTot - 28) / 2 - 1) + "…";
    }
    s = s + " [" + methClas + "] [" + methName + "] ";
    while (s.length() < 54) {
      s = s + ' ';
    }
    s = s + ':';
    s = s + logRec.getMessage();
    s = s + '\n';
    return s;
  }
  
  public static void loggerSetFormatter(Logger log)
  {
    log.setUseParentHandlers(false);
    LogFormatter lf = new LogFormatter();
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.ALL);
    handler.setFormatter(lf);
    
    Handler[] h = log.getHandlers();
    for (int x = 0; x < h.length; x++) {
      log.removeHandler(h[x]);
    }
    log.addHandler(handler);
  }
}