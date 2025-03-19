public class PostLogin implements ClientState {

    public String help() {
        return """
               - help (h) | displays this help menu
               - quit (q) | logout and exit the Chess program
               - logout (l) | logout current user
               - create (c): <name> | create a new Chess game under the given name
               - list (li) | lists all existing games
               - join (j): <ID> [WHITE|BLACK] | join game as specified color
               - observe (o): <ID> | observe game
               """;
    }

    public String eval(String cmd, String... params) {
        throw new RuntimeException("Not implemented");
    }
}
