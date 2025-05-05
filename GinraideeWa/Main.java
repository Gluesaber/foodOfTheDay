import java.io.*;
import java.util.*;

public class Main{
    private static final String DATA_FILE = "dataSet.txt";
    private static final String END_MARKER = "-1";
    private static final int DEFAULT_DISTANCE_KM = 3;
    private static final int COST_PER_KM = 10;
    private static final int TRIES = 240;

    private static String loveString(String input){
        StringBuilder result = new StringBuilder();
        int len = input.length();
        for(int i=0; i<len; i++){
            char c = input.charAt(i);
            if(i > 0 && Character.isUpperCase(c)){
                result.append(' ');
                result.append(Character.toLowerCase(c));
            }
            else if(c == ' ' && i+1 < len && Character.isLowerCase(input.charAt(i+1))){
                result.append(Character.toUpperCase(input.charAt(i+1)));
                i++;
            }
            else{
                result.append(c);
            }
        }
        return result.toString();
    }    

    public Graph formGraph(){
        Graph graph = new Graph();
    
        try(BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))){
            String line;
            while((line = reader.readLine()) != null){
                if(line.equals(END_MARKER)){
                    break;
                }
                String[] rest = line.split("\\s+");
                if(rest.length < 3){
                    System.out.println("[SOBAD] Skipping malformed restaurant line: " + line);
                    continue;
                }
                Vertex v = new Vertex(loveString(rest[0]), new AVLTree()); // decode name
                v.totalScore  = Integer.parseInt(rest[1]);
                v.ratingCount = Integer.parseInt(rest[2]);
                graph.addVertex(v);
    
                while((line = reader.readLine()) != null){
                    if(line.equals(END_MARKER)){
                        break;
                    }
                    String[] food = line.split("\\s+");
                    if(food.length < 7){
                        continue;
                    }
                    FoodItem fi = new FoodItem(
                        loveString(food[0]), // decode food name
                        Integer.parseInt(food[1]),
                        food[2],
                        Boolean.parseBoolean(food[3]),
                        Boolean.parseBoolean(food[4]),
                        Boolean.parseBoolean(food[5]),
                        Boolean.parseBoolean(food[6])
                    );
                    v.menu.insert(fi);
                }
            }
    
            while((line = reader.readLine()) != null){
                if(line.equals(END_MARKER)){
                    break;
                }
                String[] edge = line.split("\\s+");
                if(edge.length < 3){
                    continue;
                }
                Vertex src = graph.getVertex(loveString(edge[0]));  // decode
                Vertex dest = graph.getVertex(loveString(edge[1])); // decode
                int wei = Integer.parseInt(edge[2]);
                if(src != null && dest != null){
                    graph.addEdge(src, dest, wei);
                }else{
                    System.out.printf("[WARN] Cannot add edge, missing vertex: %s or %s%n", edge[0], edge[1]);
                }
            }
        }catch(FileNotFoundException e){
            System.err.println("[SOBAD] Could not locate dataSet.txt");
        }catch(IOException e){
            System.err.println("[SOBAD] I/O error while reading dataSet.txt");
        }
    
        return graph;
    }    

    void saveToTxt(Graph graph){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))){
            for(Vertex v : graph.nameToVertex.values()){
                writer.write(loveString(v.name) + " " + v.totalScore + " " + v.ratingCount); // encode name
                writer.newLine();
                saveMenu(writer, v.menu.root);
                writer.write(END_MARKER);
                writer.newLine();
            }
            writer.write(END_MARKER);
            writer.newLine();
    
            Set<Vertex> savedList = new HashSet<>();
            for(Vertex src : graph.adjacencyList.keySet()){
                for(Edge e : graph.adjacencyList.get(src)){
                    Vertex dest = e.destination;
                    if(savedList.contains(dest)) continue;
                    writer.write(String.format("%s %s %d",
                        loveString(src.name),  // encode
                        loveString(dest.name), // encode
                        e.weight));
                    writer.newLine();
                }
                savedList.add(src);
            }
    
            writer.write(END_MARKER);
            writer.newLine();
        }catch(IOException e){
            System.err.println("[SOBAD] I/O error while writing dataSet.txt");
        }
    }    

    private void saveMenu(BufferedWriter writer, TNode node) throws IOException{
        if(node == null) return;
        saveMenu(writer, node.left);
    
        FoodItem food = node.food;
        writer.write(String.format("%s %d %s %b %b %b %b", loveString(food.name), food.price, food.cuisine, food.isHalal, food.isHealthy, food.isSeafood, food.isVegan));
        writer.newLine();
    
        saveMenu(writer, node.right);
    }

    public static void main(String[] args){
        Main mainInstance = new Main();
        Graph graph = mainInstance.formGraph();
        Scanner sc = new Scanner(System.in);

        System.out.println("=====================================\n" + "          ** GINRAIDEEwa **\n" + "=====================================");
        System.out.println("Who in the world are you?\n  [1] User (find food)\n  [2] Moderator (manage house, restaurant and menu)");
        System.out.print("Enter your role: ");
        short option = sc.nextShort();

        if(option == 1){
            System.out.print("Whose house do you live in? (For location): ");
            String name = sc.next();
            Vertex home = graph.getVertex(name);

            System.out.print("\nWe'll find a dish within " + DEFAULT_DISTANCE_KM + " km.\n[0] to accept or Enter the farthest distance (km) you prefer: ");
            short distance = sc.nextShort();
            if(distance == 0){
                distance = DEFAULT_DISTANCE_KM;
            }
            Algorithm alg = new Algorithm();
            Map.Entry<Vertex, Integer> restaurant;
            
            System.out.print("Enter your budget range (min max): ");
            int min = sc.nextInt();
            int max = sc.nextInt();

            FoodItem input = new FoodItem("input", 0, "0", false, false, true, false);

            System.out.print("\nDo you have special requirements?\n  [1] Halal Food       [2] Healthy Food \n  [3] Seafood Allergy  [4] Vegetarian Food\nMultiple options allowed ([0] to go): ");
            do{
                option = sc.nextShort();
                if(option == 1){
                    input.isHalal = true;
                }
                if(option == 2){
                    input.isHealthy = true;
                }
                if(option == 3){
                    input.isSeafood = false;
                }
                if(option == 4){
                    input.isVegan = true;
                }
            }while(option != 0);

            System.out.print("\nWhich cuisine are you in the mood for?\n  [1] Chinese  [2] Indian  [3] Japanese  [4] Thai  [5] Western\nYour option ([0] for any): ");
            option = sc.nextShort();
            if(option == 1){
                input.cuisine = "Chinese";
            }
            if(option == 2){
                input.cuisine = "Indian";
            }
            if(option == 3){
                input.cuisine = "Japanese";
            }
            if(option == 4){
                input.cuisine = "Thai";
            }
            if(option == 5){
                input.cuisine = "Western";
            }

            FoodItem dish;
            short counter = 0;
            while(true){
                restaurant = alg.getRestaurant(graph, home, distance);
                dish = restaurant.getKey().menu.getFood(restaurant.getKey(), min - COST_PER_KM * restaurant.getValue(), max - COST_PER_KM * restaurant.getValue(), input);
                if(restaurant.getKey() == null || dish == null){
                    if(counter == TRIES){
                        System.out.println("No suitable restaurant or dish found based on your preferences.");
                        sc.close();
                        return;
                    }else{
                        counter++;
                        continue;
                    }
                }
                counter = 0;
                System.out.printf("\n%s from %s (rating: %.1f) is %d km away from %s's home.\n", 
                dish.name, restaurant.getKey().name, restaurant.getKey().getRating(), restaurant.getValue(), name);
                System.out.printf("The dish costs %d Baht, with %d Baht travel expenses.\n", dish.price, COST_PER_KM * restaurant.getValue());
                System.out.printf("** Total cost: %d Baht **\n", dish.price + COST_PER_KM * restaurant.getValue());

                System.out.print("[0] to accept or [1] to find new one: ");
                if(sc.nextShort() == 0) break;
            }
            System.out.println("\n(Assumes you have completed your meal.)");
            System.out.printf("How would you rate %s?(0 to 5): ", restaurant.getKey().name);
            restaurant.getKey().rate(sc.nextShort());
            System.out.println("\nI love you. <3");
        }else if(option == 2){
            System.out.print("Enter Moderator key: ");
            sc.next();
            System.out.print("\nGreetings, our precious moderator!");
            while(true){
                System.out.print("\nModerator options:\n  [1] Add house         [2] Edit House        [3] Remove house or restaurant\n  [4] Add restaurant    [5] Edit restaurant   [0] Exit\nYour option: ");
                option = sc.nextShort();
                if(option == 0) break;
                boolean isRest = true;
                if(option == 1 || option == 4){
                    if(option == 1) isRest = false;

                    String name;
                    while(true){
                        if(!isRest)
                            System.out.print("New house owner's name: ");
                        else System.out.print("New restaurant name: ");
                        name = sc.next();
                        if(graph.getVertex(name) != null)
                            System.out.println(name + " is already exist.\n");
                        else break;
                    }
                    Vertex v = new Vertex(name, new AVLTree());
                    if(isRest){
                        System.out.println("Let's add some foods to our new restaurant.");
                        System.out.println("(foodName price cuisine isHalal isHealthy isSeafood isVegan), or [0] to exit.");
                        String foodName;
                        while(true){
                            System.out.print("> ");
                            foodName = sc.next();
                            if(foodName.equals("0")) break;
                            v.menu.insert(new FoodItem(foodName, sc.nextShort(), sc.next(), sc.nextBoolean(), sc.nextBoolean(), sc.nextBoolean(), sc.nextBoolean()));
                        }
                    }
                    graph.addVertex(v);
                    System.out.println("Want to add paths to a house or restaurant?\nEnter destination (name) distance (km), or [0] to exit.");
                    String dest;
                    short wei;
                    while(true){
                        System.out.print("> ");
                        dest = sc.next();
                        if(dest.equals("0")) break;

                        wei = sc.nextShort();
                        if(graph.getVertex(dest) == null){
                            System.out.println("Couldn't find " + dest + ".");
                            continue;
                        }
                        graph.addEdge(v, graph.getVertex(dest), wei);
                    }
                }
                if(option == 2 || option == 5){
                    if(option == 2) isRest = false;

                    if(!isRest)
                        System.out.println("Let's edit a house.\nWhose house do you want to edit? [0] to exit.");
                    else System.out.println("Let's edit a Restaurant.\nWhich restaurant do you want to edit? [0] to exit.");

                    String name;
                    while(true){
                        System.out.print("> ");
                        name = sc.next();
                        if(isRest && graph.getVertex(name).menu.root == null){
                            System.out.println(name + " is not a restaurant.");
                            continue;
                        }
                        if(name.equals("0") || graph.getVertex(name) != null)
                            break;
                        else System.out.println("Couldn't find " + name + ".");
                    }
                    if(!name.equals("0")){
                        System.out.println("\nEditing options:\n  [1] Add path          [2] Remove path       [3] Rename");
                        if(isRest)
                            System.out.print("  [4] Add food          [5] Remove food     ");
                        System.out.print("  [0] Exit\nYour option: ");
                        option = sc.nextShort();
                        System.out.println();
                        if(option == 1){
                            System.out.println("Enter destination (name) distance (km), or [0] to exit.");
                            String dest;
                            short wei;
                            while(true){
                                System.out.print("> ");
                                dest = sc.next();
                                if(dest.equals("0")) break;

                                wei = sc.nextShort();
                                if(graph.getVertex(dest) == null){
                                    System.out.println("Couldn't find " + dest + ".");
                                    continue;
                                }
                                graph.addEdge(graph.getVertex(name), graph.getVertex(dest), wei);
                            }
                        }
                        if(option == 2){
                            System.out.println("Enter destination (name) to remove path, or [0] to exit.");
                            String dest;
                            while(true){
                                System.out.print("> ");
                                dest = sc.next();
                                if(dest.equals("0")) break;

                                if(graph.getVertex(dest) == null){
                                    System.out.println("Couldn't find " + dest + ".");
                                    continue;
                                }
                                graph.removeEdge(graph.getVertex(name), graph.getVertex(dest));
                            }
                        }
                        if(option == 3){
                            System.out.print("Rename to (0 to exit): ");
                            String newName = sc.next();
                            if(!newName.equals("0")){
                                if(graph.renameVertex(name, newName))
                                    System.out.println(name + " is renamed to " + newName + ".\n");
                            }
                        }
                        if(isRest){
                            String foodName;
                            if(option == 4){
                                System.out.println("Let's add more foods to " + name + ".");
                                System.out.println("(foodName price cuisine isHalal isHealthy isSeafood isVegan), or [0] to exit.");
                                while(true){
                                    System.out.print("> ");
                                    foodName = sc.next();
                                    if(foodName.equals("0")) break;
                                    graph.getVertex(name).menu.insert(new FoodItem(foodName, sc.nextShort(), sc.next(), sc.nextBoolean(), sc.nextBoolean(), sc.nextBoolean(), sc.nextBoolean()));
                                }
                            }
                            if(option == 5){
                                System.out.println("Food you want to remove?\n(foodName price), or [0] to exit.");
                                while(true){
                                    System.out.print("> ");
                                    foodName = sc.next();
                                    if(foodName.equals("0")) break;
                                    graph.getVertex(name).menu.remove(graph.getVertex(name).menu.getFoodItemByNameAndPrice(foodName, sc.nextShort()));
                                }
                            }
                        }
                        option = 0;
                    }  
                }
                if(option == 3){
                    String name;
                    System.out.println("Whose house do you want to remove? [0] to exit.");
                    while(true){
                        System.out.print("> ");
                        name = sc.next();
                        if(name.equals("0")) break;

                        if(graph.getVertex(name) == null){
                            System.out.println("Couldn't find " + name + ".");
                            continue;
                        }
                        graph.removeVertex(graph.getVertex(name));
                        System.out.println(name + " was removed.");
                    }
                }
            }
        }
        mainInstance.saveToTxt(graph);
        sc.close();
    }
}