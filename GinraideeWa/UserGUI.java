
import java.awt.*;
import java.awt.event.*;
import java.util.Map.Entry;
import javax.swing.*;

public class UserGUI{
    private static final int DEFAULT_DISTANCE_KM = 3;
    private static final int COST_PER_KM = 10;
    private static final int TRIES = 240;

    private static void hideInput(JTextField box){
        box.setBorder(null);;
        box.setOpaque(false);
        box.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
    }

    private static void hideCheckBox(JCheckBox box){
        box.setIcon(new ImageIcon("img\\Empty.png"));
        box.setSelectedIcon(new ImageIcon("img\\Check.png"));
        box.setOpaque(false);
    }

    private static void hideRadi(JRadioButton box){
        box.setIcon(new ImageIcon("img\\Empty.png"));
        box.setSelectedIcon(new ImageIcon("img\\Circle.png"));
        box.setOpaque(false);
    }

    private static void hideStar(JCheckBox star){
        star.setIcon(new ImageIcon("img\\Empty.png"));
        star.setSelectedIcon(new ImageIcon("img\\Star.png"));
        star.setOpaque(false);
        star.setContentAreaFilled(false);
        star.setBorderPainted(false);
        star.setFocusPainted(false);
    }
    
    private static void setText(JTextArea text){
        text.setEditable(false);
        text.setOpaque(false);
        text.setFocusable(false);
        text.setFont(new Font("Comic Sans MS", Font.PLAIN, 32));
        text.setForeground(Color.BLACK);
    }  

    public static void showUserUI(Graph graph){
        JFrame frame = new JFrame("Let's find something to eat!");
        Image icon = new ImageIcon("img\\CroissantTae.png").getImage();
        frame.setIconImage(icon);
        frame.setSize(1540, 860);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Image background = new ImageIcon("img\\User.png").getImage();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1540, 860));

        JPanel bgPanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBounds(0, 0, 1540, 860);
        layeredPane.add(bgPanel, Integer.valueOf(0));

        // Add all input components
        JTextField houseField = new JTextField();
        houseField.setBounds(335, 275, 255, 50);
        hideInput(houseField);
        layeredPane.add(houseField, Integer.valueOf(1));

        JTextField distanceField = new JTextField();
        distanceField.setBounds(335, 423, 200, 50);
        hideInput(distanceField);
        layeredPane.add(distanceField, Integer.valueOf(1));

        JTextField minField = new JTextField();
        minField.setBounds(398, 569, 120, 50);
        hideInput(minField);
        layeredPane.add(minField, Integer.valueOf(1));

        JTextField maxField = new JTextField();
        maxField.setBounds(398, 642, 120, 50);
        hideInput(maxField);
        layeredPane.add(maxField, Integer.valueOf(1));

        JCheckBox halal = new JCheckBox();
        halal.setBounds(760, 275, 40, 40);
        hideCheckBox(halal);
        layeredPane.add(halal, Integer.valueOf(1));        

        JCheckBox healthy = new JCheckBox();
        healthy.setBounds(1048, 275, 40, 40);
        hideCheckBox(healthy);
        layeredPane.add(healthy, Integer.valueOf(1));

        JCheckBox seafood = new JCheckBox();
        seafood.setBounds(760, 337, 40, 40);
        hideCheckBox(seafood);
        layeredPane.add(seafood, Integer.valueOf(1));

        JCheckBox vegan = new JCheckBox();
        vegan.setBounds(1048, 337, 40, 40);
        hideCheckBox(vegan);
        layeredPane.add(vegan, Integer.valueOf(1));

// Radio buttons(Cuisine)
        JRadioButton any = new JRadioButton();
        any.setBounds(758, 482, 40, 40);
        hideRadi(any);
        any.setSelected(true);
        layeredPane.add(any, Integer.valueOf(1));

        JRadioButton chinese = new JRadioButton();
        chinese.setBounds(942, 482, 40, 40);
        hideRadi(chinese);
        layeredPane.add(chinese, Integer.valueOf(1));

        JRadioButton indian = new JRadioButton();
        indian.setBounds(1126, 482, 40, 40);
        hideRadi(indian);
        layeredPane.add(indian, Integer.valueOf(1));

        JRadioButton japanese = new JRadioButton();
        japanese.setBounds(758, 544, 40, 40);
        hideRadi(japanese);
        layeredPane.add(japanese, Integer.valueOf(1));

        JRadioButton thai = new JRadioButton();
        thai.setBounds(942, 544, 40, 40);
        hideRadi(thai);
        layeredPane.add(thai, Integer.valueOf(1));

        JRadioButton western = new JRadioButton();
        western.setBounds(1126, 544, 40, 40);
        hideRadi(western);
        layeredPane.add(western, Integer.valueOf(1));

        ButtonGroup cuisineGroup = new ButtonGroup();
        cuisineGroup.add(any);
        cuisineGroup.add(chinese);
        cuisineGroup.add(indian);
        cuisineGroup.add(japanese);
        cuisineGroup.add(thai);
        cuisineGroup.add(western);

        JButton hungry = new JButton();
        hungry.setBounds(760, 620, 240, 65); // centered under preferences
        hungry.setContentAreaFilled(false);
        hungry.setBorderPainted(false);
        layeredPane.add(hungry, Integer.valueOf(1));

        hungry.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String houseName = houseField.getText();
                Vertex home = graph.getVertex(houseName);
                short distance;
                try{
                    distance = Short.parseShort(distanceField.getText());
                }catch(Exception ex){
                    distance = DEFAULT_DISTANCE_KM;
                }

                int min = Integer.parseInt(minField.getText());
                int max = Integer.parseInt(maxField.getText());

                FoodItem input = new FoodItem("input", 0, "0", false, false, true, false);
                if(halal.isSelected()){
                    input.isHalal = true;
                }
                if(healthy.isSelected()){
                    input.isHealthy = true;
                }
                if(!seafood.isSelected()){
                    input.isSeafood = false;
                }
                if(vegan.isSelected()){
                    input.isVegan = true;
                }

                if(chinese.isSelected()){
                    input.cuisine = "Chinese"; 
                }else if(indian.isSelected()){
                    input.cuisine = "Indian"; 
                }else if(japanese.isSelected()){
                    input.cuisine = "Japanese"; 
                }else if(thai.isSelected()){
                    input.cuisine = "Thai"; 
                }else if(western.isSelected()){
                    input.cuisine = "Western";
                }
                showSuggestionPopup(frame, graph, home, distance, min, max, input);
            }
        });
        frame.setContentPane(layeredPane);
        frame.setVisible(true);
    }

    private static void showSuggestionPopup(JFrame parent, Graph graph, Vertex home, short maxDist, int min, int max, FoodItem input) {

        final Vertex[] selectedRes = new Vertex[1];

        Algorithm alg = new Algorithm();
        JDialog dialog = new JDialog(parent, "FOOOD!!!", true);
        dialog.setSize(1524, 824);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Image icon = new ImageIcon("img\\CroissantTae.png").getImage();
        dialog.setIconImage(icon);

        Image bgImage = new ImageIcon("img\\Food.png").getImage();
        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(1524, 824));

        // Background panel
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBounds(0, 0, 1524, 824);
        pane.add(bgPanel, Integer.valueOf(0));

        // Create all text components up front
        JTextArea food = new JTextArea(); setText(food); food.setFont(new Font("Comic Sans MS", Font.BOLD, 45)); food.setBounds(542, 168, 500, 124); pane.add(food, Integer.valueOf(1));
        JTextArea star = new JTextArea(); setText(star); star.setForeground(Color.WHITE); star.setBounds(670, 237, 100, 50); pane.add(star, Integer.valueOf(1));
        JTextArea rest = new JTextArea(); setText(rest); rest.setFont(new Font("Comic Sans MS", Font.BOLD, 34)); rest.setBounds(740, 237, 400, 124); pane.add(rest, Integer.valueOf(1));
        JTextArea dist = new JTextArea(); setText(dist); dist.setBounds(750, 345, 100, 50); pane.add(dist, Integer.valueOf(1));
        JTextArea fPrice = new JTextArea(); setText(fPrice); fPrice.setBounds(872, 458, 100, 50); pane.add(fPrice, Integer.valueOf(1));
        JTextArea tPrice = new JTextArea(); setText(tPrice); tPrice.setBounds(872, 499, 100, 50); pane.add(tPrice, Integer.valueOf(1));
        JTextArea total = new JTextArea(); setText(total); total.setFont(new Font("Comic Sans MS", Font.BOLD, 34)); total.setBounds((867), 564, 100, 50); pane.add(total, Integer.valueOf(1));
        
        // Buttons
        JButton accept = new JButton(); accept.setBounds(542, 631, 224, 84); accept.setContentAreaFilled(false); accept.setBorderPainted(false); pane.add(accept, Integer.valueOf(1));
        JButton retry = new JButton(); retry.setBounds(815, 631, 224, 80); retry.setContentAreaFilled(false); retry.setBorderPainted(false); pane.add(retry, Integer.valueOf(1));

        // Method to load a new suggestion
        Runnable updateSuggestion = () ->{
            Entry<Vertex, Integer> restaurant;
            FoodItem dish;
            int counter = 0;
            while(true){
                restaurant = alg.getRestaurant(graph, home, maxDist);
                dish = restaurant.getKey().menu.getFood(restaurant.getKey(), min - COST_PER_KM * restaurant.getValue(), max - COST_PER_KM * restaurant.getValue(), input);
                if(restaurant.getKey() == null || dish == null ){
                    if(counter == TRIES){
                        JOptionPane.showMessageDialog(parent, "No suitable restaurant or dish found based on your preferences.");
                        break;
                    }else{
                        counter++;
                        continue;
                    }
                }
                break;
            }

            selectedRes[0] = restaurant.getKey();

            int distance = restaurant.getValue();
            int travelCost = distance * COST_PER_KM;

            food.setText(dish.name);
            star.setText(String.format("%.1f", restaurant.getKey().getRating()));
            rest.setText(restaurant.getKey().name);
            dist.setText(distance + " km");
            fPrice.setText(String.valueOf(dish.price));
            tPrice.setText(String.valueOf(travelCost));
            total.setText(String.valueOf(dish.price + travelCost));
        };

        // Listeners
        accept.addActionListener(e -> {
            dialog.dispose(); 
            showRatingPopup(parent, selectedRes[0]);});
        retry.addActionListener(e -> updateSuggestion.run());

        // Finalize and show
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        updateSuggestion.run(); // first load
        dialog.setVisible(true);
    }

    private static void showRatingPopup(JFrame parent, Vertex restaurant) {
        JDialog dialog = new JDialog(parent, "Rating for a good future TT", true);
        dialog.setSize(1524, 824);
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Image icon = new ImageIcon("img\\CroissantTae.png").getImage();
        dialog.setIconImage(icon);

        Image bgImage = new ImageIcon("img\\Rating.png").getImage();
        JLayeredPane pane = new JLayeredPane();
        pane.setPreferredSize(new Dimension(1524, 824));

        // Background panel
        JPanel bgPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setLayout(null);
        bgPanel.setBounds(0, 0, 1524, 824);
        pane.add(bgPanel, Integer.valueOf(0));

        // Create all text components up front
        JTextArea rest = new JTextArea(); rest.setText(restaurant.name + "?"); rest.setFont(new Font("Comic Sans MS", Font.BOLD, 48)); rest.setBounds(520, 237, 500, 124); pane.add(rest, Integer.valueOf(1));
        rest.setEditable(false); rest.setOpaque(false); rest.setBorder(null); rest.setFocusable(false);
        JTextArea star = new JTextArea(); setText(star); star.setForeground(Color.WHITE); star.setBounds(520, 337, 100, 50); pane.add(star, Integer.valueOf(1));
        JTextArea rate = new JTextArea(); rate.setText(String.format("%.1f", restaurant.getRating())); rate.setForeground(Color.WHITE); rate.setFont(new Font("Comic Sans MS", Font.PLAIN, 34)); rate.setBounds(570, 317, 100, 50); pane.add(rate, Integer.valueOf(1));
        rate.setEditable(false); rate.setOpaque(false); rate.setBorder(null); rate.setFocusable(false);
        JTextArea count = new JTextArea(); count.setText("(" + String.format("%d", restaurant.ratingCount) + ")"); count.setFont(new Font("Comic Sans MS", Font.PLAIN, 30)); count.setBounds(640, 317, 200, 50); pane.add(count, Integer.valueOf(1));
        count.setEditable(false); count.setOpaque(false); count.setBorder(null); count.setFocusable(false);

    JCheckBox[] stars = new JCheckBox[5];
    final int[] selectedStars = {0}; // to store rating

    for (int i = 0; i < 5; i++){
        JCheckBox ratestar = new JCheckBox();
        ratestar.setBounds(571 + i * 89, 391, 100, 100);
        hideStar(ratestar);

        // Add action to update the selected stars and rating
        ratestar.addActionListener(e -> {
            int total = 0;
            for (int j=0; j<5; j++){
                if (stars[j].isSelected()) total++;
            }
            selectedStars[0] = total;
        });        
        
        stars[i] = ratestar;
        bgPanel.add(ratestar);
    }

        // Buttons
        JButton accept = new JButton(); accept.setBounds(838, 547, 224, 84); accept.setContentAreaFilled(false); accept.setBorderPainted(false); pane.add(accept, Integer.valueOf(1));
        
        // Listeners
        accept.addActionListener(e -> {
            dialog.dispose();
            restaurant.totalScore += selectedStars[0];
            restaurant.ratingCount++;});

        // Finalize and show
        dialog.setContentPane(pane);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
