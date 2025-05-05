import java.awt.*;
import javax.swing.*;

public class MainGUI extends JFrame{
    private Main mainInstance;
    private Graph graph;

    private Image background;
    private Image icon;
    private JPanel bgPanel;

    private JTextField keyInput;
    private JButton userBtn, modBtn;

    void moderator(Graph graph){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                GraphGUI visualizer = new GraphGUI(graph);
                JScrollPane scrollPane = new JScrollPane(visualizer);
                scrollPane.setPreferredSize(new Dimension(1524, 2709));

                JFrame frame = new JFrame("I'm a Mod, I AM the GOD!");
                frame.add(scrollPane);
                frame.pack();
                frame.setIconImage(icon);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public MainGUI(){
        setTitle("GINRAIDEEwa");
        setSize(1524, 824); // Image size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainInstance = new Main();
        graph = mainInstance.formGraph();
        addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent e){
                mainInstance.saveToTxt(graph);
            }
        });
        // Load background image
        background = new ImageIcon("img\\Home.png").getImage();
        icon = new ImageIcon("img\\CroissantTae.png").getImage();
        setIconImage(icon);

        // Create layered pane for background and buttons
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1524, 824));

        // Background panel
        bgPanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };        
        bgPanel.setBounds(0, 0, 1524, 824);
        layeredPane.add(bgPanel, Integer.valueOf(0));

        // User Button
        userBtn = new JButton();
        userBtn.setBounds(430, 600, 300, 90);
        hideButton(userBtn);
        layeredPane.add(userBtn, Integer.valueOf(1)); 
        userBtn.addActionListener(e ->{
            UserGUI.showUserUI(graph);
        });

        // Moderator Button
        modBtn = new JButton();
        modBtn.setBounds(790, 600, 300, 90);
        hideButton(modBtn);
        layeredPane.add(modBtn, Integer.valueOf(1));
        modBtn.addActionListener(e ->{
            background = new ImageIcon("img\\ModKey.png").getImage();
            layeredPane.repaint();
        
            // Hide buttons
            userBtn.setVisible(false);
            modBtn.setVisible(false);
        
            // Create text input box
            keyInput = new JTextField();
            keyInput.setBorder(null);;
            keyInput.setOpaque(false);
            keyInput.setBounds((layeredPane.getWidth() - 360)/2, 645, 360, 50);
            keyInput.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        
            keyInput.addActionListener(ev ->{
                if(!keyInput.getText().trim().isEmpty()){
                    moderator(graph);
                    layeredPane.remove(keyInput); // remove input box
                    background = new ImageIcon("img\\Home.png").getImage();
                    userBtn.setVisible(true);
                    modBtn.setVisible(true);
                    layeredPane.repaint();
                }
            });
            layeredPane.add(keyInput, Integer.valueOf(2));
            keyInput.requestFocusInWindow();
        });
          
        add(layeredPane);
        pack();
        setVisible(true);
    }

    private void hideButton(JButton btn){
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
