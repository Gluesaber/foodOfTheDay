import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class AVLTreeGUI extends JFrame{
    private AVLTree tree;
    private JTextArea outputArea;
    private TreePanel treePanel;
    private final int PANEL_WIDTH = 1538;
    private final int PANEL_HEIGHT = 872;

    public AVLTreeGUI(AVLTree tree){
        this.tree = tree;

        setTitle("Let's adjust menu!");
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Image icon = new ImageIcon("img/CroissantTae.png").getImage();
        setIconImage(icon);

        treePanel = new TreePanel(tree.root, this);
        add(treePanel, BorderLayout.CENTER);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(200, getHeight()));
        add(scrollPane, BorderLayout.EAST);

        updateMenuDisplay();
    }

    public void updateMenuDisplay(){
        outputArea.setText("");
        inOrderList(tree.root);
        treePanel.setRoot(tree.root);
        treePanel.repaint();
    }

    private void inOrderList(TNode node){
        if(node == null) return;
        inOrderList(node.left);
        outputArea.append(node.food.name + " (" + node.food.price + ")\n");
        inOrderList(node.right);
    }

    public void insertFoodItem(FoodItem item){
        tree.insert(item);
        updateMenuDisplay();
    }

    public void removeFoodItem(FoodItem item){
        tree.remove(item);
        updateMenuDisplay();
    }

    class TreePanel extends JPanel{
        private TNode root;
        private final int NODE_RADIUS = 60;
        private Map<TNode, Point> nodePositions;
        private Image menuImage, menuImageSelected;
        private TNode selectedNode;
        private AVLTreeGUI parent;

        public TreePanel(TNode root, AVLTreeGUI parent){
            this.root = root;
            this.parent = parent;
            setBackground(new Color(0xE8FEFF));
            setPreferredSize(new Dimension(800, 600));
            setFocusable(true);

            menuImage = new ImageIcon("img/TNode.png").getImage();
            menuImageSelected = new ImageIcon("img/TNodeSelected.png").getImage();

            addMouseListener(new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e){
                    if(SwingUtilities.isRightMouseButton(e)){
                        TNode node = getNodeAt(e.getX(), e.getY());
                        showContextMenu(e.getX(), e.getY(), node);
                    } else if(SwingUtilities.isLeftMouseButton(e)){
                        selectedNode = getNodeAt(e.getX(), e.getY());
                        repaint();
                    }
                }
            });

            addKeyListener(new KeyAdapter(){
                @Override
                public void keyPressed(KeyEvent e){
                    if(e.getKeyCode() == KeyEvent.VK_DELETE && selectedNode != null){
                        parent.removeFoodItem(selectedNode.food);
                        selectedNode = null;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(root == null) return;
            nodePositions = new HashMap<>();
            int depth = getTreeDepth(root);
            int ySpacing = getHeight() /(depth + 1);
            computeNodePositions(root, 0, getWidth() /(countNodes(root) + 1), ySpacing, new int[]{1});
            drawEdges(g, root);
            drawNodes(g, root);
        }

        public void setRoot(TNode root){
            this.root = root;
        }

        private void computeNodePositions(TNode node, int depth, int xSpacing, int ySpacing, int[] currentX){
            if(node == null) return;
            computeNodePositions(node.left, depth + 1, xSpacing, ySpacing, currentX);
            int x = currentX[0] * xSpacing;
            int y = (depth + 1) * ySpacing;
            nodePositions.put(node, new Point(x, y));
            currentX[0]++;
            computeNodePositions(node.right, depth + 1, xSpacing, ySpacing, currentX);
        }

        private void drawEdges(Graphics g, TNode node){
            if(node == null) return;
            Point p = nodePositions.get(node);
            if(node.left != null) drawLine(g, p, nodePositions.get(node.left));
            if(node.right != null) drawLine(g, p, nodePositions.get(node.right));
            drawEdges(g, node.left);
            drawEdges(g, node.right);
        }

        private void drawLine(Graphics g, Point a, Point b){
            g.setColor(Color.BLACK);
            g.drawLine(a.x, a.y, b.x, b.y);
        }

        private void drawNodes(Graphics g, TNode node){
            if(node == null) return;
            Point p = nodePositions.get(node);
            Image icon = (node == selectedNode) ? menuImageSelected : menuImage;
            if(icon != null){
                BufferedImage img = getScaledImage(icon, NODE_RADIUS * 2, NODE_RADIUS * 2);
                g.drawImage(img, p.x - NODE_RADIUS, p.y - NODE_RADIUS, null);
            } else{
                g.setColor(Color.GRAY);
                g.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
            FontMetrics fm = g.getFontMetrics();
            String name = node.food.name;
            int textW = fm.stringWidth(name);
            g.drawString(name, p.x - textW / 2, p.y + fm.getAscent() / 4);
            g.drawString(String.valueOf(node.food.price), p.x + 24, p.y + 46);
            drawNodes(g, node.left);
            drawNodes(g, node.right);
        }

        private TNode getNodeAt(int x, int y){
            if(nodePositions == null) return null;
            for(TNode n : nodePositions.keySet()){
                Point p = nodePositions.get(n);
                int dx = x - p.x, dy = y - p.y;
                if(dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) return n;
            }
            return null;
        }

        private int getTreeDepth(TNode n){
            return(n == null) ? 0 : 1 + Math.max(getTreeDepth(n.left), getTreeDepth(n.right));
        }

        private int countNodes(TNode n){
            return(n == null) ? 0 : 1 + countNodes(n.left) + countNodes(n.right);
        }

        private BufferedImage getScaledImage(Image src, int w, int h){
            BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(src, 0, 0, w, h, null);
            g2.dispose();
            return dst;
        }

        private void showContextMenu(int x, int y, TNode node){
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(new Color(0xD3FFF6));
            menu.setBorder(BorderFactory.createEmptyBorder());
            if(node == null){
                JMenuItem add = new JMenuItem("Add food");
                add.addActionListener(e -> openEditMenu(new FoodItem("", 0, "", false, false, false, false)));
                menu.add(add);
            }
            menu.show(this, x, y);
        }

        private void openEditMenu(FoodItem item){
            JFrame frame = new JFrame();
            frame.setSize(1280, 664);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true);

            Image background = new ImageIcon("img/AddFood.png").getImage();
            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setPreferredSize(new Dimension(1280, 664));

            JPanel bg = new JPanel(){
                protected void paintComponent(Graphics g){
                    super.paintComponent(g);
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            };
            bg.setBounds(0, 0, 1280, 664);
            layeredPane.add(bg, Integer.valueOf(0));

            // Input components
            JTextField nameField = new JTextField();
            nameField.setBounds(85, 116, 255, 50);
            hideInput(nameField);
            layeredPane.add(nameField, Integer.valueOf(1));

            JTextField priceField = new JTextField();
            priceField.setBounds(85, 300, 200, 50);
            hideInput(priceField);
            layeredPane.add(priceField, Integer.valueOf(1));

            JCheckBox halalBox = new JCheckBox();
            halalBox.setBounds(565, 117, 40, 40);
            hideCheckBox(halalBox);
            layeredPane.add(halalBox, Integer.valueOf(1));

            JCheckBox healthyBox = new JCheckBox();
            healthyBox.setBounds(923, 117, 40, 40);
            hideCheckBox(healthyBox);
            layeredPane.add(healthyBox, Integer.valueOf(1));

            JCheckBox seafoodBox = new JCheckBox();
            seafoodBox.setBounds(565, 195, 40, 40);
            hideCheckBox(seafoodBox);
            layeredPane.add(seafoodBox, Integer.valueOf(1));

            JCheckBox veganBox = new JCheckBox();
            veganBox.setBounds(923, 195, 40, 40);
            hideCheckBox(veganBox);
            layeredPane.add(veganBox, Integer.valueOf(1));

            // Cuisine radio buttons
            JRadioButton chinese = new JRadioButton();
            chinese.setBounds(563, 372, 40, 40);
            hideRadi(chinese);
            layeredPane.add(chinese, Integer.valueOf(1));

            JRadioButton indian = new JRadioButton();
            indian.setBounds(792, 372, 40, 40);
            hideRadi(indian);
            layeredPane.add(indian, Integer.valueOf(1));

            JRadioButton japanese = new JRadioButton();
            japanese.setBounds(1021, 372, 40, 40);
            hideRadi(japanese);
            layeredPane.add(japanese, Integer.valueOf(1));

            JRadioButton thai = new JRadioButton();
            thai.setBounds(563, 450, 40, 40);
            hideRadi(thai);
            thai.setSelected(true);
            layeredPane.add(thai, Integer.valueOf(1));

            JRadioButton western = new JRadioButton();
            western.setBounds(792, 450, 40, 40);
            hideRadi(western);
            layeredPane.add(western, Integer.valueOf(1));

            ButtonGroup cuisineGroup = new ButtonGroup();
            cuisineGroup.add(chinese);
            cuisineGroup.add(indian);
            cuisineGroup.add(japanese);
            cuisineGroup.add(thai);
            cuisineGroup.add(western);

            JButton closeButton = new JButton();
            closeButton.setBounds(1180, 36, 60, 60);
            closeButton.setContentAreaFilled(false);
            closeButton.setBorderPainted(false);
            layeredPane.add(closeButton, Integer.valueOf(1));
            closeButton.addActionListener(e -> frame.dispose());

            JButton saveButton = new JButton();
            saveButton.setBounds(568, 545, 290, 75);
            saveButton.setContentAreaFilled(false);
            saveButton.setBorderPainted(false);
            layeredPane.add(saveButton, Integer.valueOf(1));
            saveButton.addActionListener(e ->{
                try{
                    String name = nameField.getText();
                    int price = Integer.parseInt(priceField.getText());
                    if(halalBox.isSelected()) item.isHalal = true;
                    if(healthyBox.isSelected()) item.isHealthy = true;
                    if(!seafoodBox.isSelected()) item.isSeafood = true;
                    if(veganBox.isSelected()) item.isVegan = true;

                    if(chinese.isSelected()) item.cuisine = "Chinese";
                    else if(indian.isSelected()) item.cuisine = "Indian";
                    else if(japanese.isSelected()) item.cuisine = "Japanese";
                    else if(thai.isSelected()) item.cuisine = "Thai";
                    else if(western.isSelected()) item.cuisine = "Western";

                    item.name = name;
                    item.price = price;
                    parent.insertFoodItem(item);
                    setRoot(root);
                    frame.dispose();
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(frame, "Invalid input");
                    ex.printStackTrace();
                }
            });

            frame.setContentPane(layeredPane);
            frame.setVisible(true);
        }

        private static void hideInput(JTextField box){
            box.setBorder(null);
            box.setOpaque(false);
            box.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        }

        private static void hideCheckBox(JCheckBox box){
            box.setIcon(new ImageIcon("img/Empty.png"));
            box.setSelectedIcon(new ImageIcon("img/Check.png"));
            box.setOpaque(false);
        }

        private static void hideRadi(JRadioButton box){
            box.setIcon(new ImageIcon("img/Empty.png"));
            box.setSelectedIcon(new ImageIcon("img/Circle.png"));
            box.setOpaque(false);
        }
    }
}