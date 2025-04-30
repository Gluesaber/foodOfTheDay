import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

public class TreePanel extends JPanel {
    private Node root;
    private final int NODE_RADIUS = 20;
    private final int FONT_SIZE = 10;
    private HashMap<Node, Point> nodePositions;

    public TreePanel(Node root) {
        this.root = root;
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (root != null) {
            g.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
            nodePositions = new HashMap<>();
            int depth = getTreeDepth(root);
            int ySpacing = getHeight() / (depth + 1);
            int[] currentX = {1}; // Mutable reference for in-order x position
            computeNodePositions(root, 0, getWidth() / (countNodes(root) + 1), ySpacing, currentX);
            drawTree(g, root);
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
}
