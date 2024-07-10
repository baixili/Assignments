import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;

    private static Map<Gnode, Double> pageRanks = null;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. Compute the fromLinks and toLinks for each node
    public static void computeLinks(Graph graph){
        // TODO
        for(Edge edge: graph.getOriginalEdges()){
            edge.fromNode().addToLinks(edge.toNode());
            edge.toNode().addFromLinks(edge.fromNode());
        }
        printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
      
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (Gnode node : graph.getNodes().values()){
            System.out.print("\nNode: "+node.toString());
            //for each node display the in edges 
            System.out.print("\nIn links to nodes:");
            for(Gnode c:node.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to nodes:");
            //for each node display the out edges 
            for(Gnode c: node.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        // TODO
        pageRanks = new HashMap<>();

        double nNodes = graph.getNodes().size();
        //double newPageRank = 0;
        for(Gnode node: graph.getNodes().values()){
            pageRanks.put(node, 1.0/nNodes);
        }
        int count = 1;
        while(count <= iter){
            double noOutLinkShare = 0;
            // get the page rank shared from all the node which does not have any out-neighbours
            for(Gnode node: graph.getNodes().values()){
                if(node.getToLinks().size() == 0){
                    noOutLinkShare += dampingFactor * (pageRanks.get(node) / nNodes);
                }
            }
            Map<Gnode, Double> newPageRanks = new HashMap<>();
            for(Gnode node: graph.getNodes().values()){
                // page rank for random jump and from the node with no out link
                double randomJumpShare = noOutLinkShare + (1 - dampingFactor) / nNodes;
                double neighboursShare = 0;
                
                // the page rank that will shared from all the in-neighbour nodes
                for(Gnode back: node.getFromLinks()){
                    neighboursShare += pageRanks.get(back)/back.getToLinks().size();
                }
                // find the total page rank for each node
                newPageRanks.put(node, randomJumpShare + dampingFactor * neighboursShare); 
            }
            pageRanks = newPageRanks;
            
            
            count++;
        }
        System.out.println("Ite 10: ");
        for(Gnode node: pageRanks.keySet()){
        System.out.println("Node: " + node + ": " + pageRanks.get(node));
        }
        computeMostImpneighbour(graph);

        // END TODO

    }

    public static void computeMostImpneighbour(Graph graph){
        // TODO
        System.out.println("\nmost important neighbours:");
        
        for(Gnode node: graph.getNodes().values()){
            double max = Double.MIN_VALUE;
            Gnode important = null;
            for(Gnode neighbour: node.getFromLinks()){
                double pageRank = pageRanks.get(neighbour);
                double shared = pageRank / neighbour.getToLinks().size();
                if(shared > max){
                    max = shared;
                    important = neighbour;
                }
            }
            if(important == null){
                System.out.println("Node " + node.getName() + ": Null");
            }
            else{
                System.out.println("Node " + node.getName() + ": " + important.getName());
            }
            
            
            
        }
        // END TODO
    }

}
