import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class IdBot extends SlidingPlayer {


    ArrayList<SlidingMove> path; //list of paths to solution
    int move_number = -1; //start moves at 0
    int max_depth = 1;


    public IdBot(SlidingBoard _sb)
    {
        super(_sb);
        this.path = idSearch(_sb); //path found via iDFS
    }

    // Perform a single move based on the current given board state
    public SlidingMove makeMove(SlidingBoard board)
    {
        move_number++; //increase moves
        return path.get(move_number); //get the move that corresponds to current move number

    }

    public ArrayList<SlidingMove> idSearch(SlidingBoard board)
    {
        HashSet<String> seenIt = new HashSet<>(); //keeps track of prior moves (stores resultant board)
        LinkedList<Node> stack = new LinkedList<>(); //initialize  list of Nodes
        ArrayList<SlidingMove> tempPath = new ArrayList<>(); //temporary list for paths
        Node currentNode = new Node(board, tempPath); //make new Node
        int pSize = currentNode.path.size();
        while(!currentNode.board.isSolved() && pSize <= max_depth) //while the current node's board is not solved
        {
            currentNode.getChildren(seenIt, stack);
            if(stack.isEmpty())
            {
                max_depth += 1;
                seenIt.clear();
                tempPath.clear();
                currentNode = new Node(board, tempPath);
            }else
            {
                currentNode = stack.pop(); //pop off last board/move combo
            }
        }
        return currentNode.path; //spit out the correct path
    }


}
