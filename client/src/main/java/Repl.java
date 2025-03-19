import ui.EscapeSequences;

import java.util.Objects;
import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL) {
        client = new ChessClient(serverURL);
    }

    public void run() {
        resetText();
        System.out.println( EscapeSequences.SET_BG_COLOR_WHITE +
                            EscapeSequences.SET_TEXT_COLOR_BLACK +
                            "\n\t" +
                            EscapeSequences.BLACK_KING +
                            EscapeSequences.SET_TEXT_ITALIC +
                            " Welcome to Chess! Sign in to start. " +
                            EscapeSequences.RESET_TEXT_ITALIC +
                            EscapeSequences.BLACK_KING +
                            "\n");
        resetText();
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + ChessClient.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!Objects.equals(result, "quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                resetText();
                result = client.eval(line);
                System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable ex) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + ex);
            }
        }
        resetText();
        System.out.println( "\n" +
                            EscapeSequences.SET_BG_COLOR_MAGENTA +
                            EscapeSequences.SET_TEXT_COLOR_BLACK +
                            "\n\t" +
                            EscapeSequences.BLACK_KING +
                            "Goodbye!" +
                            EscapeSequences.BLACK_KING +
                            "\n");
    }

    private void resetText() {
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printPrompt() {
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREEN + EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println(">>>");
    }
}
