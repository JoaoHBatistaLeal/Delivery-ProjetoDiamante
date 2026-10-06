package br.com.fiap.delivery.order.loader;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final DishRepository dishRepository;

    public DataLoader(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public void run(String... args) {
        if (dishRepository.count() == 0) {
            dishRepository.saveAll(List.of(
                new Dish("House Burger", "Special blend burger with cheddar and artisan sauce", new BigDecimal("29.90"), 10),
                new Dish("Pizza Margherita", "Tomato sauce, mozzarella, and fresh basil", new BigDecimal("45.00"), 15),
                new Dish("Pasta Carbonara", "Spaghetti with bacon, eggs, and pecorino cheese", new BigDecimal("38.50"), 20),
                new Dish("Caesar Salad", "Romaine lettuce, croutons, parmesan, and caesar dressing", new BigDecimal("25.00"), 12),
                new Dish("Chocolate Brownie", "Warm chocolate brownie served with vanilla ice cream", new BigDecimal("18.00"), 25)
            ));
        }
    }
}
