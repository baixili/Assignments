import java.util.*;

//=============================================================================
//   Finding Articulation Points
//   Finds and returns a collection of all the articulation points in the undirected
//   graph.
//   Uses the algorithm from the lectures, but modified to cope with a not completely
//   connected graph. For a not fully connected graph, an articulation point is one
//   that would break a currently connected component into two or more components
//=============================================================================

public class ArticulationPoints{

    /**
     * Return a collection of all the Stops in the graph that are articulation points.
     */
    public static Collection<Stop> findArticulationPoints(Graph graph) {
        // set all the stop has depth of -1
        for(Stop stop: graph.getStops()){
            stop.setDepth(-1);

        }

        Set<Stop> aPoints = new HashSet<>();

        for(Stop stop: graph.getStops()){
            // if the depth of the stop is -1, indicating that it is not in any group
            if(stop.getDepth() == -1){
                int numSubTree = 0;
                stop.setDepth(0);
                // access the neighbour, if the neighbour has depth -1, analysis it
                for(Stop neighbour: stop.getNeighbourNodes()){
                    if(neighbour.getDepth() == -1){
                        recArtPts(neighbour, 1, stop, aPoints);
                        numSubTree++;
                    }
                }
                // the root is the a point
                if(numSubTree > 1){
                    aPoints.add(stop);
                }
            }
        }


        return aPoints;
    }

    public static int recArtPts(Stop stop, int depth, Stop fromNode, Set<Stop> aPoints){
        // set the depth of the stop
        stop.setDepth(depth);
        // the reachBack is now itself
        int reachBack = depth;

        // for all the neighbours it can reach
        for(Stop neighbour: stop.getNeighbourNodes()){
            // continue if the neighbour is itself
            if(neighbour.compareTo(fromNode) == 0){

            }
            // if the depth of the neighbour is not -1, it is being reached
            else if(neighbour.getDepth() != -1){
                // update the reachback value
                reachBack = Math.min(neighbour.getDepth(), reachBack);
            }
            else{
                // do the recursion to find the reachback of the neighbour
                int childReach = recArtPts(neighbour, depth + 1, stop, aPoints);
                // if the neighbour cannot reach back the lower level, the current stop is a point
                if(childReach >= depth){
                    aPoints.add(stop);
                }
                reachBack = Math.min(childReach, reachBack);
            }
        }
        return reachBack;
    }




}
