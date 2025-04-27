import java.util.*;

public class Graph extends AdjList{
    public Graph(){
        super();
    }

    public void addVertex(Vertex vertex){
        adjacencyList.put(vertex, new ArrayList<>());
        nameToVertex.put(vertex.name, vertex);
    }

    public void addEdge(Vertex source, Vertex destination, int weight){
        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
    }

    public Vertex getVertex(String name){
        return nameToVertex.get(name);
    }
}