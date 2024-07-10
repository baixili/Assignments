
import java.util.*;

import javafx.util.Pair;

/**
 * Edmond karp algorithm to find augmentation paths and network flow.
 * <p>
 * This would include building the supporting data structures:
 * <p>
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 * - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 * - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 * <p>
 * <p>
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))
 * <p>
 * The class finds : augmentation paths, their corresponing flows and the total flow
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String, Edge> edges;

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths = null;


    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph) {
        // TODO
        int index = 0;
        edges = new HashMap<>();
        for (Edge edge : graph.getOriginalEdges()) {
            // creating the to edge with to city, from city, 0 capacity and 0 flow
            Edge to = new Edge(edge.toCity(), edge.fromCity(), 0, 0);
            
            Edge from = new Edge(edge.fromCity(), edge.toCity(), edge.capacity(), 0);
            // the forward edge
            edges.put(Integer.toString(index), from);
            // the from city has the forward edge id (even)
            edge.fromCity().addEdgeId(Integer.toString(index));
            // the reverse edge has index that is 1 greater itself forward edge.
            index++;
            // the reverse edge
            edges.put(Integer.toString(index), to);
            edge.toCity().addEdgeId(Integer.toString(index));
            index++;
        }


        printResidualGraphData(graph);  //may help in debugging
        // END TODO
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph) {
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()) {
            System.out.print(city.toString());

            // for each city display the out edges 
            for (String eId : city.getEdgeIds()) {
                System.out.print("[" + eId + "] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge) ->
                System.out.println("[" + eId + "] " + edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================

    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id) {
        return edges.get(id);
    }

    /**
     * find maximum flow
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        // recompute the graph
        computeResidualGraph(graph);
        
        augmentationPaths = new ArrayList<>();
        // ensure all the edges has 0 flows as it is the start
        for (Edge edge : graph.getOriginalEdges()) {
            edge.setFlow(0);
        }
        

        int maxFlow = 0;
        // get the first path
        Pair<ArrayList<String>, Integer> firstPath = bfs(graph, from, to);
        // while the path contains the edge id
        while(firstPath != null){
            //System.out.println("s");
            
            // get the bottle neck path flow
            int pathFlow = firstPath.getValue();
            // update the maximum flow
            maxFlow += pathFlow;
            // add this path to the final Paths pair
            augmentationPaths.add(firstPath);
            // updating the flow, capacity and reversed capacity
            for(String id: firstPath.getKey()){
                // get each edge for the whole path
                Edge edge = edges.get(id);
                
                // update the new flow for each edge
                edge.setFlow(pathFlow);
                
                // update the capacity for each edge
                edge.setCapacity(edge.capacity() - pathFlow);
            
            
                // update 
                Edge reverseEdge = edges.get(String.valueOf(Integer.valueOf(id) + 1));
                reverseEdge.setCapacity(pathFlow);
                
            }
            // get another path
            firstPath = bfs(graph, from, to);
        }
        if(augmentationPaths.size() == 0){
            augmentationPaths = null;
        }



        // END TODO
        return augmentationPaths;
    }

    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<String>();
        // toCity name -> corresponding edge id
        HashMap<String, String> backPointer = new HashMap<String, String>();
        // TODO
        
        for(City city: graph.getCities().values()){
            backPointer.put(city.getName(), null);
        }

        // the queue stores all the cities that we already be reached by the natural order
        Queue<City> queue = new ArrayDeque<>();
        // s is the start city
        queue.offer(s);

        while (!queue.isEmpty()) {

            City current = queue.poll();
            // we want the edge id that current can reach
            for (String id : current.getEdgeIds()) {
                // get the current edge for the current city based on the id
                Edge edge = edges.get(id);
                // the toCity of the edge cannot be the starting point
                // the toCity has not been reached yet since the edge id for toCity is null
                // the capacity need to > 0
                if (edge.toCity() != s && backPointer.get(edge.toCity().getName()) == null && edge.capacity() != 0) {
                    // we reach the toCity through the edge with the right id
                    backPointer.put(edge.toCity().getName(), id);
                    if (backPointer.get(t.getName()) != null) {
                        String pathEdgeId = backPointer.get(t.getName());
                        //System.out.println(edges.get(pathEdgeId).toCity().getName());
                        while (pathEdgeId != null) {
                            // add the id of the edge to the path
                            augmentationPath.add(pathEdgeId);
                            
                            pathEdgeId = backPointer.get(edges.get(pathEdgeId).fromCity().getName());
                        }
                        Collections.reverse(augmentationPath);
                        return new Pair(augmentationPath, bottleneck(augmentationPath));

                    }
                    queue.offer(edge.toCity());
                }
            }
        }

        //System.out.println("s");
        // END TODO
        return null;
        
    }

    public static int bottleneck(ArrayList<String> augmentationPath){
        int max = Integer.MAX_VALUE;
        for(String id: augmentationPath){
 
            if(edges.get(id).capacity() < max){
                max = edges.get(id).capacity();
            }
        }
        System.out.println(max);
        
        return max;
    }
}



