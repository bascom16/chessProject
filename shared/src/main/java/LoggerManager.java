import java.io.IOException;
import java.util.logging.*;

public class LoggerManager {

    public static void setup(Logger logger, String fileName) throws IOException {
        if (logger.getHandlers().length == 0) {

            FileHandler fileHandler = new FileHandler(fileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.setLevel(Level.FINE);
        }
    }
}
