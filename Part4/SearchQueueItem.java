import java.util.*;

/**
 * AStar search uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information
 * to specify the path to that point, its cost so far, and
 * its estimated total cost
 */

public class SearchQueueItem implements Comparable<SearchQueueItem> {
    private Stop stop;
    private Edge edge;
    private double lengthToNode;
    private double estimateDistance;
    private double times;

    public SearchQueueItem(Stop stop, Edge edge, double lengthToNode, double estimateDistance, double times){
        this.stop = stop;
        this.edge = edge;
        this.lengthToNode = lengthToNode;
        this.estimateDistance = estimateDistance;
        this.times = times;
    }
    // stub needed for file to compile.
    public int compareTo(SearchQueueItem other) {

        // compare the total estimated length first
        if(this.getTimes() < other.getTimes()){
            return -1;
        }
        if(this.getTimes() > other.getTimes()){
            return 1;
        }
        if(this.getEstimate() + this.getLength() < other.getEstimate() + other.getLength()){
            return -1;
        }
        if(this.getEstimate() + this.getLength() > other.getEstimate() + other.getLength()) {
            return 1;
        }
        // compare the estimated length if the total estimated length is the same
        if(this.getEstimate() < other.getEstimate()){
                return -1;
        }
        if(this.getEstimate() > other.getEstimate()){
            return 1;
        }
        return 0;

    }
    
    public Stop getStop(){
        return stop;
    }
    
    public Edge getEdge(){
        return edge;
    }
    
    public double getLength(){
        return lengthToNode;
    }
    
    public double getEstimate(){
        return estimateDistance;
    }
    
    public double getTimes(){
        return times;
    }



}
