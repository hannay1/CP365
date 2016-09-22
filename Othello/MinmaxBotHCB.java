import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.lang.reflect.Array;
import java.util.Random;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by mainpc on 9/3/16.
 */
class BoardNode {


    public OthelloBoard board; //board
    public ArrayList<OthelloMove> path; //our moves
    public ArrayList<Double> minMaxList;
    public int depth;




    public BoardNode(OthelloBoard b, ArrayList<OthelloMove> p, ArrayList<Double> mm)
    {
        this.board = b;
        this.path = p;
        this.depth = path.size();
        this.minMaxList = mm;


    }


}


public class MinimaxBotHCB extends OthelloPlayer {

    // makeMove gets a current OthelloBoard game state as input
    // and then returns an OthelloMove object

    // Your bot knows what color it is playing
    //    because it has a playerColor int field

    // Your bot can get an ArrayList of legal moves:
    //    ArrayList<OthelloMove> moves = board.legalMoves(playerColor);

    // The constructor for OthelloMove needs the row, col, and player color ints.
    // For example, play your token in row 1, col 2:
    //   OthelloMove m = new OthelloMove(1, 2, playerColor);

    // OthelloBoard objects have a public size field defining the
    // size of the game board:
    //   board.size

    // You can ask the OthelloBoard if a particular OthelloMove
    //  flanks in a certain direction.
    // For example:
    //  board.flanksLeft(m) will return true if you can capture pieces to the left of move, m

    // You can ask the board what the current score is.
    //  This is just the difference in checker counts
    //  return the point differential in black's favor
    //  +3 means black is up by 3
    //  -5 means white is up by 5
    // int score = board.getBoardScore();

    // OthelloBoard has a toString:
    //  System.out.println(board);

    // OthelloPlayer superclass has a method to get the color for your opponent:
    //  int opponentColor = getOpponentColor();

    int MAX_SIMS = 4;

    public MinimaxBotHCB(Integer _color) {
        super(_color);
    }


    public OthelloMove makeMove(OthelloBoard board) {
        //figure out opponentColor

        Integer currColor = playerColor;
        int depth = 0;
        ArrayList<BoardNode> simBoards = new ArrayList();
        ArrayList<OthelloMove> path = new ArrayList<>();
        ArrayList<Double> minMax = new ArrayList<>();
        // OthelloBoard currentBoard = new OthelloBoard(board.size, board.hasViewWindow);
        //ArrayList<OthelloMove> legals = board.legalMoves(currColor);
        BoardNode bn = new BoardNode(board, path, minMax);

        BoardNode currentBn = copyNode(bn);
        currentBn = copyNode(bn);

        int emptySquares = 0;
        for(int i = 0; i < board.size; i++){
            for(int j = 0; j < board.size; j++){
                if (board.board[i][j] == 0){
                    emptySquares++;
                }
            }
        }

        if (emptySquares < 10){
            return board.legalMoves(playerColor).get(0);
        }


        while (depth  + 1 <= MAX_SIMS) {
            ArrayList<OthelloMove> legals = currentBn.board.legalMoves(currColor);
            System.out.println();
            depth = currentBn.depth;
           if (depth + 1  != MAX_SIMS) {
                for (OthelloMove m : legals) {
                    BoardNode copy_bn = copyNode(currentBn);
                    System.out.println(currColor);
                    copy_bn = simulateMove(copy_bn, currColor, m);
                  // System.out.println("simulated move...");
                  // System.out.println("copied node: " + copy_bn.board.toString());
                   // copy_bn.path.add(m);
                   // BoardNode cp = new BoardNode(copy_bn.board, copy_bn.path, copy_bn.minMaxList);
                    simBoards.add(copy_bn);
                }
                currColor = otherColor(playerColor);
                currentBn = simBoards.remove(0);
           }
            else
           {
                depth = MAX_SIMS +1;
           }
            //currColor = otherColor(playerColor);
        }
        //currColor = otherColor(playerColor);
        currentBn = pickMove(simBoards, currentBn);
        return currentBn.path.get(0);
    }


    public int otherColor(int color) {
        if (color == 1) {
            return 2;
        } else {
            return 1;
       }

        //return color == 1 ? 2: 1;


    }


    public BoardNode copyNode(BoardNode node) {
        OthelloBoard copy = new OthelloBoard(node.board.size, false);
        ArrayList<OthelloMove> arr_node = new ArrayList<>();
        ArrayList<Double> minimax_list = new ArrayList<>();
        for (int i = 0; i < node.board.size; i++) {
            for (int j = 0; j < node.board.size; j++) {
                copy.board[i][j] = node.board.board[i][j];
            }
        }


        for (int k = 0; k < node.path.size(); k++) {
            arr_node.add(node.path.get(k));
            minimax_list.add(node.minMaxList.get(k));


        }

        BoardNode ret_bord = new BoardNode(copy, arr_node, minimax_list);
        return ret_bord;
    }

    public void minMax(BoardNode node){

        double OPP_WEIGHT = 0.6;
        double PLAYER_WEIGHT = 0.7;
        double oppMoves = (double)(node.minMaxList.get(node.minMaxList.size() - 2));
        double playerMoves = (double)(node.minMaxList.get(node.minMaxList.size() -1 ));
        node.minMaxList.add(node.depth, (oppMoves*OPP_WEIGHT - playerMoves*PLAYER_WEIGHT));
    }


    public BoardNode simulateMove(BoardNode copy, Integer color, OthelloMove move) {
        Double oppMoves = (double) copy.board.legalMoves(otherColor(color)).size();
        copy.board.addPiece(move);
        Double oppDifference = (double) oppMoves - copy.board.legalMoves(otherColor(color)).size();
        copy.minMaxList.add(oppDifference);
        copy.path.add(move);
        printShit(copy);
        return copy;
    }


    public BoardNode pickMove(ArrayList<BoardNode> nodes, BoardNode curr){

        Random r = new Random();
        double rDouble = r.nextDouble();
        System.out.println("in pickmove");
        nodes.add(curr);
        ArrayList<BoardNode> editedList = new ArrayList();
        ArrayList<OthelloMove> path = new ArrayList();


        double bestDiff = Double.MAX_VALUE;
        BoardNode bestNode = nodes.get(0);

        for (int i = 0; i < nodes.size(); i++){
           // printShit(nodes.get(i));
            minMax(nodes.get(i));
            if (nodes.get(i).minMaxList.get(0) < bestDiff && rDouble > 0.1){
                bestDiff = nodes.get(i).minMaxList.get(0);
                bestNode =  nodes.get(i);

            }
        }
        return bestNode;
    }

    public void printShit(BoardNode bn)
    {
        System.out.println("board: " + bn.board.toString());
        System.out.println("path: " + bn.path.toString());
        System.out.println("minmax list: " + bn.minMaxList.toString());
        System.out.println("depth: " + bn.depth);

    }
}

