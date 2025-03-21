package client;

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
        StringBuilder entryMessage = getEntryString();
        System.out.println(entryMessage);
        resetText();
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "\n" + ChessClient.help());

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
        System.out.println(getExitString());
    }

    private static StringBuilder getEntryString() {
        StringBuilder entryMessage = new StringBuilder();
        entryMessage.append(EscapeSequences.SET_BG_COLOR_WHITE);
        entryMessage.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        entryMessage.append("\n\t");
        entryMessage.append(EscapeSequences.BLACK_KING);
        entryMessage.append(EscapeSequences.SET_TEXT_ITALIC);
        entryMessage.append("Welcome to Chess! Sign in to start.");
        entryMessage.append(EscapeSequences.RESET_TEXT_ITALIC);
        entryMessage.append(EscapeSequences.BLACK_KING);
        entryMessage.append("\n");
        return entryMessage;
    }

    private static StringBuilder getExitString() {
        StringBuilder exitMessage = new StringBuilder();

        exitMessage.append(EscapeSequences.SET_BG_COLOR_MAGENTA);
        exitMessage.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        exitMessage.append("\n\t");
        exitMessage.append(EscapeSequences.BLACK_KING);
        exitMessage.append("Goodbye!");
        exitMessage.append(EscapeSequences.BLACK_KING);
        exitMessage.append("\n");
        return exitMessage;
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
