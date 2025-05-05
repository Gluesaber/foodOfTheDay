import java.util.*;

class Vertex{
    String name;
    AVLTree menu;
    int totalScore;
    int ratingCount;

    public Vertex(String name, AVLTree menu){
        this.name = name;
        this.menu = menu;
        totalScore = 0;
        ratingCount = 0;
    }

    public void rate(int score){
        totalScore += score;
        ratingCount++;
    }

    public float getRating(){
        if(ratingCount == 0) return 0f;
        return (float)totalScore / ratingCount;
    }
}

class Edge{
    Vertex destination;
    int weight;
    
    public Edge(Vertex destination, int weight){
      this.destination = destination;
      this.weight = weight;
    }
}

public abstract class AdjList{
    protected Map<Vertex, List<Edge>> adjacencyList;
    protected Map<String, Vertex> nameToVertex;

    public AdjList(){
        this.adjacencyList = new HashMap<>();
        this.nameToVertex = new HashMap<>();
    }

    public abstract void addVertex(Vertex vertex);

    public abstract void addEdge(Vertex source, Vertex dest, int weight);

    public abstract void removeVertex(Vertex vertex);

    public abstract void removeEdge(Vertex source, Vertex destination);

    public abstract Vertex getVertex(String name);

    public abstract boolean renameVertex(String name, String newName);
}