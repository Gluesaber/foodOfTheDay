import java.io.*;
import java.util.*;

public class Main{
    Graph formGraph(){
        Graph graph = new Graph();
    
        try(BufferedReader reader = new BufferedReader(new FileReader("dataSet.txt"))){
            String line;
            while((line = reader.readLine()) != null){
                if(line.equals("-1")){
                    break;
                }
    
                String[] rest = line.split("\\s+");
                if(rest.length < 3){
                    System.out.println("[SOBAD] Skipping malformed restaurant line: " + line);
                    continue;
                }
                Vertex v = new Vertex(rest[0], new AVLTree());
                v.totalScore  = Integer.parseInt(rest[1]);
                v.ratingCount = Integer.parseInt(rest[2]);
                graph.addVertex(v);
    
                while((line = reader.readLine()) != null){
                    if (line.equals("-1")) {
                        break;
                    }
                    String[] food = line.split("\\s+");
                    if (food.length < 7){
                        continue;
                    }
                    FoodItem fi = new FoodItem(
                        food[0],
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
                if (line.equals("-1")){
                    break;
                }
                String[] edge = line.split("\\s+");
                if (edge.length < 3){
                    continue;
                }
                Vertex src = graph.getVertex(edge[0]);
                Vertex dest = graph.getVertex(edge[1]);
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
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("dataSet.txt"))){
            for (Vertex v : graph.nameToVertex.values()) {
                // Write restaurant header
                writer.write(v.name + " " + v.totalScore + " " + v.ratingCount);
                writer.newLine();
            
                // Write each menu item (assuming AVLTree has an inOrder() method)
                saveMenu(writer, v.menu.root);
            
                // Write -1 to indicate the end of the restaurant's menu
                writer.write("-1");
                writer.newLine();
            }            
            writer.write("-1");
            writer.newLine();
            // write edges
            Set<Vertex> savedList = new HashSet<>();
            for(Vertex src : graph.adjacencyList.keySet()){
                for(Edge e : graph.adjacencyList.get(src)){
                    Vertex dest = e.destination;
                    if(savedList.contains(dest)) continue; // skip if we've already written dest→src
                    writer.write(String.format("%s %s %d", src.name, dest.name, e.weight));
                    writer.newLine();
                }
                savedList.add(src);
            }
    
            writer.write("-1");
            writer.newLine();
        }catch(IOException e){
            System.err.println("[SOBAD] I/O error while writing dataSet.txt");
        }
    }

    //Inorder
    private void saveMenu(BufferedWriter writer, TNode node) throws IOException{
        if(node == null) return;
        saveMenu(writer, node.left);

        FoodItem food = node.food;
        writer.write(String.format("%s %d %s %b %b %b %b", food.name, food.price, food.cuisine, food.isHalal, food.isHealthy, food.isSeafood, food.isVegan));
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

            System.out.print("\nWe'll find a dish within 3 km.\n[0] to accept or Enter the farthest distance (km) you prefer: ");
            short distance = sc.nextShort();
            if(distance == 0){
                distance = 3;
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

            System.out.print("\nAny preferred cuisine?\n  [1] Chinese  [2] Indian  [3] Japanese  [4] Thai  [5] Western\nYour option ([0] for any): ");
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
                dish = restaurant.getKey().menu.getFood(restaurant.getKey(), min - 10 * restaurant.getValue(), max - 10 * restaurant.getValue(), input);
                if(restaurant.getKey() == null || dish == null){
                    if(counter == 10){
                        System.out.println("No suitable restaurant or dish found based on your preferences.");
                        counter = 0;
                        break;
                    }else{
                        counter++;
                        continue;
                    }
                }
                counter = 0;
                System.out.printf("\n%s from %s (rating: %.1f) is %d km away from %s's home.\n", 
                dish.name, restaurant.getKey().name, restaurant.getKey().getRating(), restaurant.getValue(), name);
                System.out.printf("The dish costs %d Baht, with %d Baht travel expenses.\n", dish.price, 10 * restaurant.getValue());
                System.out.printf("** Total cost: %d Baht **\n", dish.price + 10 * restaurant.getValue());

                System.out.print("[0] to accept or [1] to find new one: ");
                if(sc.nextShort() == 0) break;
            }
            System.out.println("\n(Assumes you have completed your meal.)");
            System.out.printf("How would you rate %s? (0 to 5): ", restaurant.getKey().name);
            restaurant.getKey().rate(sc.nextShort());
            System.out.println("\nI love you. <3");
        }else if(option == 2){
            System.out.print("Enter Moderator key: ");
            sc.next();
            System.out.print("\nGreetings, our precious moderator!\n");
            while(true){
                System.out.print("  [1] Add house          [2] Delete house      [3] Edit house\n  [4] Add restaurant     [5] Delete restaurant [6] Edit restaurant\n  [0] exit\nYour option: ");
                option = sc.nextShort();
                System.out.println();
                if(option == 0) break;
                if(option == 1){
                    
                }
            }
        }

        mainInstance.saveToTxt(graph);
        sc.close();
    }
}