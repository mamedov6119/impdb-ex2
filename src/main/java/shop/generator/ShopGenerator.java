package shop.generator;

import shop.Customer;
import shop.Order;
import shop.Product;

import java.util.ArrayList;
import java.util.Random;

import java.util.List;

// TODO: Task 2.2 a)
public class ShopGenerator {

    private static final Random random = new Random();

    public static List<Product> generateProducts(int numProducts) {
        // TODO
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < numProducts; i++) {
            products.add(new Product(i, "Product " + i));
        }
        return products;
    }

    public static List<Order> generateOrders(List<Product> products, int numOrders) {
        // TODO
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < numOrders; i++) {
            List<Product> orderProducts = new ArrayList<>();
            int numOfProductsInOrder = random.nextInt(products.size()) + 1;

            for (int j = 0; j < numOfProductsInOrder; j++) {
                Product product = products.get(random.nextInt(products.size()));
                if (!orderProducts.contains(product)) {
                    orderProducts.add(product);
                }
            }

            orders.add(new Order(i, "Shipping Address " + i, orderProducts));
        }
        return orders;
    }

    public static Customer generateCustomer(List<Product> products, int maxOrders) {
        // TODO
        int numOrders = random.nextInt(maxOrders) + 1;
        Integer customerID = random.nextInt(1000);
        String customerName = "Customer  " + customerID;
        String address = "Address + " + random.nextInt(1000);

        Customer customer = new Customer(customerID, customerName, address);

        List<Order> orders = generateOrders(products, numOrders);

        for (Order order : orders) {
            customer.addOrder(order);
        }

        return customer;
    }

}
