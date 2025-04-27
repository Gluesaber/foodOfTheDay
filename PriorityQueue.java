import java.util.*;

class PQNode{
    Vertex element;
    int priority;

    public PQNode(Vertex element, int priority){
        this.element = element;
        this.priority = priority;
    }
}

public class PriorityQueue{
    private List<PQNode> heap = new ArrayList<>();

    public void enqueue(PQNode PQNode){
        heap.add(PQNode);
        int index = heap.size() - 1;
        while(index > 0){
            int parent = (index - 1) / 2;
            if(heap.get(index).priority >= heap.get(parent).priority) break;
            PQNode temp = heap.get(index);
            heap.set(index, heap.get(parent));
            heap.set(parent, temp);
            index = parent;
        }
    }

    public PQNode dequeue(){
        if(heap.isEmpty()) return null;
        PQNode root = heap.get(0);
        PQNode last = heap.remove(heap.size() - 1);
        if(!heap.isEmpty()){
            heap.set(0, last);
            int index = 0;
            while(true){
                int left = 2 * index + 1;
                int right = 2 * index + 2;
                int smallest = index;
                if(left < heap.size() && heap.get(left).priority < heap.get(smallest).priority)
                    smallest = left;
                if(right < heap.size() && heap.get(right).priority < heap.get(smallest).priority)
                    smallest = right;
                if(smallest == index) break;
                PQNode temp = heap.get(index);
                heap.set(index, heap.get(smallest));
                heap.set(smallest, temp);
                index = smallest;
            }
        }
        return root;
    }

    public boolean isEmpty(){
        return heap.isEmpty();
    }
}