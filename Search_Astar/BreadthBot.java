import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


class BreadthBot extends SlidingPlayer {


    ArrayList<SlidingMove> path; //list of paths to solution
    int move_number = -1; //start moves at 0

    public BreadthBot(SlidingBoard _sb)
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
        LinkedList<Node> queue = new LinkedList<Node>(); //initialize  list of Nodes
        ArrayList<SlidingMove> tempPath = new ArrayList<>(); //temporary list for paths
        Node currentNode = new Node(board, tempPath); //make new Node
        while(!currentNode.board.isSolved())
        {
            currentNode.getChildren(seenIt, queue);
            currentNode = queue.get(0); //get the first element
            queue.remove(0);//remove first in queue
        }
        return currentNode.path;
        
    }




}


