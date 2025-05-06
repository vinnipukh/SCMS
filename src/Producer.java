import java.util.Objects;

public class Producer {
    private String name;
    private int  cost;
    private int price;
    private int capacity;
    private int fund;
    private int itemID;

    public Producer(String name, int capacity, int fund, int price, int cost) {
        this.name = name;
        this.capacity = capacity;
        this.fund = fund;
        this.price = price;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getFund() {
        return fund;
    }

    public void setFund(int fund) {
        this.fund = fund;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Producer producer = (Producer) o;
        return cost == producer.cost && price == producer.price && capacity == producer.capacity && fund == producer.fund && itemID == producer.itemID && Objects.equals(name, producer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost, price, capacity, fund, itemID);
    }
}
