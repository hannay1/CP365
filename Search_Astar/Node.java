import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Created by mainpc on 9/1/16.
 */

public class Node
{
    //stack holding board/move dictionary

    public SlidingBoard board; //board
    public ArrayList<SlidingMove> path; //corresponding path

    public Node(SlidingBoard b, ArrayList<SlidingMove> p)
    {
        this.board = b;
        this.path = p;
    }

    public void getChildren(HashSet<String> seenIt, LinkedList<Node> stack)
    {
        ArrayList<SlidingMove> legals = this.board.getLegalMoves(); //store legal moves in a list --
        for(SlidingMove move : legals) //for each move in list --> for each child node...
        {
            SlidingBoard childBoard = new SlidingBoard(this.board.size); //make a new board of the same size as current board
            childBoard.setBoard(this.board); //copy board numbers over
            childBoard.doMove(move); //do move m on new board
            if(!seenIt.contains(childBoard.toString())) //check to see if the resultant board has not already been reached
            {
                seenIt.add(childBoard.toString());//if not, then add the new resultant board
                ArrayList<SlidingMove> childPath =
                        (ArrayList<SlidingMove>)this.path.clone(); //clone the path from current board path
                childPath.add(move); //add the new move
                //NODE = (board, path) is now made
                stack.add(new Node(childBoard, childPath)); //push board/corresponding move onto stack
            }
        }
    }





}
