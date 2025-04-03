import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    private static boolean isInitialized = false;

    public static void setup() throws IOException {
        if (!isInitialized) {
            Logger logger = Logger.getLogger("chessLogger");

            FileHandler fileHandler = new FileHandler("chess.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.setLevel(Level.INFO);
            isInitialized = true;
        }
    }
}
