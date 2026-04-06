package Bot;

import main.GamePanel;
import main.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;


public class Own {
    private static final int EARLY_GAME_DEPTH = 2;
    private static final int MID_GAME_DEPTH = 3;
    private static final int LATE_GAME_DEPTH = 4;
    private static final int MAX_CANDIDATE_MOVES = 32;
    private static final int SEARCH_RADIUS = 2;
    private static final int WIN_SCORE = 10_000_000;
    private static final int TT_MAX_SIZE = 400_000;
    private static final int TT_EXACT = 0;
    private static final int TT_LOWER_BOUND = 1;
    private static final int TT_UPPER_BOUND = 2;
    private static final int[][] DIRECTIONS = {
            {1, 0},  // Vertical
            {0, 1},  // Horizontal
            {1, 1},  // Main diagonal
            {1, -1}  // Anti diagonal
    };
    private static class TTEntry {
        int depth;
        int value;
        int flag;
        int row;
        int col;
    }

    GamePanel panel;
    private final int rows;
    private final int cols;
    private final int[] influenceCount;
    private final Set<Integer> frontierCells = new HashSet<>(512);
    private int lastSearchDepth = EARLY_GAME_DEPTH;
    private final long[][][] zobrist;
    private final long xToMoveKey;
    private final long oToMoveKey;
    private final long botXPerspectiveKey;
    private final long botOPerspectiveKey;
    private final Map<Long, TTEntry> transpositionTable = new HashMap<>(262_144);

    public Own (GamePanel panel){
        this.panel = panel;
        this.rows = panel.screenRow - 1;
        this.cols = panel.screenCol;
        this.influenceCount = new int[rows * cols];
        SplittableRandom random = new SplittableRandom(20260406L);
        this.zobrist = new long[rows][cols][2];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                zobrist[i][j][0] = random.nextLong();
                zobrist[i][j][1] = random.nextLong();
            }
        }
        this.xToMoveKey = random.nextLong();
        this.oToMoveKey = random.nextLong();
        this.botXPerspectiveKey = random.nextLong();
        this.botOPerspectiveKey = random.nextLong();
    }

    public void update(int botTurn){
        if (!panel.end) {
            panel.botThinking = true;
            panel.botStatus = "thinking";
            long startMs = System.currentTimeMillis();
            Pair<Integer, Integer> move = bestMove(botTurn);
            long elapsedMs = System.currentTimeMillis() - startMs;
            if (move.first >= 0 && move.second >= 0) {
                panel.botStatus = "doing (" + elapsedMs + "ms)";
                System.out.println("Bot thinking time: " + elapsedMs + "ms (d=" + lastSearchDepth + "). Move: (" + move.first + ", " + move.second + ")");
                panel.cellButtons[move.first][move.second].button = true;
            } else {
                panel.botStatus = "no valid move";
                System.out.println("Bot has no valid move.");
            }
            panel.botThinking = false;
        }
    }

    private int toIndex(int row, int col) {
        return row * cols + col;
    }

    private int rowFromIndex(int index) {
        return index / cols;
    }

    private int colFromIndex(int index) {
        return index % cols;
    }

    private void initializeFrontierState() {
        Arrays.fill(influenceCount, 0);
        frontierCells.clear();
        boolean hasStone = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (panel.Board[i][j] == 0) {
                    continue;
                }
                hasStone = true;
                for (int ni = i - SEARCH_RADIUS; ni <= i + SEARCH_RADIUS; ni++) {
                    for (int nj = j - SEARCH_RADIUS; nj <= j + SEARCH_RADIUS; nj++) {
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                            influenceCount[toIndex(ni, nj)]++;
                        }
                    }
                }
            }
        }

        if (!hasStone) {
            frontierCells.add(toIndex(rows / 2, cols / 2));
            return;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = toIndex(i, j);
                if (panel.Board[i][j] == 0 && influenceCount[index] > 0) {
                    frontierCells.add(index);
                }
            }
        }
    }

    private void adjustInfluenceAndFrontier(int row, int col, int delta) {
        for (int ni = row - SEARCH_RADIUS; ni <= row + SEARCH_RADIUS; ni++) {
            for (int nj = col - SEARCH_RADIUS; nj <= col + SEARCH_RADIUS; nj++) {
                if (ni < 0 || ni >= rows || nj < 0 || nj >= cols) {
                    continue;
                }
                int index = toIndex(ni, nj);
                influenceCount[index] += delta;
                if (influenceCount[index] < 0) {
                    influenceCount[index] = 0;
                }
                boolean shouldBeCandidate = panel.Board[ni][nj] == 0 && influenceCount[index] > 0;
                if (shouldBeCandidate) {
                    frontierCells.add(index);
                } else {
                    frontierCells.remove(index);
                }
            }
        }
    }

    private void applyMove(int row, int col, int turn) {
        panel.Board[row][col] = turn;
        adjustInfluenceAndFrontier(row, col, 1);
    }

    private void undoMove(int row, int col) {
        panel.Board[row][col] = 0;
        adjustInfluenceAndFrontier(row, col, -1);
    }

    private ArrayList<Pair<Integer, Integer>> getAllFrontierMoves() {
        ArrayList<Pair<Integer, Integer>> moves = new ArrayList<>(frontierCells.size());
        for (int index : frontierCells) {
            int row = rowFromIndex(index);
            int col = colFromIndex(index);
            if (panel.Board[row][col] == 0) {
                moves.add(new Pair<>(row, col));
            }
        }
        return moves;
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMoves(){
        ArrayList<Pair<Integer, Integer>> availableMoves = getAllFrontierMoves();
        if (availableMoves.isEmpty()) {
            return availableMoves;
        }

        availableMoves.sort(Comparator.comparingInt((Pair<Integer, Integer> move) ->
                -neighborScore(move.first, move.second)));
        if (availableMoves.size() > MAX_CANDIDATE_MOVES) {
            return new ArrayList<>(availableMoves.subList(0, MAX_CANDIDATE_MOVES));
        }
        return availableMoves;
    }

    private ArrayList<Pair<Integer, Integer>> getImmediateWinningMoves(int turn) {
        ArrayList<Pair<Integer, Integer>> winningMoves = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> candidateMoves = getAllFrontierMoves();
        for (Pair<Integer, Integer> move : candidateMoves) {
            panel.Board[move.first][move.second] = turn;
            if (panel.checkWin(move.first, move.second, turn) > 0) {
                winningMoves.add(new Pair<>(move.first, move.second));
            }
            panel.Board[move.first][move.second] = 0;
        }
        return winningMoves;
    }

    private Pair<Integer, Integer> pickMostConnectedMove(ArrayList<Pair<Integer, Integer>> moves) {
        Pair<Integer, Integer> best = moves.get(0);
        int bestScore = Integer.MIN_VALUE;
        for (Pair<Integer, Integer> move : moves) {
            int score = neighborScore(move.first, move.second);
            if (score > bestScore) {
                bestScore = score;
                best = move;
            }
        }
        return best;
    }

    private int neighborScore(int i, int j) {
        int score = 0;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) {
                    continue;
                }
                int ni = i + di;
                int nj = j + dj;
                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && panel.Board[ni][nj] != 0) {
                    score++;
                }
            }
        }
        return score;
    }

    private int linePotentialScore(int i, int j, int turn) {
        int score = 0;
        for (int[] direction : DIRECTIONS) {
            int di = direction[0];
            int dj = direction[1];
            int count = 1;
            int openEnds = 0;

            int ni = i + di;
            int nj = j + dj;
            while (inBoard(ni, nj) && panel.Board[ni][nj] == turn) {
                count++;
                ni += di;
                nj += dj;
            }
            if (inBoard(ni, nj) && panel.Board[ni][nj] == 0) {
                openEnds++;
            }

            ni = i - di;
            nj = j - dj;
            while (inBoard(ni, nj) && panel.Board[ni][nj] == turn) {
                count++;
                ni -= di;
                nj -= dj;
            }
            if (inBoard(ni, nj) && panel.Board[ni][nj] == 0) {
                openEnds++;
            }

            score += patternScore(count, openEnds);
        }
        return score;
    }

    private int quickMoveOrderScore(int i, int j, int turn) {
        if (panel.Board[i][j] != 0) {
            return Integer.MIN_VALUE;
        }

        int opponentTurn = -turn;
        int score = neighborScore(i, j) * 100;

        // If opponent could win by playing here, this move is a critical block.
        panel.Board[i][j] = opponentTurn;
        boolean blocksImmediateLoss = panel.checkWin(i, j, opponentTurn) > 0;
        int blockedThreatScore = linePotentialScore(i, j, opponentTurn);
        panel.Board[i][j] = 0;

        panel.Board[i][j] = turn;
        boolean immediateWin = panel.checkWin(i, j, turn) > 0;
        int myThreatScore = linePotentialScore(i, j, turn);
        panel.Board[i][j] = 0;

        if (immediateWin) {
            score += WIN_SCORE;
        }
        if (blocksImmediateLoss) {
            score += WIN_SCORE / 2;
        }

        // Weight creating own threats more than passive proximity.
        score += myThreatScore * 10;
        score += blockedThreatScore * 2;
        return score;
    }

    private ArrayList<Pair<Integer, Integer>> orderMoves(ArrayList<Pair<Integer, Integer>> availableMoves, int turn) {
        availableMoves.sort(Comparator.comparingInt((Pair<Integer, Integer> move) ->
                -quickMoveOrderScore(move.first, move.second, turn)));
        return availableMoves;
    }

    private boolean inBoard(int i, int j) {
        return i >= 0 && i < rows && j >= 0 && j < cols;
    }

    private int patternScore(int count, int openEnds) {
        if (count >= 5) {
            return WIN_SCORE;
        }
        if (count == 4) {
            if (openEnds == 2) {
                return 500_000;
            }
            if (openEnds == 1) {
                return 50_000;
            }
        }
        if (count == 3) {
            if (openEnds == 2) {
                return 20_000;
            }
            if (openEnds == 1) {
                return 2_000;
            }
        }
        if (count == 2) {
            if (openEnds == 2) {
                return 500;
            }
            if (openEnds == 1) {
                return 50;
            }
        }
        if (count == 1 && openEnds == 2) {
            return 10;
        }
        return 0;
    }

    private int evaluateForTurn(int turn) {
        int score = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (panel.Board[i][j] != turn) {
                    continue;
                }
                for (int[] direction : DIRECTIONS) {
                    int di = direction[0];
                    int dj = direction[1];
                    int prevI = i - di;
                    int prevJ = j - dj;

                    // Evaluate each contiguous segment once from its first cell.
                    if (inBoard(prevI, prevJ) && panel.Board[prevI][prevJ] == turn) {
                        continue;
                    }

                    int count = 0;
                    int ni = i;
                    int nj = j;
                    while (inBoard(ni, nj) && panel.Board[ni][nj] == turn) {
                        count++;
                        ni += di;
                        nj += dj;
                    }

                    int openEnds = 0;
                    if (inBoard(prevI, prevJ) && panel.Board[prevI][prevJ] == 0) {
                        openEnds++;
                    }
                    if (inBoard(ni, nj) && panel.Board[ni][nj] == 0) {
                        openEnds++;
                    }
                    score += patternScore(count, openEnds);
                }
            }
        }
        return score;
    }

    private int evaluateBoard(int botTurn) {
        int botScore = evaluateForTurn(botTurn);
        int opponentScore = evaluateForTurn(-botTurn);
        // Slightly bias defense so bot respects dangerous open lines from opponent.
        return botScore - (opponentScore * 12 / 10);
    }

    private int countPlayedMoves() {
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (panel.Board[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private int chooseSearchDepth(int candidateCount) {
        int playedMoves = countPlayedMoves();
        if (playedMoves < 8) {
            return EARLY_GAME_DEPTH;
        }
        if (playedMoves < 60) {
            return MID_GAME_DEPTH;
        }
        if (candidateCount <= 24) {
            return LATE_GAME_DEPTH;
        }
        return MID_GAME_DEPTH;
    }

    private long computeBoardHash() {
        long hash = 0L;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (panel.Board[i][j] == 1) {
                    hash ^= zobrist[i][j][0];
                } else if (panel.Board[i][j] == -1) {
                    hash ^= zobrist[i][j][1];
                }
            }
        }
        return hash;
    }

    private long transpositionKey(long boardHash, int currentTurn, int botTurn) {
        long key = boardHash;
        key ^= (currentTurn == 1) ? xToMoveKey : oToMoveKey;
        key ^= (botTurn == 1) ? botXPerspectiveKey : botOPerspectiveKey;
        return key;
    }

    private void promoteCachedMove(ArrayList<Pair<Integer, Integer>> moves, TTEntry entry) {
        if (entry == null || entry.row < 0 || entry.col < 0) {
            return;
        }
        for (int i = 0; i < moves.size(); i++) {
            Pair<Integer, Integer> move = moves.get(i);
            if (move.first == entry.row && move.second == entry.col) {
                if (i > 0) {
                    Pair<Integer, Integer> first = moves.get(0);
                    moves.set(0, move);
                    moves.set(i, first);
                }
                return;
            }
        }
    }

    private void storeTransposition(long key, int depth, int alphaBefore, int betaBefore, int bestValue, int bestRow, int bestCol) {
        TTEntry entry = new TTEntry();
        entry.depth = depth;
        entry.value = bestValue;
        entry.row = bestRow;
        entry.col = bestCol;
        if (bestValue <= alphaBefore) {
            entry.flag = TT_UPPER_BOUND;
        } else if (bestValue >= betaBefore) {
            entry.flag = TT_LOWER_BOUND;
        } else {
            entry.flag = TT_EXACT;
        }
        transpositionTable.put(key, entry);
    }

    public int minimax(int depth, int alpha, int beta, int currentTurn, int botTurn, long boardHash){
        if (depth == 0){
            return evaluateBoard(botTurn);
        }

        int alphaBefore = alpha;
        int betaBefore = beta;
        long key = transpositionKey(boardHash, currentTurn, botTurn);
        TTEntry cached = transpositionTable.get(key);
        if (cached != null && cached.depth >= depth) {
            if (cached.flag == TT_EXACT) {
                return cached.value;
            }
            if (cached.flag == TT_LOWER_BOUND) {
                alpha = Math.max(alpha, cached.value);
            } else if (cached.flag == TT_UPPER_BOUND) {
                beta = Math.min(beta, cached.value);
            }
            if (beta <= alpha) {
                return cached.value;
            }
        }

        ArrayList<Pair<Integer, Integer>> availableMoves = getAvailableMoves();
        if (availableMoves.size() == 0){
            return evaluateBoard(botTurn);
        }
        orderMoves(availableMoves, currentTurn);
        promoteCachedMove(availableMoves, cached);

        if (currentTurn == botTurn){
            int best = Integer.MIN_VALUE; // Maximize for bot
            int bestRow = -1;
            int bestCol = -1;
            for (Pair <Integer, Integer> move: availableMoves){
                applyMove(move.first, move.second, currentTurn);
                long nextHash = boardHash ^ zobrist[move.first][move.second][currentTurn == 1 ? 0 : 1];
                int score;
                if (panel.checkWin(move.first, move.second, currentTurn) > 0){
                    score = WIN_SCORE + depth;
                } else {
                    score = minimax(depth - 1, alpha, beta, -currentTurn, botTurn, nextHash);
                }
                undoMove(move.first, move.second);
                if (score > best) {
                    best = score;
                    bestRow = move.first;
                    bestCol = move.second;
                }
                alpha = Math.max(alpha, best);
                if (beta <= alpha){
                    break;
                }
            }
            storeTransposition(key, depth, alphaBefore, betaBefore, best, bestRow, bestCol);
            return best;
        }
        else {
            int best = Integer.MAX_VALUE; // Minimize for opponent
            int bestRow = -1;
            int bestCol = -1;
            for (Pair <Integer, Integer> move: availableMoves){
                applyMove(move.first, move.second, currentTurn);
                long nextHash = boardHash ^ zobrist[move.first][move.second][currentTurn == 1 ? 0 : 1];
                int score;
                if (panel.checkWin(move.first, move.second, currentTurn) > 0){
                    score = -WIN_SCORE - depth;
                } else {
                    score = minimax(depth - 1, alpha, beta, -currentTurn, botTurn, nextHash);
                }
                undoMove(move.first, move.second);
                if (score < best) {
                    best = score;
                    bestRow = move.first;
                    bestCol = move.second;
                }
                beta = Math.min(beta, best);
                if (beta <= alpha){
                    break;
                }
            }
            storeTransposition(key, depth, alphaBefore, betaBefore, best, bestRow, bestCol);
            return best;
        }
    }

    public Pair<Integer, Integer> bestMove(int botTurn){
        initializeFrontierState();

        ArrayList<Pair<Integer, Integer>> winningMoves = getImmediateWinningMoves(botTurn);
        if (!winningMoves.isEmpty()) {
            lastSearchDepth = EARLY_GAME_DEPTH;
            return pickMostConnectedMove(winningMoves);
        }

        ArrayList<Pair<Integer, Integer>> blockingMoves = getImmediateWinningMoves(-botTurn);
        if (!blockingMoves.isEmpty()) {
            lastSearchDepth = EARLY_GAME_DEPTH;
            return pickMostConnectedMove(blockingMoves);
        }

        int bestVal = Integer.MIN_VALUE;
        Pair<Integer, Integer> bestMove = new Pair<>(-1, -1);
        ArrayList<Pair<Integer, Integer>> availableMoves = getAvailableMoves();
        orderMoves(availableMoves, botTurn);
        int searchDepth = chooseSearchDepth(availableMoves.size());
        lastSearchDepth = searchDepth;
        if (transpositionTable.size() > TT_MAX_SIZE) {
            transpositionTable.clear();
        }
        long rootHash = computeBoardHash();

        for (Pair<Integer, Integer> move : availableMoves) {
            applyMove(move.first, move.second, botTurn);
            long nextHash = rootHash ^ zobrist[move.first][move.second][botTurn == 1 ? 0 : 1];
            if (panel.checkWin(move.first, move.second, botTurn) > 0){
                undoMove(move.first, move.second);
                return move;
            }
            int moveVal = minimax(searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, -botTurn, botTurn, nextHash);
            undoMove(move.first, move.second);

            if (moveVal > bestVal) {
                bestVal = moveVal;
                bestMove = move;
            }
        }
        return bestMove;
    }
}
