public class FoodItem{
    String name;
    int price;
    String cuisine;
    boolean isSpicy;
    boolean isHalal;
    boolean isHealthy;
    boolean isSeafood;
    boolean isVegan;

    public FoodItem(String name, int price, String cuisine, boolean isSpicy, boolean isHalal, boolean isHealthy, boolean isSeafood, boolean isVegan){
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
        return name + " (" + cuisine + ") - " + price + " THB";
    }
}