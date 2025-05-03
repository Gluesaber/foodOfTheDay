public class FoodItem implements Comparable<FoodItem> {
    String name;
    int price;
    String cuisine;
    boolean isSpicy;
    boolean isHalal;
    boolean isHealthy;
    boolean isSeafood;
    boolean isVegan;

    public FoodItem(String name, int price, String cuisine, boolean isSpicy, boolean isHalal, boolean isHealthy, boolean isSeafood, boolean isVegan) {
        this.name = name;
        this.price = price;
        this.cuisine = cuisine;
        this.isSpicy = isSpicy;
        this.isHalal = isHalal;
        this.isHealthy = isHealthy;
        this.isSeafood = isSeafood;
        this.isVegan = isVegan;
    }

    @Override
    public String toString() {
        return name + " (" + price + ")";
    }

    @Override
    public int compareTo(FoodItem other) {
        int priceComparison = Integer.compare(this.price, other.price);
        if (priceComparison != 0) {
            return priceComparison;  // If prices are different, use that to determine order
        }
        // If prices are the same, compare by name
        return this.name.compareTo(other.name);
    }

}
