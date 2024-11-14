package shop.storage;

import shop.generator.CustomerDatabase;
import shop.Customer;

import java.util.List;

public class StorePerformanceTest {
    private static final int NUM_CUSTOMERS = 2_000; // 2_000

    public static void main(String[] args) {
        // Generate test data
        CustomerDatabase customerDatabase = new CustomerDatabase(NUM_CUSTOMERS, 10, 5);
        List<Customer> customers = customerDatabase.getCustomersList();

        // Test KVStore
        System.out.println("Testing KVStore Implementation...");
        KVStoreImpl kvStore = new KVStoreImpl();
        long kvStoreTime = testStore(kvStore, customers);

        // Test H2Store
        System.out.println("\nTesting H2Store Implementation...");
        H2StoreImpl h2Store = new H2StoreImpl();
        long h2StoreTime = testStore(h2Store, customers);

        // Print comparison results
        printResults(kvStoreTime, h2StoreTime);
    }

    private static long testStore(CustomerStore store, List<Customer> customers) {
        try {
            // Clean up any existing data
            store.cleanUp();

            // Open store
            store.open();

            // Measure insertion time
            long startTime = System.currentTimeMillis();

            // Insert all customers
            for (Customer customer : customers) {
                store.insertCustomer(customer);
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            // Print individual results
            System.out.printf("Inserted %d customers in %d ms%n", NUM_CUSTOMERS, totalTime);
            System.out.printf("Average time per insertion: %.2f ms%n", (double) totalTime / NUM_CUSTOMERS);

            // Clean up
            store.close();
            return totalTime;

        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    private static void printResults(long kvStoreTime, long h2StoreTime) {
        System.out.println("\n=== Performance Comparison Results ===");
        System.out.println("KVStore total time: " + kvStoreTime + " ms");
        System.out.println("H2Store total time: " + h2StoreTime + " ms");

        if (kvStoreTime > 0 && h2StoreTime > 0) {
            double timeRatio = (double) h2StoreTime / kvStoreTime;
            System.out.printf("H2Store is %.2fx %s than KVStore%n",
                    Math.abs(timeRatio - 1),
                    timeRatio > 1 ? "slower" : "faster");
        }
    }
}