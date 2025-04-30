import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainAVL extends JFrame {
    private AVLTree tree;
    private JTextArea outputArea;
    private TreePanel treePanel;

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

        treePanel = new TreePanel(tree.root);
        add(treePanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(250, getHeight()));
        add(scrollPane, BorderLayout.EAST);

        JButton addButton = new JButton("Add Food Item");
        add(addButton, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddMenu(MainAVL.this);
            }
        });

        updateMenuDisplay();
    }

    public void addFoodItem(FoodItem item) {
        tree.insert(item);
        treePanel.setRoot(tree.root);
        updateMenuDisplay();
    }

    private void updateMenuDisplay() {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainAVL app = new MainAVL();
            app.setVisible(true);
        });
    }
}
