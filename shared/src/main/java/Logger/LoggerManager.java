package Logger;

import java.io.IOException;
import java.util.logging.*;

public class LoggerManager {

    public static void setup(Logger logger, String fileName) {
        try {
            if (logger.getHandlers().length == 0) {

                FileHandler fileHandler = new FileHandler(fileName, true);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);

                logger.setLevel(Level.FINE);

                logger.info(String.format("""
                ---------------------------------------------
                
                %s initialized""", logger.getName()));
            }
        } catch (IOException ex) {
            System.out.println("Logger uninitialized: " + ex.getMessage());
        }
    }
}
