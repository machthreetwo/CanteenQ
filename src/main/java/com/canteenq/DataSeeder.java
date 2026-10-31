package com.canteenq;

import com.canteenq.model.*;
import com.canteenq.repository.FoodOrderRepository;
import com.canteenq.repository.MemberRepository;
import com.canteenq.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private MenuItemRepository menuRepo;

    @Autowired
    private MemberRepository memberRepo;

    @Autowired
    private FoodOrderRepository orderRepo;

    @Override
    public void run(String... args) {
        if (menuRepo.count() > 0) {
            return;
        }
        List<MenuItem> menu = menuRepo.saveAll(List.of(
                new MenuItem("Samosa", Category.SNACK, 15, 60),
                new MenuItem("Bread Pakora", Category.SNACK, 20, 40),
                new MenuItem("Veg Thali", Category.VEG, 90, 30),
                new MenuItem("Rajma Chawal", Category.VEG, 70, 25),
                new MenuItem("Paneer Paratha", Category.VEG, 45, 40),
                new MenuItem("Chole Bhature", Category.VEG, 60, 20),
                new MenuItem("Chicken Biryani", Category.NON_VEG, 130, 15),
                new MenuItem("Egg Roll", Category.NON_VEG, 40, 35),
                new MenuItem("Masala Chai", Category.BEVERAGE, 12, 100),
                new MenuItem("Cold Coffee", Category.BEVERAGE, 50, 30),
                new MenuItem("Gulab Jamun", Category.DESSERT, 25, 8)
        ));

        List<Member> people = memberRepo.saveAll(List.of(
                new Student("Akash Kumar", "9876543210", 500, "22BCS1045", "CSE"),
                new Student("Sumit Raj", "9123456780", 350, "22BIT2011", "IT"),
                new Staff("Nitin Raj", "9988776655", 800, "Accounts"),
                new Student("Priya Singh", "9871203456", 420, "22BCS1178", "CSE"),
                new Student("Rahul Yadav", "9012345678", 150, "22BME3002", "Mechanical"),
                new Staff("Meena Gupta", "9090909090", 600, "Library")
        ));

        Random random = new Random(42);
        LocalDate today = LocalDate.now();
        for (int daysAgo = 13; daysAgo >= 1; daysAgo--) {
            int ordersThatDay = 2 + random.nextInt(4);
            for (int t = 1; t <= ordersThatDay; t++) {
                FoodOrder order = new FoodOrder(people.get(random.nextInt(people.size())), "No offer");
                int dishes = 1 + random.nextInt(2);
                for (int d = 0; d < dishes; d++) {
                    order.addLine(new OrderLine(menu.get(random.nextInt(menu.size())), 1 + random.nextInt(3)));
                }
                order.setToken(t);
                order.setStatus(OrderStatus.COLLECTED);
                order.setPlacedAt(today.minusDays(daysAgo).atTime(11 + random.nextInt(5), random.nextInt(60)));
                orderRepo.save(order);
            }
        }
        System.out.println("CanteenQ ready -> " + menu.size() + " dishes, " + people.size()
                + " members, " + orderRepo.count() + " old orders loaded");
    }
}
