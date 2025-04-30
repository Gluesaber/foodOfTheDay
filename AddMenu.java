import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddMenu extends JFrame {
    private JTextField nameField, priceField, cuisineField;
    private JCheckBox spicyBox, halalBox, healthyBox, seafoodBox, veganBox;
    private MainAVL mainWindow;  // Change to MainAVL

    // Update constructor to expect MainAVL, not MainWindow
    public AddMenu(MainAVL mainWindow) {
        this.mainWindow = mainWindow;

        setTitle("Add New Food Item");
        setSize(300, 400);
        setLayout(new GridLayout(9, 2, 5, 5));
        setLocationRelativeTo(mainWindow); // Center on Main Window

        nameField = new JTextField();
        priceField = new JTextField();
        cuisineField = new JTextField();
        spicyBox = new JCheckBox("Spicy");
        halalBox = new JCheckBox("Halal");
        healthyBox = new JCheckBox("Healthy");
        seafoodBox = new JCheckBox("Seafood");
        veganBox = new JCheckBox("Vegan");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Price:"));
        add(priceField);
        add(new JLabel("Cuisine:"));
        add(cuisineField);
        add(spicyBox);
        add(halalBox);
        add(healthyBox);
        add(seafoodBox);
        add(veganBox);

        JButton submitButton = new JButton("Add Item");
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitItem();
            }
        });

        setVisible(true);
    }

    private void submitItem() {
        try {
            String name = nameField.getText();
            int price = Integer.parseInt(priceField.getText());
            String cuisine = cuisineField.getText();
            boolean isSpicy = spicyBox.isSelected();
            boolean isHalal = halalBox.isSelected();
            boolean isHealthy = healthyBox.isSelected();
            boolean isSeafood = seafoodBox.isSelected();
            boolean isVegan = veganBox.isSelected();

            FoodItem item = new FoodItem(name, price, cuisine, isSpicy, isHalal, isHealthy, isSeafood, isVegan);
            mainWindow.addFoodItem(item);

            dispose();  // Close the popup
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be an integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
