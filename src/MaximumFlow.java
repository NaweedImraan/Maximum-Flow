import java.util.*;

public class MaximumFlow {

    static int countVertex = 0;    //Number of vertices in graph is hardcoded
    //maps array index 0 to "s", & successive indexes to a string equivalent
    private String[] stringEquivalent;  //stringEquivalent[0]="S" & stringEquivalent[countVertex-1]="T"

    public static void main(String[] args) {

        String[] arrayIndexStringEquivalents;

        Scanner vertexScanner = new Scanner(System.in);
        System.out.println("How many nodes should be there?(Excluding S and T nodes)");
        countVertex = vertexScanner.nextInt();


        arrayIndexStringEquivalents = new String[countVertex];
        while (countVertex > 10 || countVertex < 4) {
            System.out.println("Enter a number in the range of 6 to 12");
            countVertex = vertexScanner.nextInt();
        }
        
        Random random = new Random();
        int k = 0;
        int j = 0;
        while (j < countVertex) {

            arrayIndexStringEquivalents[k] = String.valueOf(random.nextInt(10));

            ++j;
            ++k;
        }
        
        int[][] matrix = new int[countVertex][countVertex];
        for (int x = 0; x < countVertex; x++) {
            for (int y = 0; y < countVertex; y++) {

                matrix[x][y] = ((int) (Math.random() * 20));
            }
        }

        MaximumFlow maximumFlow = new MaximumFlow(arrayIndexStringEquivalents);

        int sourceVertex = 0;
        int sinkVertex = countVertex - 1;    //T is the last thing in the list
        System.out.println("\nBasic Ford Fulkerson Max Flow: " + maximumFlow.maxFlow(matrix, sourceVertex, sinkVertex));


    }


    public MaximumFlow(String[] stringEquivalent) {
        this.stringEquivalent = stringEquivalent;  //pass by reference, but don't care since main doesn't modify this
    }

    // Returns max flow from S to T in a graph
    public int maxFlow(int graph[][], int sourceVertex, int sinkVertex) {
        int maximumFlow = 0;
        int base[] = new int[countVertex];   //holds base of a vertex when a path if found (filled by BFS)
        int vertexU = 0; //iterator vertices to loop over the matrix
        int vertexV = 0;

        int residualGraph[][] = new int[countVertex][countVertex]; //residualGraph[i][j] tells you if there's an edge between vertex i & j. 0=no edge, positive number=capacity of that edge
        for (vertexU = 0; vertexU < countVertex; vertexU++) {      //copy over every edge from the original graph into residual
            for (vertexV = 0; vertexV < countVertex; vertexV++) {
                residualGraph[vertexU][vertexV] = graph[vertexU][vertexV];
            }
        }

        while (breathFirstSearch(residualGraph, sourceVertex, sinkVertex, base)) {    //if a path exists from S to T
            String pathString = "";       //Shows the augmented path taken

            //find bottleneck by looping over path from BFS using base[] array
            int bottleneckFlow = Integer.MAX_VALUE;       //we want the bottleneck (minimum), so initially set it to the largest number possible. Loop updates value if it's smaller
            for (vertexV = sinkVertex; vertexV != sourceVertex; vertexV = base[vertexV]) {      //loop backward through the path using base[] array
                vertexU = base[vertexV];    //get the previous vertex in the path
                bottleneckFlow = Math.min(bottleneckFlow, residualGraph[vertexU][vertexV]);       //minimum of previous bottleneck & the capacity of the new edge

                pathString = " --> " + stringEquivalent[vertexV] + pathString; //prepend vertex to path
            }
            pathString = "S" + pathString + "--> T";      //loop stops before it gets to S, so add S to the beginning
            System.out.println("Augmentation path \n" + pathString);
            System.out.println("bottleneck (min flow on path added to max flow) = " + bottleneckFlow + "\n");

            //Update residual graph capacities & reverse edges along the path
            for (vertexV = sinkVertex; vertexV != sourceVertex; vertexV = base[vertexV]) {   //loop backwards over path (same loop as above)
                vertexU = base[vertexV];
                residualGraph[vertexU][vertexV] -= bottleneckFlow;    //back edge
                residualGraph[vertexV][vertexU] += bottleneckFlow;    //forward edge
            }

            maximumFlow += bottleneckFlow;    //add the smallest flow found in the augmentation path to the overall flow
        }

        return maximumFlow;
    }

    //Returns true if it finds a path from S to T
    //saves the vertices in the path in parent[] array
    public boolean breathFirstSearch(int residualGraph[][], int vertexS, int vertexT, int parent[]) {
        boolean visited[] = new boolean[countVertex];  //has a vertex been visited when finding a path. Boolean so all values start as false

        LinkedList<Integer> queueOfVertex = new LinkedList<Integer>();      //queue of vertices to explore (BFS to FIFO queue)
        queueOfVertex.add(vertexS);  //add source vertex
        visited[vertexS] = true;   //visit it
        parent[vertexS] = -1;          //"S" has no parent

        while (!queueOfVertex.isEmpty()) {
            int vertexU = queueOfVertex.remove();       //get a vertex from the queue

            for (int vertexV = 0; vertexV < countVertex; vertexV++) {  //Check all edges to vertexV by checking all values in the row of the matrix
                if (visited[vertexV] == false && residualGraph[vertexU][vertexV] > 0) {  //residualGraph[u][v] > 0 means there actually is an edge
                    queueOfVertex.add(vertexV);
                    parent[vertexV] = vertexU;    //used to calculate path later
                    visited[vertexV] = true;
                }
            }
        }
        return visited[vertexT];   //return true/false if we found a path to T
    }


}