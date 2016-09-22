import java.util.*;


class AStarNode implements Comparable
{
    public SlidingBoard board;
    public ArrayList<SlidingMove> path = new ArrayList<>(); //corresponding path;
    public int manCost;

    public AStarNode(SlidingBoard brd, ArrayList<SlidingMove> paths) {
        this.board = brd;
        this.path = paths;
        this.manCost = Math.abs(this.path.size()) + Math.abs(this.getManCost(this.board));
    }

    public int compareTo(Object otherNode) {
        AStarNode other = (AStarNode) otherNode;
        return new Double(manCost).compareTo(new Double(other.manCost));
    }

    public String toString() {
        return "AStarNode: " + this.path + "\t" + this.manCost + "\n";
    }
    
    public int getManCost(SlidingBoard board) 
    { //loop thru board, if number of iterations not the same as value, calculate distance
        SlidingBoard finalState = new SlidingBoard(board.size);
        finalState.initBoard();
        int count = 0;
        int num_itz = 0;
        for (int i = 0; i < board.size; i++)
            for (int j = 0; j < board.size; j++)
            {
                int value = board.board[i][j];
                num_itz++; 
                if (value != num_itz)
                {
                    count += Math.abs(i - finalState.board[i][j] + Math.abs(j - finalState.board[i][j]));
                }
            }
        return count;
    }

    public void getAstar(HashSet<String> seenIt, PriorityQueue<AStarNode> pq)
    {
        ArrayList<SlidingMove> legal = this.board.getLegalMoves();
        for (SlidingMove move : legal) {
            SlidingBoard childBoard = new SlidingBoard(this.board.size);
            childBoard.setBoard(this.board);
            childBoard.doMove(move);
            if (!seenIt.contains(childBoard.toString())) {
                seenIt.add(childBoard.toString());
                ArrayList<SlidingMove> childPath =
                        (ArrayList<SlidingMove>)this.path.clone();
                childPath.add(move);
                pq.add(new AStarNode(childBoard, childPath));
            }
        }
    }
}

class AStarBot extends SlidingPlayer
{
    ArrayList<SlidingMove> path; //list of paths to solution
    int move_number = -1; //start moves at 0

    public AStarBot(SlidingBoard _sb)
    {
        super(_sb);
        this.path = aStar_findPath(_sb);
    }

    public SlidingMove makeMove(SlidingBoard board)
    {
        move_number++; //increase moves
        return path.get(move_number); //get the move that corresponds to current move number
    }

    public ArrayList<SlidingMove> aStar_findPath(SlidingBoard board)
    {
        HashSet<String> seenIt = new HashSet<>();
        PriorityQueue<AStarNode> pq = new PriorityQueue<>();
        ArrayList<SlidingMove> sm = new ArrayList<>();
        AStarNode currentNode = new AStarNode(board, sm);

        while (!currentNode.board.isSolved())
        {
            currentNode.getAstar(seenIt, pq);
            currentNode = pq.poll();
        }
        return currentNode.path;
    }

}








