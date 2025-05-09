import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private List<Customers> customers;

    public CustomerController() {
        this.customers = new ArrayList<>();
        /*
        DUMMY'S FOR TESTING
        customers.add(new Customers("Customer_0", 400.0));
        customers.add(new Customers("Customer_1", 150.0));
        customers.add(new Customers("Customer_2", 300.0));
        customers.add(new Customers("Customer_4", 500.0));
        customers.add(new Customers("Customer_5", 250.0));

         */
    }

    public List<Customers> getCustomers() {
        return new ArrayList<>(customers);
    }

    public void addCustomer(Customers customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        this.customers.add(customer);
    }

    public Customers getCustomer(int index) {
        if (index < 0 || index >= customers.size()) {
            throw new IndexOutOfBoundsException("Invalid customer index: " + index);
        }
        return customers.get(index);
    }

    public Customers findCustomerByID(String customerID) {
        for (Customers customer : customers) {
            if (customer.getCustomerID().equals(customerID)) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Edits an existing customer's details.
     * @param customerID The ID of the customer to edit.
     * @param newName The new name for the customer.
     * @param newBalance The new balance for the customer.
     * @throws IllegalArgumentException if customer not found or inputs are invalid (handled by ConcreteCustomer setters).
     */
    public void editCustomer(String customerID, String newName, double newBalance) {
        Customers customerToEdit = findCustomerByID(customerID);
        if (customerToEdit == null) {
            throw new IllegalArgumentException("Customer with ID " + customerID + " not found for editing.");
        }
        customerToEdit.setName(newName);
        customerToEdit.setBalance(newBalance);
    }

    /**
     * Deletes a customer from the list.
     * @param customerID The ID of the customer to delete.
     * @return true if the customer was found and removed, false otherwise.
     */
    public boolean deleteCustomer(String customerID) {
        Customers customerToRemove = findCustomerByID(customerID);
        if (customerToRemove != null) {
            return this.customers.remove(customerToRemove);
        }
        return false;
    }
}