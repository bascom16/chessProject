package client;

import client.websocket.NotificationHandler;
import ui.EscapeSequences;
import websocket.messages.NotificationMessage;
import Logger.LoggerManager;

import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    Logger log = Logger.getLogger("clientLogger");

    public Repl(String serverURL) {
        Logger log = Logger.getLogger("clientLogger");
        LoggerManager.setup(log, "client.log");
        client = new ChessClient(serverURL, this);
    }

    public void run() {
        resetText();
        StringBuilder entryMessage = getEntryString();
        System.out.println(entryMessage);
        resetText();
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "\n" + client.help());

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
                log.warning(ex.getMessage());
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + ex);
            }
        }
        resetText();
        System.out.println(getExitString());
    }

    @Override
    public void notify(NotificationMessage message) {
        resetText();
        System.out.println( "\t" +
                            EscapeSequences.SET_TEXT_COLOR_BLUE +
                            ">>> " +
                            EscapeSequences.SET_TEXT_ITALIC +
                            message.getMessage() +
                            EscapeSequences.RESET_TEXT_ITALIC);
        resetText();
        printPrompt();
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
        resetText();
    }
}
