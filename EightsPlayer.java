/*
 * EightsPlayer by Yanfeng Jin (Tony) and Uriel Ulloa
 */

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


/*
 * Solves the 8-Puzzle Game (can be generalized to n-Puzzle)
 */


public class EightsPlayer {

	static Scanner scan = new Scanner(System.in);
	static int size=3; //size=3 for 8-Puzzle
	static int numiterations = 1000;
	static int numnodes; //number of nodes generated
	static int nummoves; //number of moves required to reach goal
	static int nummovessum; //sum of the number of moves required to reach all goals found;
	
	
	public static int getBoardChoice()
	{
		
		System.out.println("single(0) or multiple boards(1)");
		int choice = Integer.parseInt(scan.nextLine());
		
		return choice;
	}
	
	public static int getAlgChoice()
	{
		
		System.out.println("BFS(0) or A* Manhattan Distance(1) or A* <Our Origninal Heuristic>(2)");
		int choice = Integer.parseInt(scan.nextLine());
		
		return choice;
	}

	
	public static void main(String[] args)
	{	
		int boardchoice = getBoardChoice();
		int algchoice = getAlgChoice();
			
		int numsolutions = 0;
		
		Node initNode;
	
		if(boardchoice==0)
			numiterations = 1;
	
		for(int i=0; i<numiterations; i++){
		
			if(boardchoice==0)
				initNode = getUserBoard();
			else
				initNode = generateInitialState();//create the random board for a new puzzle
			
			boolean result=false; //whether the algorithm returns a solution
			
			switch (algchoice){
				case 0: 
					result = runBFS(initNode); //BFS
					break;
				case 1: 
					result = runAStar(initNode, 0); //A* with Manhattan Distance heuristic
					break;
				case 2: 
					result = runAStar(initNode, 1); //A* with your new heuristic
					break;
			}
			
			
			//if the search returns a solution
			if(result){
				
				numsolutions++;
				
				
				System.out.println("Number of nodes generated to solve: " + numnodes);
				System.out.println("Number of moves to solve: " + nummoves);			
				System.out.println("Number of solutions so far: " + numsolutions);
				System.out.println("_______");		
				
			}
			else
				System.out.print(".");
			
		}//for
	
		
		
		System.out.println();
		System.out.println("Number of iterations: " +numiterations);
		
		if(numsolutions > 0){
			// We assume that this line is for average number of moves for all the solutions found. So we need to 
			// divide the sum of moves to reach all goals by number of solutions. 
			System.out.println("Average number of moves for "+numsolutions+" solutions: "+nummovessum/numsolutions);
			System.out.println("Average number of nodes generated for "+numsolutions+" solutions: "+numnodes/numsolutions);
		}
		else
			System.out.println("No solutions in "+numiterations+" iterations.");
		
	}


	public static Node getUserBoard()
	{
		
		System.out.println("Enter board: ex. 012345678");
		String stbd = scan.nextLine();
		
		int[][] board = new int[size][size];
		
		int k=0;
		
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[0].length; j++){
				//System.out.println(stbd.charAt(k));
				board[i][j]= Integer.parseInt(stbd.substring(k, k+1));
				k++;
			}
		}
		
		
		for(int i=0; i<board.length; i++){
			for(int j=0; j<board[0].length; j++){
				//System.out.println(board[i][j]);
			}
			System.out.println();
		}
		
		
		Node newNode = new Node(null,0, board);

		return newNode;
		
		
	}

    
	
	
	/**
	 * Generates a new Node with the initial board
	 */
	public static Node generateInitialState()
	{
		int[][] board = getNewBoard();
		
		Node newNode = new Node(null,0, board);

		return newNode;
	}
	
	
	/**
	 * Creates a randomly filled board with numbers from 0 to 8. 
	 * The '0' represents the empty tile.
	 */
	public static int[][] getNewBoard()
	{
		
		int[][] brd = new int[size][size];
		Random gen = new Random();
		int[] generated = new int[size*size];
		for(int i=0; i<generated.length; i++)
			generated[i] = -1;
		
		int count = 0;
		
		for(int i=0; i<size; i++)
		{
			for(int j=0; j<size; j++)
			{
				int num = gen.nextInt(size*size);
				
				while(contains(generated, num)){
					num = gen.nextInt(size*size);
				}
				
				generated[count] = num;
				count++;
				brd[i][j] = num;
			}
		}
		
		/*
		//Case 1: 12 moves
		brd[0][0] = 1;
		brd[0][1] = 3;
		brd[0][2] = 8;
		
		brd[1][0] = 7;
		brd[1][1] = 4;
		brd[1][2] = 2;
		
		brd[2][0] = 0;
		brd[2][1] = 6;
		brd[2][2] = 5;
		*/
		
		return brd;
		
	}
	
	/**
	 * Helper method for getNewBoard()
	 */
	public static boolean contains(int[] array, int x)
	{ 
		int i=0;
		while(i < array.length){
			if(array[i]==x)
				return true;
			i++;
		}
		return false;
	}
	
	
	/**
     * Prints out all the steps of the puzzle solution and sets the number of moves used to solve this board.
     */
    public static void printSolution(Node node) {
    	// Create a stack to store all the parent nodes of the current node up until the initial node
    	Stack<Node> parentStack = new Stack<Node>();
    	parentStack.add(node);
    	while (node.getparent()!=null) {
    		parentStack.add(node.getparent());
    		node = node.getparent();
    	}
    	
    	// Number of moves
    	nummoves = parentStack.size()-1;
    	
    	// Update cumulative number of moves for all solutions found
    	nummovessum += parentStack.size()-1;
    	 
    	// Print the solution starting from the initial state.
    	while (!parentStack.isEmpty()) {
    		Node topNode = parentStack.pop();
    		topNode.print(topNode);
    	}

    }
	
	
	
	
	/**
	 * Runs Breadth First Search to find the goal state.
	 * Return true if a solution is found; otherwise returns false.
	 */
	public static boolean runBFS(Node initNode)
	{
		Queue<Node> Frontier = new LinkedList<Node>();
		ArrayList<Node> Explored = new ArrayList<Node>();
		
		Frontier.add(initNode); 
		numnodes++;
		int maxDepth = 13;
		
		// While the frontier is not empty and the maximum depth doesn't exceed 13
		while (!Frontier.isEmpty() && Frontier.peek().getdepth()<=maxDepth) {
			Node firstNode = Frontier.poll();
			// If the first node removed from the queue is the goal
			if (firstNode.isGoal()) {
				printSolution(firstNode);
				return true;
			} else {
				// Expand the node and add all its children to the frontier
				ArrayList<int[][]> boards = firstNode.expand();
				
				// Create a child node for each board expanded.
				for (int[][] board : boards) {
					Node tempNode = new Node(firstNode, firstNode.getdepth()+1, board);
					// If the node is not already in frontier, nor is it explored, we add it to the frontier
					if (!Frontier.contains(tempNode) && !Explored.contains(tempNode)) {
						Frontier.add(tempNode);
						numnodes++;
					} 
				}
				Explored.add(firstNode);
			}
		}
		
		// Returns false if no result is found within 13 steps.
		return false;	
	}
	
	
	
	/***************************A* Code Starts Here ***************************/
	
	/**
	 * Runs A* Search to find the goal state.
	 * Return true if a solution is found; otherwise returns false.
	 * heuristic = 0 for Manhattan Distance, heuristic = 1 for your new heuristic
	 */
	public static boolean runAStar(Node initNode, int heuristic)
	{
		// Initialize the frontier, using f value (g value + h value) as the comparator.
		PriorityQueue<Node> frontier = new PriorityQueue<Node>(11,new Comparator<Node>() {
			public int compare (Node node1, Node node2) {
				if ((node1.getgvalue()+node1.gethvalue())>(node2.getgvalue()+node2.gethvalue())) {
					return 1;
				} else if ((node1.getgvalue()+node1.gethvalue())<(node2.getgvalue()+node2.gethvalue())) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		
		ArrayList<int[][]> Explored = new ArrayList<int[][]>();
		int g = 0;
		// The h value of the initial node.
		int initialH = (int) initNode.evaluateHeuristic(heuristic);
		
		// Create the initial node for A star and add it to the frontier.
		Node newInitNode = new Node(initNode.getparent(),g,initialH, initNode.getboard());
		frontier.add(newInitNode);
		numnodes ++;
		
		int maxDepth = 13;
		
		while (!frontier.isEmpty() && frontier.peek().getgvalue() <= maxDepth) {
			// Remove the one with the smallest f value from the frontier.
			Node X = frontier.poll(); 
			if (X.isGoal()) {
				printSolution(X);
				return true;
			}
			
			else {
				Explored.add(X.getboard());
				ArrayList<int[][]> boards = X.expand();
				
				// for every possible next steps
				for (int[][] child : boards) {
					if (!Explored.contains(child)) {
						// Add one to g value (a.k.a. cost/depth)
						g = (int) (X.getgvalue()+1);
						// Create a temp node in order to use the evaluateHeuristic function to calculate the 
						// h value.
						Node tempNode = new Node (X,g,child);
						Node childNode = new Node(X,g,tempNode.evaluateHeuristic(heuristic),child);
						// If the frontier doesn't contain the childnode, add it to the frontier.
						if (!frontier.contains(childNode)) {
							frontier.add(childNode);
							numnodes ++;
						} 
						
						else if (frontier.contains(childNode)) {
							
						}
	
					} 
				}
			}
		}
		
		
		return false;
	}
}
