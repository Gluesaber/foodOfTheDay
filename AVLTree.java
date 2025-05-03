import java.util.ArrayList;
import java.util.List;

class Node {
    int height;
    FoodItem data;
    Node left;
    Node right;

    // Position for drawing
    int x;
    int y;

    Node(FoodItem value){
        this.data = value;
        this.height = 1;
    }
}

public class AVLTree {
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
        return (node == null) ? 0 : node.height;
    }

    int max(int a, int b){
        return Math.max(a, b);
    }

    int balanceFactor(Node node){
        return (node == null) ? 0 : height(node.left) - height(node.right);
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

        node.height = 1 + max(height(node.left), height(node.right));
        return balance(node);
    }

    void insert(FoodItem item) {
        root = insert(root, item);
    }

    Node delete(Node node, FoodItem item) {
        if (node == null) return null;

        if (item.price < node.data.price) {
            node.left = delete(node.left, item);
        } else if (item.price > node.data.price) {
            node.right = delete(node.right, item);
        } else {
            if (!item.name.equals(node.data.name)) {
                if (item.name.compareTo(node.data.name) < 0) {
                    node.left = delete(node.left, item);
                } else {
                    node.right = delete(node.right, item);
                }
            } else {
                if (node.left == null || node.right == null) {
                    node = (node.left != null) ? node.left : node.right;
                } else {
                    Node successor = getMinValueNode(node.right);
                    node.data = successor.data;
                    node.right = delete(node.right, successor.data);
                }
            }
        }

        if (node == null) return null;

        node.height = 1 + max(height(node.left), height(node.right));
        return balance(node);
    }

    void delete(FoodItem item) {
        root = delete(root, item);
    }

    private Node getMinValueNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    Node balance(Node node) {
        int balance = balanceFactor(node);

        if (balance > 1 && balanceFactor(node.left) >= 0)
            return rightRotate(node);

        if (balance > 1 && balanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && balanceFactor(node.right) <= 0)
            return leftRotate(node);

        if (balance < -1 && balanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
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

        if (min < node.data.price) {
            getItemsInRangeRecursive(node.left, min, max, itemsInRange);
        }

        if (node.data.price >= min && node.data.price <= max) {
            itemsInRange.add(node.data);
        }

        if (max > node.data.price) {
            getItemsInRangeRecursive(node.right, min, max, itemsInRange);
        }
    }

    private int currentX = 0;

    public void calculateNodePositions(int panelWidth, int panelHeight) {
        int depth = getTreeDepth(root);
        int ySpacing = panelHeight / (depth + 1);
        currentX = 0;
        assignPositions(root, 0, ySpacing);
    }

    private void assignPositions(Node node, int depth, int ySpacing) {
        if (node == null) return;

        assignPositions(node.left, depth + 1, ySpacing);

        node.x = currentX++;
        node.y = depth;

        assignPositions(node.right, depth + 1, ySpacing);
    }

    private int getTreeDepth(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }
} 
