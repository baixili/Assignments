/**
 * Implements the A* search algorithm to find the shortest path
 *  in a graph between a start node and a goal node.
 * If start or goal are null, it returns null
 * If start and goal are the same, it returns an empty path
 * Otherwise, it returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class AStar {
    private static final double waitingTime = 600;

    /**
     * Finds the shortest path between two stops
     */
    public static List<Edge> findShortestPath(Stop start, Stop goal) {
        if(start == null || goal == null){
            return null;
        }
        if(start == goal){
            return null;
        }
        NetworkViewer.edgeAndTime = new HashMap<>();
        // store all the node as searchQueueItem based on the estimate distance
        // and total distance (total length between the nodes + estimate distance)
        PriorityQueue<SearchQueueItem> queue = new PriorityQueue<>();
        // store the stop and the corresponding edge
        Map<Stop, Edge> backpointers = new HashMap<>();
        // check if the node is being visited or not
        Set<Stop> visited = new HashSet<>();
        // the start node
        SearchQueueItem startNode = new SearchQueueItem(start, null, 0, start.distanceTo(goal), 0);
        queue.offer(startNode);
        // keep extracting the node with the shortest length as long as the queue is not empty
        while(!queue.isEmpty()){
            SearchQueueItem item = queue.poll();
            // if the node has not been visited yet, visit it and find all its neighbours
            if(!visited.contains(item.getStop())){
                visited.add(item.getStop());
                // add it to the backpointer so we can track it
                backpointers.put(item.getStop(), item.getEdge());
                // if the visited not is the goal, we build the path
                if(item.getStop().equals(goal)){
                    return reconstructPath(start, goal, backpointers);
                }

                // finding all the neighbours of the current node and add them to the queue
                for(Edge edge: item.getStop().getEdges()){
                    if(!visited.contains(edge.toStop())){
                        double updateTime = 0;
                        if(edge.transpType().equals(Transport.WALKING)){
                            updateTime = edge.distance() / Transport.WALKING_SPEED_MPS;
                        }
                        else{
                            double fromStopTime = edge.line().getTimes().get(edge.line().getStops().indexOf(edge.fromStop()));
                            double toStopTime = edge.line().getTimes().get(edge.line().getStops().indexOf(edge.toStop()));
                            if (toStopTime < fromStopTime){
                                toStopTime = edge.line().getTimes().get(edge.line().getTimes().size() - 1);
                            }
                            double timeBetweenStopsInMin = (toStopTime - fromStopTime);

                            if(item.getEdge() == null) { // start
                                updateTime = timeBetweenStopsInMin + waitingTime;
                            }
                            else if(item.getEdge().line() == null){ // from walking to line
                                updateTime = timeBetweenStopsInMin + waitingTime;
                            }
                            else if(edge.line().getId().equals(item.getEdge().line().getId())){ // no transfer
                                updateTime = timeBetweenStopsInMin;
                            }
                            else{ // with the transfer
                                updateTime = timeBetweenStopsInMin + waitingTime;
                            }


                        }
                        updateTime = updateTime / 60;

                        NetworkViewer.edgeAndTime.put(edge, updateTime);

                        SearchQueueItem nextItem = new SearchQueueItem(edge.toStop(), edge,
                                item.getLength() + edge.distance(), edge.toStop().distanceTo(goal), item.getTimes() + updateTime);
                        queue.offer(nextItem);
                    }
                }
            }

            
        }

        return null; // to make the template compile!
    }

    /**
     * This method is used to find all the path through the backpointer
     * @param start
     * @param goal
     * @param backpointers
     * @return path
     */
    public static List<Edge> reconstructPath(Stop start, Stop goal, Map<Stop, Edge> backpointers){
        List<Edge> path = new ArrayList<>();
        // make the goal as the start node
        Stop stopNode = goal;
        // get the edge by the node through the backpointer
        do{
            Edge edge = backpointers.get(stopNode);
            path.add(edge);
            // updating the stop node to the from stop of the edge
            stopNode = edge.fromStop();
        }
        // stop if the node is equal to the start
        while(!stopNode.equals(start));
        Collections.reverse(path);
        return path;
    }

}
