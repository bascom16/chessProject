public class Gameplay implements ClientState {
    public String help() {
        return """
               
               - help (h) | displays this help menu
               - quit (q) | logout and exit the Chess program
               - logout (l) | logout current user
               - exit (e) | exit current game
               - TBD | Have patience, young padawan
               """;
    }

    public String eval(String cmd, String... params) {
        throw new RuntimeException("Not implemented");
    }
}
