package Bot;

import main.GamePanel;
import main.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Own {
    private static final int SEARCH_DEPTH = 2;
    private static final int MAX_CANDIDATE_MOVES = 16;
    GamePanel panel;

    public Own (GamePanel panel){
        this.panel = panel;
    }

    public void update(int botTurn){
        if (!panel.end) {
            Pair<Integer, Integer> move = bestMove(botTurn);
            if (move.first >= 0 && move.second >= 0) {
                panel.cellButtons[move.first][move.second].button = true;
            }
        }
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMoves(){
        ArrayList<Pair<Integer, Integer>> availableMoves = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        if (panel.history.size() == 0){
            availableMoves.add(new Pair<>(panel.screenRow / 2, panel.screenCol / 2));
            return availableMoves;
        }

        Pair<Integer, Integer> move1 = panel.history.get(panel.history.size() - 1);
        Pair<Integer, Integer> move2 = new Pair<>(-1, -1);
        if(panel.history.size() > 1) {
            move2 = panel.history.get(panel.history.size() - 2);
        }

        for (int i = move1.first - 5; i <= move1.first + 5; i++){
            for (int j = move1.second - 5; j <= move1.second + 5; j++){
                if (i >= 0 && i < panel.screenRow - 1 && j >= 0 && j < panel.screenCol && panel.Board[i][j] == 0){
                    String key = i + "," + j;
                    if (visited.add(key)) {
                        availableMoves.add(new Pair<>(i, j));
                    }
                }
            }
        }

        for (int i = move2.first - 5; i <= move2.first + 5; i++){
            for (int j = move2.second - 5; j <= move2.second + 5; j++){
                if (i >= 0 && i < panel.screenRow - 1 && j >= 0 && j < panel.screenCol && panel.Board[i][j] == 0){
                    String key = i + "," + j;
                    if (visited.add(key)) {
                        availableMoves.add(new Pair<>(i, j));
                    }
                }
            }
        }
        availableMoves.sort(Comparator.comparingInt((Pair<Integer, Integer> move) ->
                -neighborScore(move.first, move.second)));
        if (availableMoves.size() > MAX_CANDIDATE_MOVES) {
            return new ArrayList<>(availableMoves.subList(0, MAX_CANDIDATE_MOVES));
        }
        return availableMoves;
    }

    public int minimax(int depth, int alpha, int beta, int currentTurn, int botTurn){
        ArrayList<Pair<Integer, Integer>> availableMoves = getAvailableMoves();
        if (availableMoves.size() == 0){
            return 0;
        }

        if (depth == 0){
            return 0;
        }

        if (currentTurn == botTurn){
            int best = -1000; // Maximize for bot
            for (Pair <Integer, Integer> move: availableMoves){
                panel.Board[move.first][move.second] = currentTurn;
                if (panel.checkWin(move.first, move.second, currentTurn) > 0){
                    panel.Board[move.first][move.second] = 0;
                    return 1;
                }
                best = Math.max(best, minimax(depth - 1, alpha, beta, -currentTurn, botTurn));
                panel.Board[move.first][move.second] = 0;
                alpha = Math.max(alpha, best);
                if (beta <= alpha){
                    break;
                }
            }
            return best;
        }
        else {
            int best = 1000; // Minimize for opponent
            for (Pair <Integer, Integer> move: availableMoves){
                panel.Board[move.first][move.second] = currentTurn;
                if (panel.checkWin(move.first, move.second, currentTurn) > 0){
                    panel.Board[move.first][move.second] = 0;
                    return -1;
                }
                best = Math.min(best, minimax(depth - 1, alpha, beta, -currentTurn, botTurn));
                panel.Board[move.first][move.second] = 0;
                beta = Math.min(beta, best);
                if (beta <= alpha){
                    break;
                }
            }
            return best;
        }
    }

    public Pair<Integer, Integer> bestMove(int botTurn){
        int bestVal = -1000;
        Pair<Integer, Integer> bestMove = new Pair<>(-1, -1);
        ArrayList<Pair<Integer, Integer>> availableMoves = getAvailableMoves();

        for (Pair<Integer, Integer> move : availableMoves) {
            panel.Board[move.first][move.second] = botTurn;
            if (panel.checkWin(move.first, move.second, botTurn) > 0){
                panel.Board[move.first][move.second] = 0;
                return move;
            }
            int moveVal = minimax(3, -1000, 1000, -botTurn, botTurn);
            panel.Board[move.first][move.second] = 0;

            if (moveVal > bestVal) {
                bestVal = moveVal;
                bestMove = move;
            }
        }
        return bestMove;
    }
}
