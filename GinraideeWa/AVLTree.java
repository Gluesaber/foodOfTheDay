import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AVLTree{
    TNode root;

    private TNode rightRotate(TNode oldRoot){
        TNode newRoot = oldRoot.left;
        TNode T2 = newRoot.right;
        newRoot.right = oldRoot;
        oldRoot.left = T2;
        oldRoot.height = max(height(oldRoot.left), height(oldRoot.right)) + 1;
        newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1;
        return newRoot;
    }

    private TNode leftRotate(TNode oldRoot){
        TNode newRoot = oldRoot.right;
        TNode T2 = newRoot.left;
        newRoot.left = oldRoot;
        oldRoot.right = T2;
        oldRoot.height = max(height(oldRoot.left), height(oldRoot.right)) + 1;
        newRoot.height = max(height(newRoot.left), height(newRoot.right)) + 1;
        return newRoot;
    }

    private int height(TNode node){
        if(node == null){
            return 0;
        }
        return node.height;
    }

    private int max(int a, int b){
        return Math.max(a, b);
    }

    private int balanceFactor(TNode node){
        if(node == null){
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    void insert(FoodItem item){
        root = insert(root, item);
    }

    private TNode insert(TNode node, FoodItem item){
        if(node == null){
            return new TNode(item);
        }
        
        if(item.price < node.food.price){
            node.left = insert(node.left, item);
        }else if(item.price > node.food.price){
            node.right = insert(node.right, item);
        }else{
            if(item.name.compareTo(node.food.name) < 0){
                node.left = insert(node.left, item);
            }else{
                node.right = insert(node.right, item);
            }
        }

        node.height = 1 + max(height(node.left), height(node.right));
        int balance = balanceFactor(node);
        if(balance > 1 && item.price < node.left.food.price) return rightRotate(node);
        if(balance < -1 && item.price > node.right.food.price) return leftRotate(node);
        if(balance > 1 && item.price > node.left.food.price){
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if(balance < -1 && item.price < node.right.food.price){
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void remove(FoodItem item){
        root = remove(root, item);
    }
    
    private TNode remove(TNode node, FoodItem item){
        if(node == null) return null;
    
        if(item.price < node.food.price){
            node.left = remove(node.left, item);
        }else if(item.price > node.food.price){
            node.right = remove(node.right, item);
        }else{
            // Price matches, compare by name to ensure correct item
            if(item.name.equals(node.food.name)){
                // Node with only one child or no child
                if(node.left == null || node.right == null){
                    TNode temp = (node.left != null) ? node.left : node.right;
                    if(temp == null){
                        return null; // No child
                    }else{
                        return temp; // One child
                    }
                }
    
                // Node with two children: get the inorder successor (smallest in the right subtree)
                TNode temp = minValueNode(node.right);
                node.food = temp.food;
                node.right = remove(node.right, temp.food);
            }else{
                // Price same, but name doesn't match — continue to both subtrees
                node.left = remove(node.left, item);
                node.right = remove(node.right, item);
            }
        }
    
        // Update height
        node.height = 1 + max(height(node.left), height(node.right));
    
        // Rebalance
        int balance = balanceFactor(node);
    
        // Left Left
        if(balance > 1 && balanceFactor(node.left) >= 0){
            return rightRotate(node);
        }
    
        // Left Right
        if(balance > 1 && balanceFactor(node.left) < 0){
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
    
        // Right Right
        if(balance < -1 && balanceFactor(node.right) <= 0){
            return leftRotate(node);
        }
    
        // Right Left
        if(balance < -1 && balanceFactor(node.right) > 0){
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
    
        return node;
    }
    
    private TNode minValueNode(TNode node){
        TNode current = node;
        while(current.left != null){
            current = current.left;
        }
        return current;
    }

    public FoodItem getFoodItemByNameAndPrice(String name, int price){
        return searchByNameAndPrice(root, name, price);
    }
    
    private FoodItem searchByNameAndPrice(TNode node, String name, int price){
        if(node == null) return null;
    
        if(node.food.name.equals(name) && node.food.price == price){
            return node.food;
        }
    
        FoodItem leftResult = searchByNameAndPrice(node.left, name, price);
        if(leftResult != null) return leftResult;
    
        return searchByNameAndPrice(node.right, name, price);
    }    

    public FoodItem getFood(Vertex restaurant, int min, int max, FoodItem required){
        List<FoodItem> itemsInRange = new ArrayList<>();
        Random r = new Random();
        searchItemsInRange(restaurant.menu.root, min, max, itemsInRange, required);

        if(itemsInRange.isEmpty()){
            return null;
        }
        return itemsInRange.get(r.nextInt(itemsInRange.size()));
    }

    private void searchItemsInRange(TNode node, int min, int max, List<FoodItem> itemsInRange, FoodItem required){
        if(node == null) return;

        if(node.food.price > min){
            searchItemsInRange(node.left, min, max, itemsInRange, required);
        }
        if(node.food.price >= min && node.food.price <= max){
            if(matchFood(node.food, required)){
                itemsInRange.add(node.food);
            }
        }
        if(node.food.price < max){
            searchItemsInRange(node.right, min, max, itemsInRange, required);
        }
    }

    private boolean matchFood(FoodItem f1, FoodItem f2){
        if(!f2.cuisine.equals("0") && !(f1.cuisine.equals(f2.cuisine))) return false;
        if((f2.isHalal == true) && (f1.isHalal != f2.isHalal)) return false;
        if((f2.isHealthy == true) && (f1.isHealthy != f2.isHealthy)) return false;
        if((f2.isSeafood == false) && (f1.isSeafood != f2.isSeafood)) return false;
        if((f2.isVegan == true) && (f1.isVegan != f2.isVegan)) return false;

        return true;
    }
}