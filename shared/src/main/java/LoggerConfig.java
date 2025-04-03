import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    public static void setup() throws IOException {
        Logger logger = Logger.getLogger("chessLogger");

        FileHandler fileHandler = new FileHandler("chess.log", true);
        logger.addHandler(fileHandler);

        logger.setLevel(Level.INFO);
    }
}
