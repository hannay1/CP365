import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;



public class DepthBot extends SlidingPlayer {


    ArrayList<SlidingMove> path; //list of paths to solution
    int move_number = -1; //start moves at 0

    public DepthBot(SlidingBoard _sb)
    {
        super(_sb);
        this.path = depthFirstSearch(_sb); //path found via DFS
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board)
    {
        move_number++; //increase moves
        return path.get(move_number); //get the move that corresponds to current move number

    }

    public ArrayList<SlidingMove> depthFirstSearch(SlidingBoard board)
    {
        HashSet<String> seenIt = new HashSet<String>(); //keeps track of prior moves (stores resultant board)
        LinkedList<Node> stack = new LinkedList<Node>(); //initialize  list of Nodes
        ArrayList<SlidingMove> tempPath = new ArrayList<>(); //temporary list for paths
        Node currentNode = new Node(board, tempPath); //make new Node
        while(!currentNode.board.isSolved()) //while the current node's board is not solved
        {
            currentNode.getChildren(seenIt, stack);
            //currentNode = stack.pop();

            currentNode = stack.pop(); //pop off last board/move combo
        }
        return currentNode.path; //spit out the correct path
    }







}
