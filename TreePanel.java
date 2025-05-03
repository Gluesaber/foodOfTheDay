import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.*;

public class TreePanel extends JPanel {
    private Node root;
    private final int NODE_RADIUS = 20;
    private final int FONT_SIZE = 10;
    private HashMap<Node, Point> nodePositions;
    private MainAVL mainWindow;

    // Animation
    private Node animatingNode = null;
    private Point animationStart;
    private Point animationEnd;
    private Timer animationTimer;
    private float animationProgress = 0f;
    private LinkedList<Node> pathToNode;
    private int currentStep = 0;

    public TreePanel(Node root, MainAVL mainWindow) {
        this.root = root;
        this.mainWindow = mainWindow;
        setBackground(Color.BLACK);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Node clickedNode = getNodeAt(e.getX(), e.getY());
                    showContextMenu(e.getX(), e.getY(), clickedNode);
                }
            }
        });
    }

    public void startInsertAnimation(FoodItem item, int clickX, int clickY) {
        animatingNode = new Node(item);
        animationStart = new Point(clickX, clickY);
        animationProgress = 0f;
        currentStep = 0;
    
        // Only calculate path here, but don't insert yet
        root = mainWindow.tree.root;
        calculateNodePositions();
    
        pathToNode = getPathToNode(root, item); // Get path from root to new node
    
        if (!pathToNode.isEmpty()) {
            animationEnd = nodePositions.get(pathToNode.get(0));
            startAnimationStep(); // Begin animation
        }
    }

    private LinkedList<Node> getPathToNode(Node node, FoodItem target) {
        LinkedList<Node> path = new LinkedList<>();
        while (node != null) {
            path.add(node);
            if (node.data.equals(target)) break;
            node = (target.compareTo(node.data) < 0) ? node.left : node.right;
        }
        return path;
    }

    private void startAnimationStep() {
        animationTimer = new Timer(16, e -> {
            if (animatingNode == null || pathToNode == null || currentStep >= pathToNode.size()) {
                animationTimer.stop();
                insertNodeAfterAnimation();
                return;
            }

            Point currentTarget = nodePositions.get(pathToNode.get(currentStep));
            if (currentTarget == null) return;

            // Interpolate to next node
            animationProgress += 0.03f;
            if (animationProgress >= 1f) {
                animationProgress = 0f;
                animationStart = currentTarget;
                currentStep++;
                if (currentStep < pathToNode.size()) {
                    animationEnd = nodePositions.get(pathToNode.get(currentStep));
                } else {
                    animationEnd = currentTarget;
                }
            }

            repaint();
        });

        animationTimer.start();
    }

    private void insertNodeAfterAnimation() {
        // Insert the node after animation is complete
        mainWindow.tree.insert(animatingNode.data);
        root = mainWindow.tree.root; // Update the root after insertion
        mainWindow.treePanel.setRoot(root); // Refresh the panel
        mainWindow.updateMenuDisplay(); // Update the menu display with the new tree structure
        animatingNode = null; // Reset the animating node
    }

    private void openAddMenu(int x, int y) {
        AddMenu addMenu = new AddMenu(mainWindow, item -> {
            startInsertAnimation(item, x + getLocationOnScreen().x, y + getLocationOnScreen().y);
        });
        addMenu.setLocation(x + getLocationOnScreen().x, y + getLocationOnScreen().y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            g.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
            nodePositions = new HashMap<>();
            int depth = getTreeDepth(root);
            int ySpacing = getHeight() / (depth + 1);
            int[] currentX = {1};
            computeNodePositions(root, 0, getWidth() / (countNodes(root) + 1), ySpacing, currentX);
            drawTree(g, root);
        }

        if (animatingNode != null && animationStart != null && animationEnd != null) {
            int x = (int) (animationStart.x * (1 - animationProgress) + animationEnd.x * animationProgress);
            int y = (int) (animationStart.y * (1 - animationProgress) + animationEnd.y * animationProgress);
            drawNode(g, animatingNode, x, y);
        }
    }

    private void computeNodePositions(Node node, int depth, int xSpacing, int ySpacing, int[] currentX) {
        if (node == null) return;

        computeNodePositions(node.left, depth + 1, xSpacing, ySpacing, currentX);

        int x = currentX[0] * xSpacing;
        int y = (depth + 1) * ySpacing;
        nodePositions.put(node, new Point(x, y));
        currentX[0]++;

        computeNodePositions(node.right, depth + 1, xSpacing, ySpacing, currentX);
    }

    private void drawTree(Graphics g, Node node) {
        if (node == null) return;

        Point p = nodePositions.get(node);

        if (node.left != null) {
            Point leftP = nodePositions.get(node.left);
            g.setColor(Color.RED);
            g.drawLine(p.x, p.y, leftP.x, leftP.y);
            drawTree(g, node.left);
        }

        if (node.right != null) {
            Point rightP = nodePositions.get(node.right);
            g.setColor(Color.BLUE);
            g.drawLine(p.x, p.y, rightP.x, rightP.y);
            drawTree(g, node.right);
        }

        drawNode(g, node, p.x, p.y);
    }

    private void drawNode(Graphics g, Node node, int x, int y) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        String text = node.data.toString();
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        g.setColor(Color.RED);
        g.drawString(text, x - textWidth / 2, y + textHeight / 4);
    }

    public void setRoot(Node newRoot) {
        this.root = newRoot;
        repaint();
    }

    private int getTreeDepth(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getTreeDepth(node.left), getTreeDepth(node.right));
    }

    private int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    private void showContextMenu(int x, int y, Node clickedNode) {
        JPopupMenu popupMenu = new JPopupMenu();

        if (clickedNode == null) {
            JMenuItem addItem = new JMenuItem("Add Node");
            addItem.addActionListener(e -> openAddMenu(x, y));
            popupMenu.add(addItem);
        } else {
            JMenuItem deleteItem = new JMenuItem("Delete Node");
            deleteItem.addActionListener(e -> mainWindow.deleteFoodItem(clickedNode.data));
            popupMenu.add(deleteItem);
        }

        popupMenu.show(this, x, y);
    }

    private Node getNodeAt(int x, int y) {
        if (nodePositions == null) return null;
        for (Node node : nodePositions.keySet()) {
            Point p = nodePositions.get(node);
            int dx = x - p.x;
            int dy = y - p.y;
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                return node;
            }
        }
        return null;
    }

    public void calculateNodePositions() {
        if (root == null) return;
        int depth = getTreeDepth(root);
        int ySpacing = getHeight() / (depth + 1);
        int[] currentX = {1};
        nodePositions = new HashMap<>();
        computeNodePositions(root, 0, getWidth() / (countNodes(root) + 1), ySpacing, currentX);
    }
}
