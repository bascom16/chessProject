import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(0);
        System.out.println("Started HTTP server on " + port);
        String url = "http://localhost:" + server.port();

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        new Repl(url).run();
        server.stop();
    }
}