import java.util.*;

//=============================================================================
//   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Constructs a Map recording the number of the subgraph for each Stop
//=============================================================================

public class Components{

    // Based on Kosaraju's_algorithm
    // https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm
    // Use a visited set to record which stops have been visited

    // store all the stop and the component id


    public static Map<Stop,Integer> findComponents(Graph graph) {
        Map<Stop, Integer> components = new HashMap<>();

        // check if the stop is visited
        Set<Stop> allVisited = new HashSet<>();

        // start the component id with number 0
        int componentNum = 0;

        // the nodes that could be visited through one node
        List<Stop> nodeList = new ArrayList<>();

        for(Stop stop: graph.getStops()) {

            // set the component id for each stop to be -1, indicating that it has not been added to any component group
            stop.setComponentID(-1);

            // if the stop has not been visited yet, find all the node it can reach
            if(!allVisited.contains(stop)){

                forwardVisit(stop, nodeList, allVisited); // visit forward for all the available neighbours

            }
        }

        // reverse the nodeList
        for(int i = nodeList.size() - 1; i >= 0; i--){
            // if the node has not been added to any component group, check which nodes it can reach
            if(nodeList.get(i).getComponentID() == -1){

                backwardVisit(nodeList.get(i), componentNum, components);
              // update the component group
                componentNum++;
            }
        }

        return components; //just to make it compile.
    }


    /**
     * This method is used to add all the forward node can be reached form one node to the nodeList
     * @param node
     * @param nodeList
     * @param visited
     */
    public static void forwardVisit(Stop node, List<Stop> nodeList, Set<Stop> visited){
        if(!visited.contains(node)){
            visited.add(node);
            // get the out neighbour (forward)
            Collection<Stop> outNeighbour = new HashSet<>();

            // add all the edges can help the node to reach its neighbours
            for(Edge edge: node.getOutEdges()){
                outNeighbour.add(edge.toStop());
            }

            // for the neighbour, do the recursion to see any further neighbours could be reached
            for(Stop stop: outNeighbour){
                forwardVisit(stop, nodeList, visited);
            }
            // after the recursion, add the node to the nodeList
            nodeList.add(node);

        }
    }

    /**
     * This mehtod is used to check the stops could be reached backward through a node
     * @param node
     * @param componentNum
     */
    public static void backwardVisit(Stop node, int componentNum, Map<Stop, Integer> components){

        if(node.getComponentID() == -1){
            // set the node component id to the current component number
            node.setComponentID(componentNum);
            // put the node and the id to the map
            components.put(node, node.getComponentID());

            // find the neighbour edges can reach this current node
            Collection<Edge> inNeighbour = new HashSet<>(node.getInEdges());

            // check further neighbour edges that can reach the neighbours
            for(Edge edge: inNeighbour){
                backwardVisit(edge.fromStop(), componentNum, components);
            }
        }
    }
}