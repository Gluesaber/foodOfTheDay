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
        removeEdge(source, destination);
        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
    }

    public void removeVertex(Vertex vertex){
        adjacencyList.remove(vertex);
    
        for(List<Edge> edges : adjacencyList.values()){
            for(int i=0; i<edges.size(); ){
                if(edges.get(i).destination.equals(vertex))
                    edges.remove(i);
                else
                    i++;
            }
        }
        nameToVertex.remove(vertex.name);
    }

    public void removeEdge(Vertex source, Vertex destination){
        List<Edge> srcEdges = adjacencyList.get(source);
        List<Edge> destEdges = adjacencyList.get(destination);
    
        if(srcEdges != null){
            srcEdges.removeIf(edge -> edge.destination.equals(destination));
        }
    
        if(destEdges != null){
            destEdges.removeIf(edge -> edge.destination.equals(source));
        }
    }

    public Vertex getVertex(String name){
        return nameToVertex.get(name);
    }

    public boolean renameVertex(String name, String newName){
        Vertex vertex = nameToVertex.get(name);
    
        if(vertex == null){
            System.out.println("Couldn't find " + name + ".");
            return false;
        }
        if(nameToVertex.containsKey(newName)){
            System.out.println(newName + " is already exist.\n");
            return false;
        }
        nameToVertex.remove(name);
        vertex.name = newName;
        nameToVertex.put(newName, vertex);
    
        return true;
    }
}