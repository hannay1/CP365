import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.lang.reflect.*;

class SlidingMove {
    
    public int row;
    public int col;
    
    public SlidingMove(int _row, int _col) {
        row = _row;
        col = _col;
    }
    
    public String toString() {
        return "MOVE: " + row + ", " + col;
    }
}


class SlidingPlayer {
    
    SlidingBoard sb;
    
    public SlidingPlayer(SlidingBoard _sb) {
        sb = _sb;
    }
    
    /*
     *override this method! :P
     */
    public SlidingMove makeMove(SlidingBoard board) {
        return null;
    }
}


class SlidingBoard {

    //each node is a new SlidingBoard - each [][], etc
    //do a temp swap with a copy of the board OR
    //use undoMove() to undo last move, so 
    
    public int size;
    public int[][] board = null;
    
   
    public SlidingBoard(int _size) {
        size = _size;
        board = new int[size][size];
        //initBoard();
        //randomizeBoard();
    }

    
    public void setBoard(SlidingBoard otherBoard) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = otherBoard.board[r][c];
            }
        }
    }
    
    
    public void initBoard() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = r * size + c;
            }
        }
    }
    
    
    public void randomizeBoard() {
        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            ArrayList<SlidingMove> legalMoves = getLegalMoves();
            int choice = r.nextInt(legalMoves.size());
            doMove(legalMoves.get(choice));
        }
    }
    
    
    public boolean isLegalMove(SlidingMove m) {
        if (m.row - 1 >= 0 && board[m.row-1][m.col] == 0) return true;
        if (m.row + 1 < size && board[m.row+1][m.col] == 0) return true;
        if (m.col - 1 >= 0 && board[m.row][m.col-1] == 0) return true;
        if (m.col + 1 < size && board[m.row][m.col+1] == 0) return true;
        return false;       
    }
    
    
    public ArrayList<SlidingMove> getLegalMoves() {
        ArrayList<SlidingMove> legalMoves = new ArrayList<SlidingMove>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                SlidingMove m = new SlidingMove(r, c);
                if (isLegalMove(m)) {
                    legalMoves.add(m);
                }
            }
        }
        return legalMoves;
    }
    
    
    public boolean isSolved() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] != r * size + c) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void undoMove(SlidingMove m, int direction) {
        
        // had moved up
        if (direction == 0) {
            doMove(new SlidingMove(m.row-1, m.col));
        }
        else if (direction == 1) {
            doMove(new SlidingMove(m.row+1, m.col));
        }
        else if (direction == 2) {
            doMove(new SlidingMove(m.row, m.col-1));
        }
        else if (direction == 3) {
            doMove(new SlidingMove(m.row, m.col+1));
        }
        
    }
    
    public int doMove(SlidingMove m) {
        if (m.row - 1 >= 0 && board[m.row-1][m.col] == 0) {
            int tmp = board[m.row-1][m.col];
            board[m.row-1][m.col] = board[m.row][m.col];
            board[m.row][m.col] = tmp;
            return 0;
        }
        else if (m.row + 1 < size && board[m.row+1][m.col] == 0) {
            int tmp = board[m.row+1][m.col];
            board[m.row+1][m.col] = board[m.row][m.col];
            board[m.row][m.col] = tmp;
            return 1;
        }
        else if (m.col - 1 >= 0 && board[m.row][m.col-1] == 0) {
            int tmp = board[m.row][m.col-1];
            board[m.row][m.col-1] = board[m.row][m.col];
            board[m.row][m.col] = tmp;
            return 2;
        }
        else if (m.col + 1 < size && board[m.row][m.col+1] == 0) {
            int tmp = board[m.row][m.col+1];
            board[m.row][m.col+1] = board[m.row][m.col];
            board[m.row][m.col] = tmp;
            return 3;
        }
        return -1;
    }
    
   
    public String toString() {
        String result = "";
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result += board[r][c] + " ";
            }
            result += "\n";
        }
        return result;
    }
}

//add hash table for curbing mechanism
//also store latest current node

class SlidingGame {
    
    public static int playGame(SlidingBoard sb, SlidingPlayer player, boolean viewPlayback, int playbackDelay) {
        int moves = 0;
        
        while (!sb.isSolved()) {
            SlidingMove m = player.makeMove(sb);
            if (sb.isLegalMove(m)) {
                sb.doMove(m);
            }
            
            moves++;
            if (moves % 1000000 == 0) {
                System.out.println("MOVES: " + moves);
            }
            
            if (viewPlayback) {
                System.out.println(sb);
                try {
                  Thread.sleep(playbackDelay);
                } catch (InterruptedException e) {
                    System.out.println("Execution interrupted!");
                }
            }
        }
        return moves;
    }
    
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        String[] bots = new String[4];
        bots[0] = "DepthBot";
        bots[1] = "BreadthBot";
        bots[2] = "IdBot";
        bots[3] = "AStarBot";

        for(String str : bots)
        {
            System.out.println("\n" + str.toUpperCase() + "/*/*/*/*/*/*/*/*/*/*/*/*/*");
            String botClassName = str;
            int BOARD_SIZE = Integer.parseInt("3");
            int NUMBER_GAMES = Integer.parseInt("1");
            boolean viewPlayback = Integer.parseInt("1") > 0;
            int PLAYBACK_DELAY = Integer.parseInt("0");
            SlidingBoard sb = new SlidingBoard(BOARD_SIZE);
            sb.initBoard();
            sb.randomizeBoard();
            Class cl = Class.forName(botClassName);
            Constructor con = cl.getConstructor(SlidingBoard.class);
            SlidingPlayer player = (SlidingPlayer)con.newInstance(sb);
            int totalMoves = 0;
            for (int i = 0; i < NUMBER_GAMES; i++)
            {
                totalMoves += playGame(sb, player, viewPlayback, PLAYBACK_DELAY);
                sb.randomizeBoard();
            }
            System.out.println("TOTAL:" + totalMoves);
            System.out.println("Average moves required: " + totalMoves / NUMBER_GAMES);
        }
    }
}






















