import java.awt.*;
import javax.swing.*;

public class MainAVL extends JFrame {
    public AVLTree tree;
    private JTextArea outputArea;
    public TreePanel treePanel;

    public MainAVL() {
        tree = new AVLTree();

        // Insert pre-existing nodes
        tree.insert(new FoodItem("Pad Thai", 45, "Thai", false, true, true, false, false));
        tree.insert(new FoodItem("Spaghetti", 80, "Western", false, false, false, false, false));
        tree.insert(new FoodItem("Green Curry", 60, "Thai", true, true, true, false, false));
        tree.insert(new FoodItem("Tofu Salad", 45, "IDK", false, true, true, false, true));

        setTitle("AVL Tree Food Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        treePanel = new TreePanel(tree.root, this);
        add(treePanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(250, getHeight()));
        add(scrollPane, BorderLayout.EAST);

        updateMenuDisplay();
    }

    public void updateMenuDisplay() {
        outputArea.setText("");
        captureInOrder(tree.root);
        repaint();
    }

    private void captureInOrder(Node node) {
        if (node != null) {
            captureInOrder(node.left);
            outputArea.append(node.data.toString() + "\n");
            captureInOrder(node.right);
        }
    }

    public void deleteFoodItem(FoodItem item) {
        tree.delete(item);  // Ensure AVL tree delete logic is implemented
        treePanel.setRoot(tree.root);  // Refresh panel
        updateMenuDisplay();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainAVL app = new MainAVL();
            app.setVisible(true);
        });
    }
}
