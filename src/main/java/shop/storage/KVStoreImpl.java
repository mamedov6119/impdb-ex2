package shop.storage;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.PrimaryHashMap;
import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.HashMap;
import java.util.Map;

// TODO: Task 2.2 c)
public class KVStoreImpl implements CustomerStore, CustomerStoreQuery {

    private PrimaryHashMap<Integer, Customer> customerMap;
    private RecordManager recordManager;

    @Override
    public void open() {
        // TODO
        try {
            recordManager = RecordManagerFactory.createRecordManager("customerDatabase");
            customerMap = recordManager.hashMap("customerMap");
        } catch (Exception e) {
            System.err.println("Opening Exception caught! -- " + e);
        }

    }

    @Override
    public void insertCustomer(Customer customer) {
        // TODO
        try {
            customerMap.put(customer.getCustomerId(), customer);

            recordManager.commit();
        } catch (Exception e) {
            System.err.println("Insertion Exception caught! -- " + e);
        }
    }

    @Override
    public void close() {
        // TODO
        try {
            if (recordManager != null) {
                recordManager.close();
            }
        } catch (Exception e) {
            System.err.println("Closing Exception caught! -- " + e);
        }
    }

    @Override
    public void cleanUp() {
        // TODO
        try {
            if (recordManager != null) {
                recordManager.close();
            }

            java.io.File dbFile = new java.io.File("customerDatabase.db");
            java.io.File dbFileLck = new java.io.File("customerDatabase.lck");

            if (dbFile.exists() && !dbFile.delete()) {
                System.err.println("Failed to delete database file!");
            }

            if (dbFileLck.exists() && !dbFileLck.delete()) {
                System.err.println("Failed to delete lock file!");
            }

        } catch (Exception e) {
            System.err.println("Cleaning Up Exception caught! -- " + e);
        }
    }

    @Override
    public void queryAllUsers() {
        // TODO
        try {
            for (Integer customerID : customerMap.keySet()) {
                Customer customer = customerMap.get(customerID);
                System.out.println("Customer ID: " + customerID + ", Customer: " + customer);
            }
        } catch (Exception e) {
            System.err.println("Querying All Users Exception caught! -- " + e);
        }
    }

    @Override
    public void queryTopProduct() {
        // TODO
        try {
            Map<String, Integer> productCount = new HashMap<>();
            for (Customer customer : customerMap.values()) {
                for (Order order : customer.getOrders()) {
                    for (Product product : order.getItems()) {
                        String productName = product.getName();
                        productCount.put(productName, productCount.getOrDefault(productName, 0) + 1);
                    }
                }
            }

            String topProductName = "";
            int maxCount = 0;

            for (Map.Entry<String, Integer> entry : productCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    topProductName = entry.getKey();
                    maxCount = entry.getValue();
                }
            }

            if (!topProductName.isEmpty()) {
                System.out.println("Top Product: " + topProductName + " with " + maxCount + " purchases.");
            } else {
                System.out.println("No products found.");
            }
        } catch (Exception e) {
            System.err.println("Querying Top Product Exception caught! -- " + e);
        }
    }
}
