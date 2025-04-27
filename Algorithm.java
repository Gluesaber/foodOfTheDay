import java.util.*;

public class Algorithm{
    // Add this field to store the last restaurant distances.
    private Map<Vertex, Integer> restaurantInRangeCache = null;

    public Map<Vertex, Integer> dijkstra(Graph graph, Vertex startVertex){
        Map<Vertex, Integer> dist = new HashMap<>();
        PriorityQueue pq = new PriorityQueue();

        for(Vertex vertex : graph.adjacencyList.keySet()){
            dist.put(vertex, Integer.MAX_VALUE);
        }
        dist.put(startVertex, 0);
        pq.enqueue(new PQNode(startVertex, 0));

        while(!pq.isEmpty()){
            PQNode current = pq.dequeue();
            Vertex currentVertex = current.element;
            int currentDistance = current.priority;

            if(currentDistance > dist.get(currentVertex)) continue;

            for(Edge edge : graph.adjacencyList.get(currentVertex)){
                Vertex neighbor = edge.destination;
                int newDist = currentDistance + edge.weight;

                if(newDist < dist.get(neighbor)){
                    dist.put(neighbor, newDist);
                    pq.enqueue(new PQNode(neighbor, newDist));
                }
            }
        }

        return dist;
    }

    public Map.Entry<Vertex, Integer> getRestaurant(Graph graph, Vertex home, short maxDist){
        if(restaurantInRangeCache == null){
            restaurantInRangeCache = dijkstra(graph, home);
        }
        Map<Vertex, Integer> restaurantInRange = new HashMap<>(restaurantInRangeCache);
        restaurantInRange.entrySet().removeIf(entry -> entry.getValue() > maxDist);

        if(restaurantInRange.isEmpty()){
            System.out.println("No restaurants within " + maxDist + " km.");
            return null;
        }
        List<Map.Entry<Vertex, Integer>> entries = new ArrayList<>(restaurantInRange.entrySet());

        Random r = new Random();
        Map.Entry<Vertex, Integer> chosen;
        while(true){
            chosen = entries.get(r.nextInt(entries.size()));
            if(chosen.getKey().menu.root != null) break;
        }

        return chosen;
    }

    public void resetCache(){
        restaurantInRangeCache = null;
    }
}