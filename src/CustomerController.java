import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private List<Customers> customers;

    public CustomerController() {
        this.customers = new ArrayList<>();
        // Add some dummy customers for testing, as per project description screenshots
        // Customer_0, Customer_1, Customer_2, Customer_4, Customer_5
        // Names can be more descriptive if desired.
        customers.add(new Customers("Customer_0", 400.0)); // Example from screenshot
        customers.add(new Customers("Customer_1", 150.0));
        customers.add(new Customers("Customer_2", 300.0));
        // Skipping Customer_3 for now to match screenshot that shows _0,_1,_2,_4,_5
        customers.add(new Customers("Customer_4", 500.0)); // Another example
        customers.add(new Customers("Customer_5", 250.0));
    }

    public List<Customers> getCustomers() {
        return new ArrayList<>(customers); // Return a copy to prevent external modification of the internal list
    }

    public void addCustomer(Customers customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        // Optional: Add checks for duplicate customer IDs or names if required by business logic
        this.customers.add(customer);
    }

    public Customers getCustomer(int index) {
        if (index < 0 || index >= customers.size()) {
            // Or return null, or throw a more specific custom exception
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
        return null; // Not found
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
        // Let ConcreteCustomer's setters handle validation for name and balance
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
        return false; // Customer not found
    }
}