import chess.*;
import client.Repl;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        String url = "http://localhost:" + port;

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        new Repl(url).run();
    }
}