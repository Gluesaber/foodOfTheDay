
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class GraphGUI extends JPanel{

    private Image houseNodeImg;
    private Image restaurantNodeImg;
    private Image houseSelectedImg;
    private Image restaurantSelectedImg;

    private Graph graph;
    private Map<Vertex, Point> positions;
    private final int RADIUS = 60;
    private final int H_GAP = 270;
    private final int V_GAP = 135;
    private final int PANEL_WIDTH = 1500;
    private final int PANEL_HEIGHT = 2600;

    private Vertex draggedVertex = null;
    private Point dragOffset = null;
    private Vertex selectedVertex = null;
    private Edge selectedEdge = null;

    private Vertex edgeStartVertex = null;
    private Vertex edgeEndVertex = null;
    private Point tempEdgeEndPoint = null;

    private JTextField weightInputField = null;
    private Vertex tempEdgeStart = null;
    private Vertex tempEdgeEnd = null;

    JPopupMenu popupMenu;
    Point popupPoint;

    public GraphGUI(Graph graph){
        this.graph = graph;
        this.positions = new HashMap<>();
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        calculatePositions();

        houseNodeImg = new ImageIcon("img/HNode.png").getImage();
        restaurantNodeImg = new ImageIcon("img/RNode.png").getImage();
        houseSelectedImg = new ImageIcon("img/HSelected.png").getImage();
        restaurantSelectedImg = new ImageIcon("img/RSelected.png").getImage();

        popupMenu = new JPopupMenu();

        JMenuItem addHouse = new JMenuItem("Add house");
        addHouse.addActionListener(e -> showVertexNameInput("House"));
        addHouse.setBackground(new Color(0xD3FFF6));

        JMenuItem addRestaurant = new JMenuItem("Add restaurant");
        addRestaurant.addActionListener(e -> showVertexNameInput("Restaurant"));
        addRestaurant.setBackground(new Color(0xD3FFF6));

        popupMenu.add(addHouse);
        popupMenu.add(addRestaurant);
        popupMenu.setBorder(BorderFactory.createEmptyBorder());

        MouseAdapter mouseAdapter = new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    boolean isNearNode = false;
    
                    for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                        Point p = entry.getValue();
                        if(p.distance(e.getPoint()) <= RADIUS){
                            isNearNode = true; // A node exists near the clicked point
                            break;
                        }
                    }
    
                    if(!isNearNode){
                        popupPoint = e.getPoint(); // Remember where to add
                        popupMenu.show(GraphGUI.this, e.getX(), e.getY());
                    }
                }

                if(SwingUtilities.isRightMouseButton(e)){
                    for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                        Point p = entry.getValue();
                        if(p.distance(e.getPoint()) <= RADIUS){
                            edgeStartVertex = entry.getKey();
                            break;
                        }
                    }
                    return; // prevent dragging on right click
                }
            
                // your existing left-drag start logic
                for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                    Point p = entry.getValue();
                    if(p.distance(e.getPoint()) <= RADIUS){
                        draggedVertex = entry.getKey();
                        dragOffset = new Point(e.getX() - p.x, e.getY() - p.y);
                        break;
                    }
                }
            }            

            public void mouseDragged(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    if(edgeStartVertex != null){
                        tempEdgeEndPoint = e.getPoint();
                        repaint();
                    }
                    return;
                }
            
                if(draggedVertex != null){
                    positions.put(draggedVertex, new Point(e.getX() - dragOffset.x, e.getY() - dragOffset.y));
                    repaint();
                }
            }
            
            private void showWeightInput(Vertex from, Vertex to){
                Point p1 = positions.get(from);
                Point p2 = positions.get(to);
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
            
                weightInputField = new JTextField("");
                weightInputField.setBorder(BorderFactory.createEmptyBorder());
                weightInputField.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
                weightInputField.setBounds(midX - 30, midY - 12, 30, 24);
            
                tempEdgeStart = from;
                tempEdgeEnd = to;
                new Point(midX, midY);
            
                weightInputField.addKeyListener(new KeyAdapter(){
                    public void keyPressed(KeyEvent e){
                        if(e.getKeyCode() == KeyEvent.VK_ENTER){
                            String text = weightInputField.getText();
                            try{
                                int weight = Integer.parseInt(text);
                                graph.addEdge(tempEdgeStart, tempEdgeEnd, weight);
                            }catch(NumberFormatException ex){
                                // Invalid, do nothing
                            }
                            removeWeightInput();
                            repaint();
                        }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                            removeWeightInput();
                            repaint();
                        }
                    }
                });
            
                // Clicking outside removes the input field
                GraphGUI.this.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e){
                        if(weightInputField != null && !weightInputField.getBounds().contains(e.getPoint())){
                            removeWeightInput();
                            repaint();
                        }
                    }
                });
            
                setLayout(null);
                add(weightInputField);
                weightInputField.requestFocusInWindow();
            }
            
            private void removeWeightInput(){
                if(weightInputField != null){
                    remove(weightInputField);
                    weightInputField = null;
                    tempEdgeStart = null;
                    tempEdgeEnd = null;
                    repaint();
                }
            }            

            public void mouseReleased(MouseEvent e){
                if(SwingUtilities.isRightMouseButton(e)){
                    if(edgeStartVertex != null){
                        boolean foundTarget = false;
                        for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                            Point p = entry.getValue();
                            if(p.distance(e.getPoint()) <= RADIUS){
                                edgeEndVertex = entry.getKey();
                                if(edgeEndVertex != edgeStartVertex){
                                    showWeightInput(edgeStartVertex, edgeEndVertex);
                                }
                                foundTarget = true;
                                break;
                            }
                        }
                        if(!foundTarget) repaint();
                    }
                    edgeStartVertex = null;
                    edgeEndVertex = null;
                }
            }                                 
        };

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)){
                    for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                        Point p = entry.getValue();
                        int dx = e.getX() - p.x;
                        int dy = e.getY() - p.y;
                        if(dx * dx + dy * dy <= RADIUS * RADIUS && entry.getKey().menu.root != null){
                            selectedVertex = entry.getKey();
                            selectedEdge = null;
    
                            AVLTreeGUI treeWindow = new AVLTreeGUI(entry.getKey().menu);
                            treeWindow.setVisible(true);
                        }
                    }
                    return;
                }
            }
        });

        addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if(e.getButton() != MouseEvent.BUTTON1){
                    return; // Left-click only
                }

                // Check if we clicked on a vertex
                for(Map.Entry<Vertex, Point> entry : positions.entrySet()){
                    Point p = entry.getValue();
                    int dx = e.getX() - p.x;
                    int dy = e.getY() - p.y;
                    if(dx * dx + dy * dy <= RADIUS * RADIUS){
                        selectedVertex = entry.getKey();
                        selectedEdge = null; // Deselect edge if vertex selected
                        
                        repaint();
                        return;
                    }
                }

                // If we clicked between two vertices(on an edge)
                selectedEdge = null;
                // In your mouseClicked method:
                for(Vertex v : graph.adjacencyList.keySet()){
                    Point p1 = positions.get(v);
                    for(Edge eObj : graph.adjacencyList.get(v)){
                        Point p2 = positions.get(eObj.destination);
                        if(isNearEdge(e.getPoint(), p1, p2)){
                            selectedEdge = eObj;
                            selectedVertex = null; // Deselect vertex if edge selected
                            repaint();
                            return;
                        }
                    }
                }

                selectedVertex = null;
                repaint();
            }
        });

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    if(selectedVertex != null){
                        // Remove the selected vertex
                        graph.removeVertex(selectedVertex);
                        positions.remove(selectedVertex);
                        selectedVertex = null;
                    } else if(selectedEdge != null){
                        // Remove the selected edge using the source and destination
                        removeEdge(selectedEdge);
                        selectedEdge = null;
                    }
                    repaint();
                }
            }
        });
    }

    public void showVertexNameInput(String type){
        JTextField input = new JTextField();
        input.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
        input.setBorder(BorderFactory.createEmptyBorder()); // No outline
        input.setBackground(new Color(0xD3FFF6));

        String labelText = type.equalsIgnoreCase("House") ? "New house owner's name:" : "New restaurant name:";
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0xF5FFF1));
        panel.add(label);
        panel.add(input);
    
        JDialog dialog = new JDialog((Frame)null, "Enter " + type + " name", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setSize(150, 37);
        dialog.setLocation(popupPoint); // Show near click
        dialog.setUndecorated(true); // Remove title bar
    
        input.addActionListener(e ->{
            String name = input.getText().trim();
            if(!name.isEmpty()){
                Vertex v = new Vertex(name, new AVLTree());
                graph.addVertex(v);
                positions.put(v, popupPoint); // Add at clicked point
                repaint();
            }
            dialog.dispose();
        });
    
        input.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    dialog.dispose();
                }
            }
        });
    
        dialog.setVisible(true);
    }    

    // This method determines if the mouse click is near the edge
    private boolean isNearEdge(Point clickPoint, Point p1, Point p2){
        double distance = Math.abs((p2.y - p1.y) * clickPoint.x -(p2.x - p1.x) * clickPoint.y
                + p2.x * p1.y - p2.y * p1.x)
                / Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));

        return distance <= 3; // You can adjust the threshold
    }

    public void keyPressed(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_DELETE){
            if(selectedVertex != null){
                // Remove the selected vertex
                graph.removeVertex(selectedVertex);
                selectedVertex = null;
            } else if(selectedEdge != null){
                // Remove the selected edge
                removeEdge(selectedEdge);
                selectedEdge = null;
            }
            repaint();
        }
    }

    public Vertex getSource(Edge selectedEdge){
        for(Map.Entry<Vertex, List<Edge>> entry : graph.adjacencyList.entrySet()){
            Vertex vertex = entry.getKey();
            List<Edge> edges = entry.getValue();
            
            // Check if the edge is in the list of edges of this vertex
            for(Edge e : edges){
                if(e.equals(selectedEdge)){
                    return vertex;  // Return the source vertex of the selected edge
                }
            }
        }
        return null; // If no source is found, return null
    }

    public void removeEdge(Edge selectedEdge){
        // Get the source vertex using the getSource method
        Vertex sourceVertex = getSource(selectedEdge);
    
        // Now that we know the source, remove the selected edge from both the source and destination
        if(sourceVertex != null){
            // Remove the edge from the source vertex's adjacency list
            graph.removeEdge(sourceVertex, selectedEdge.destination);
            selectedEdge = null; // Reset selected edge
            repaint();
        }
    }    

    private void calculatePositions(){
        List<Vertex> vertices = new ArrayList<>(graph.adjacencyList.keySet());
        int index = 0;
        int y = 100;
        int spacing = H_GAP / 2;
        int rowHeight = V_GAP;
        boolean isOddRow = true;

        while(index < vertices.size()){
            int nodesInRow = isOddRow ? 5 : 4;
            int totalWidth = (nodesInRow - 1) * 2 * spacing;
            int startX = (PANEL_WIDTH - totalWidth) / 2;

            for(int i = 0; i < nodesInRow && index < vertices.size(); i++){
                int x = startX + i * 2 * spacing;
                positions.put(vertices.get(index), new Point(x, y));
                index++;
            }

            y += rowHeight;
            isOddRow = !isOddRow;
        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setColor(new Color(0xF5FFF1));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setStroke(new BasicStroke(2));
        for(Vertex v : graph.adjacencyList.keySet()){
            Point p1 = positions.get(v);
            for(Edge e : graph.adjacencyList.get(v)){
                Vertex dest = e.destination;
                Point p2 = positions.get(dest);
                if(p1 != null && p2 != null && v.name.compareTo(dest.name) < 0){
                    boolean isSelected = selectedEdge != null
                            &&((getSource(selectedEdge).name.equals(v.name) && selectedEdge.destination.name.equals(dest.name))
                            ||(getSource(selectedEdge).name.equals(dest.name) && selectedEdge.destination.name.equals(v.name)));
                    if(selectedVertex == v || selectedVertex == dest || isSelected){
                        g2.setColor(new Color(0xFE6400));
                        g2.setStroke(new BasicStroke(3));
                    }else{
                        g2.setColor(Color.BLACK);
                        g2.setStroke(new BasicStroke(1));
                    }
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                    String label = e.weight + " km";
                    FontMetrics fm = g2.getFontMetrics();
                    int labelWidth = fm.stringWidth(label);
                    int labelHeight = fm.getAscent();
                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    if(selectedVertex == v || selectedVertex == dest || isSelected){
                        g2.setColor(new Color(0xFE6400));
                    }else{
                        g2.setColor(Color.BLACK);
                    }
                    g2.drawString(label, midX - labelWidth / 2, midY - labelHeight / 2);
                }
            }
        }

        if(edgeStartVertex != null && tempEdgeEndPoint != null){
            Point from = positions.get(edgeStartVertex);
            g.setColor(new Color(0xFE6400));
            g.drawLine(from.x, from.y, tempEdgeEndPoint.x, tempEdgeEndPoint.y);
        }        

        for(Vertex v : positions.keySet()){
            Point p = positions.get(v);
            boolean isSelected = v == selectedVertex;

            Image icon;
            if(v.menu.root == null){
                icon = isSelected ? houseSelectedImg : houseNodeImg;
            } else{
                icon = isSelected ? restaurantSelectedImg : restaurantNodeImg;
            }

            g2.drawImage(icon, p.x - RADIUS, p.y - RADIUS, 2 * RADIUS, 2 * RADIUS, this);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(v.name);
            int textHeight = fm.getAscent();
            g2.setColor(Color.BLACK);
            g2.drawString(v.name, p.x - textWidth / 2, p.y + textHeight / 2 - 2);
        }
    }
}
