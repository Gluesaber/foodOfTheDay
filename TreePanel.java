import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.*;


public class TreePanel extends JPanel {
    private Node root;
    private final int NODE_RADIUS = 40; // Adjust as needed
    private final int FONT_SIZE = 20;
    private HashMap<Node, Point> nodePositions;
    private MainAVL mainWindow;

    private Node animatingNode = null;
    private Point animationStart;
    private Point animationEnd;
    private Timer animationTimer;
    private float animationProgress = 0f;
    private LinkedList<Node> pathToNode;
    private int currentStep = 0;

    private Image menuImage;

    public TreePanel(Node root, MainAVL mainWindow) {
        this.root = root;
        this.mainWindow = mainWindow;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(800, 600)); // Make sure the panel has a visible size

        // Load the image from resources
        try {
            menuImage = new ImageIcon("menu2.png").getImage();
            System.out.println("Image loaded successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
        }

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

        root = mainWindow.tree.root;
        calculateNodePositions();
        pathToNode = getPathToNode(root, item);

        if (!pathToNode.isEmpty()) {
            animationEnd = nodePositions.get(pathToNode.get(0));
            startAnimationStep();
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
        mainWindow.tree.insert(animatingNode.data);
        root = mainWindow.tree.root;
        mainWindow.treePanel.setRoot(root);
        mainWindow.updateMenuDisplay();
        animatingNode = null;
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
            g.setColor(Color.GREEN);
            g.drawLine(p.x, p.y, rightP.x, rightP.y);
            drawTree(g, node.right);
        }

        drawNode(g, node, p.x, p.y);
    }

    private void drawNode(Graphics g, Node node, int x, int y) {
        if (menuImage != null) {
            int size = NODE_RADIUS * 2;
            BufferedImage scaledImage = getScaledImage(menuImage, size, size);
        g.drawImage(scaledImage, x - NODE_RADIUS, y - NODE_RADIUS, null);

        } else {
            g.setColor(Color.GRAY);
            g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        }

        String text = node.data.toString();
        g.setColor(Color.RED);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
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

            JMenuItem editItem = new JMenuItem("Edit Node");
            editItem.addActionListener(e -> openEditMenu(clickedNode));
            popupMenu.add(editItem);
        }

        popupMenu.show(this, x, y);
    }

    private void openEditMenu(Node node) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Food Item", true);
        dialog.setSize(300, 400);
        dialog.setLayout(new GridLayout(9, 2, 5, 5));
        dialog.setLocationRelativeTo(this);
    
        FoodItem item = node.data;
    
        JTextField nameField = new JTextField(item.name);
        JTextField priceField = new JTextField(String.valueOf(item.price));
        JTextField cuisineField = new JTextField(item.cuisine);
        JCheckBox spicyBox = new JCheckBox("Spicy", item.isSpicy);
        JCheckBox halalBox = new JCheckBox("Halal", item.isHalal);
        JCheckBox healthyBox = new JCheckBox("Healthy", item.isHealthy);
        JCheckBox seafoodBox = new JCheckBox("Seafood", item.isSeafood);
        JCheckBox veganBox = new JCheckBox("Vegan", item.isVegan);
    
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Cuisine:"));
        dialog.add(cuisineField);
        dialog.add(spicyBox);
        dialog.add(halalBox);
        dialog.add(healthyBox);
        dialog.add(seafoodBox);
        dialog.add(veganBox);
    
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                // Get updated values from input
                String name = nameField.getText();
                int price = Integer.parseInt(priceField.getText());
                String cuisine = cuisineField.getText();
                boolean isSpicy = spicyBox.isSelected();
                boolean isHalal = halalBox.isSelected();
                boolean isHealthy = healthyBox.isSelected();
                boolean isSeafood = seafoodBox.isSelected();
                boolean isVegan = veganBox.isSelected();
        
                // Create a new FoodItem with updated data
                FoodItem updatedItem = new FoodItem(name, price, cuisine, isSpicy, isHalal, isHealthy, isSeafood, isVegan);
        
                // Replace the old item in the AVL tree
                mainWindow.tree.delete(node.data);   // Remove old node
                mainWindow.tree.insert(updatedItem); // Insert updated node
        
                // Update tree display
                root = mainWindow.tree.root;
                mainWindow.treePanel.setRoot(root);
                mainWindow.updateMenuDisplay(); // If you have a menu list or panel that shows food items
                repaint();                      // Redraw tree
                dialog.dispose();              // Close dialog
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
    
        dialog.add(saveButton);
        dialog.setVisible(true);
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

    private BufferedImage getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}
