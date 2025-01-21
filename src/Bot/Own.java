package Bot;

import main.GamePanel;
import main.Pair;

import java.util.ArrayList;


public class Own {
    GamePanel panel;

    public Own (GamePanel panel){
        this.panel = panel;
    }

    public void update(){
        if (!panel.end) {
            Pair<Integer, Integer> move = bestMove();
            panel.cellButtons[move.first][move.second].button = true;
        }
    }

    public ArrayList<Pair> getAvailableMoves(){
        ArrayList<Pair> availableMoves = new ArrayList<>();
        Pair move1 = panel.history.get(panel.history.size() - 1);
        Pair move2 = new Pair(-1, -1);
        if(panel.history.size() > 1) {
            move2 = panel.history.get(panel.history.size() - 2);
        }

        for (int i = (int)move1.first - 5; i <= (int)move1.first + 5; i++){
            for (int j = (int)move1.second - 5; j <= (int)move1.second + 5; j++){
                if (i >= 0 && i < panel.screenRow - 1 && j >= 0 && j < panel.screenCol){
                    if(panel.Board[i][j] == 0) {
                        availableMoves.add(new Pair(i, j));
                    }
                }
            }
        }

        for (int i = (int)move2.first - 5; i <= (int)move2.first + 5; i++){
            for (int j = (int)move2.second - 5; j <= (int)move2.second + 5; j++){
                if (i >= 0 && i < panel.screenRow - 1 && j >= 0 && j < panel.screenCol){
                    if(panel.Board[i][j] == 0) {
                        availableMoves.add(new Pair(i, j));
                    }
                }
            }
        }
        return availableMoves;
    }

    public int minimax(int depth, int alpha, int beta, boolean XPlayer){
        ArrayList<Pair> availableMoves = getAvailableMoves();
        if (availableMoves.size() == 0){
            return 0;
        }

        if (depth == 0){
            return 0;
        }

        if (XPlayer){
            int best = -1000;
            for (Pair <Integer, Integer> move: availableMoves){
                panel.Board[move.first][move.second] = 1;
                if (panel.checkWin(move.first, move.second, 1) > 0){
                    panel.Board[move.first][move.second] = 0;
                    return 1;
                }
                best = Math.max(best, minimax(depth - 1, alpha, beta,false));
                panel.Board[move.first][move.second] = 0;
                alpha = Math.max(alpha, best);
                if (beta <= alpha){
                    break;
                }
            }
            return best;
        }
        else {
            int best = 1000;
            for (Pair <Integer, Integer> move: availableMoves){
                panel.Board[move.first][move.second] = -1;
                if (panel.checkWin(move.first, move.second, -1) > 0){
                    panel.Board[move.first][move.second] = 0;
                    return -1;
                }
                best = Math.min(best, minimax(depth - 1, alpha, beta,true));
                panel.Board[move.first][move.second] = 0;
                beta = Math.min(beta, best);
                if (beta <= alpha){
                    break;
                }
            }
            return best;
        }
    }

    public Pair<Integer, Integer> bestMove(){
        int bestVal = 1000; // Giá trị tốt nhất ban đầu cho O (tối thiểu hóa)
        Pair<Integer, Integer> bestMove = new Pair(-1, -1); // Nước đi tốt nhất ban đầu
        ArrayList<Pair> availableMoves = getAvailableMoves();

        for (Pair<Integer, Integer> move : availableMoves) {
            panel.Board[move.first][move.second] = -1; // Giả lập nước đi của O
            int moveVal = minimax(3, -1000, 1000, true); // Tính giá trị nước đi với độ sâu 2
            panel.Board[move.first][move.second] = 0; // Hoàn tác nước đi

            if (moveVal < bestVal) { // Nếu giá trị tốt hơn (nhỏ hơn), cập nhật nước đi tốt nhất
                bestVal = moveVal;
                bestMove = move;
            }
        }
        return bestMove;
    }
}
