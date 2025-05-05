class TNode{
    int height;
    FoodItem food;
    TNode left;
    TNode right;
    TNode(FoodItem value){
        this.food = value;
        this.height = 1;
    }
}