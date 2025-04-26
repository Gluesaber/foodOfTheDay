import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Node{
    int height;
    FoodItem data;
    Node left;
    Node right;
    Node(FoodItem value){
        this.data = value;
        this.height = 1;
    }
}

public class AVLTree{
    Node root;

    Node rightRotate(Node oldRoot){
        Node newRoot = oldRoot.left;

        Node T2 = newRoot.right;

        newRoot.right = oldRoot;
        oldRoot.left = T2;

        oldRoot.height = max(height(oldRoot.left), height(oldRoot.right)) + 1;
        newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1;

        return newRoot;
    }

    Node leftRotate(Node oldRoot){
        Node newRoot = oldRoot.right;

        Node T2 = newRoot.left;

        newRoot.left = oldRoot;
        oldRoot.right = T2;

        oldRoot.height = max(height(oldRoot.left), height(oldRoot.right)) + 1;
        newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1;

        return newRoot;
    }

    int height(Node node){
        if(node == null){
            return 0;
        }
        return  node.height;
    }

    int max(int a, int b){
        return Math.max(a, b);
    }

    int balanceFactor(Node node){
        if(node == null){
            return 0;
        }
        return  height(node.left) - height(node.right);
    }

    Node insert(Node node, FoodItem item){
        if(node == null){
            return new Node(item);
        }

        if (item.price < node.data.price) {
            node.left = insert(node.left, item);
        } else if (item.price > node.data.price) {
            node.right = insert(node.right, item);
        } else {
            if (item.name.compareTo(node.data.name) < 0) {
                node.left = insert(node.left, item);
            } else {
                node.right = insert(node.right, item);
            }
        }

        node.height = 1+max(height(node.left), height(node.right));

        int balance = balanceFactor(node);

        // Balancing cases
        if (balance > 1 && item.price < node.left.data.price)
            return rightRotate(node);

        if (balance < -1 && item.price > node.right.data.price)
            return leftRotate(node);

        if (balance > 1 && item.price > node.left.data.price) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && item.price < node.right.data.price) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return  node;
    }
    
     void insert(FoodItem item) {
        root = insert(root, item);
    }

    void inOrder(Node node) {
        if (node != null) {
            inOrder(node.left);
            System.out.println(node.data);
            inOrder(node.right);
        }
    }

    public void displayInOrder() {
        inOrder(root);
    }

    public List<FoodItem> getItemsInRange(Node node, int min, int max) {
        List<FoodItem> itemsInRange = new ArrayList<>();
        getItemsInRangeRecursive(node, min, max, itemsInRange);
        return itemsInRange;
    }

    private void getItemsInRangeRecursive(Node node, int min, int max, List<FoodItem> itemsInRange) {
        if (node == null) return;

        // Search the left subtree if the current node's price is greater than min
        if (min < node.data.price) {
            getItemsInRangeRecursive(node.left, min, max, itemsInRange);
        }

        // Check if the current node's price is within the range
        if (node.data.price >= min && node.data.price <= max) {
            itemsInRange.add(node.data);
        }

        // Search the right subtree if the current node's price is less than max
        if (max > node.data.price) {
            getItemsInRangeRecursive(node.right, min, max, itemsInRange);
        }
    }


    public static void main(String[] args) {
        AVLTree tree = new AVLTree();

        tree.insert(new FoodItem("Pad Thai", 45, "Thai", false, true, true, false, false));
        tree.insert(new FoodItem("Spaghetti", 80, "Western", false, false, false, false, false));
        tree.insert(new FoodItem("Green Curry", 60, "Thai", true, true, true, false, false));
        tree.insert(new FoodItem("Tofu Salad", 45, "IDK", false, true, true, false, true));

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter min price: ");
        int min = sc.nextInt();
        System.out.print("Enter max price: ");
        int max = sc.nextInt();

        System.out.println("\nMenu (sorted by price, then name):");
        tree.displayInOrder();

        List<FoodItem> itemsInRange = tree.getItemsInRange(tree.root, min, max);
        System.out.println("\nMenu within price range (" + min + " - " + max + "):");
        for (FoodItem item : itemsInRange) {
            System.out.println(item);  // This will call your overridden toString()
        }

        sc.close();
    }
    
}