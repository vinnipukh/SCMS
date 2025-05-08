import java.util.ArrayList;
import java.util.List;

public class FactoryController {
    private List<Factory> factories;

    public FactoryController() {
        factories = new ArrayList<>();
    }

    public List<Factory> getFactories() {
        return factories;
    }

    public void addFactory(Factory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null.");
        }
        factories.add(factory);
    }

    public void editFactory(int index, Factory updatedFactory) {
        if (index < 0 || index >= factories.size()) {
            throw new IndexOutOfBoundsException("Invalid factory index.");
        }
        if (updatedFactory == null) {
            throw new IllegalArgumentException("Updated factory cannot be null.");
        }
        factories.set(index, updatedFactory);
    }

    public Factory getFactory(int index) {
        if (index < 0 || index >= factories.size()) {
            throw new IndexOutOfBoundsException("Invalid factory index.");
        }
        return factories.get(index);
    }
} 