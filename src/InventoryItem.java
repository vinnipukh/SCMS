import java.util.Objects;

public abstract class InventoryItem  {

    private final String name;
    private final String itemID;
    private static int nextID = 0;
    public InventoryItem(String name){
        this.name = name;
        this.itemID = "ITEM_"+nextID++;


    }

    /**
     * I have created itemID to see when there is same objects of InventoryItem.
     * They are assigned sequentially  starting from 0.
     */

    public String getName() {
        return this.name;
    }

    public String getItemID() {
        return this.itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    /**
     * I have not added itemID to both equals and hashCode methods because
     * they will be used to tell if the objects are the same  or not.
     *
     */

    @Override
    public String toString() {
        return "InventoryItem{" +
                "name='" + name + '\'' +
                ", itemID=" + itemID +
                '}';
    }
}
