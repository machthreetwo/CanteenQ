package com.canteenq.service;

import com.canteenq.model.Category;
import com.canteenq.model.FoodOrder;
import com.canteenq.model.MenuItem;
import com.canteenq.model.OrderLine;
import com.canteenq.repository.FoodOrderRepository;
import com.canteenq.repository.MenuItemRepository;
import com.canteenq.util.SalesMatrix;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final FoodOrderRepository orders;
    private final MenuItemRepository items;

    public ReportService(FoodOrderRepository orders, MenuItemRepository items) {
        this.orders = orders;
        this.items = items;
    }

    public Map<String, Object> summary() {
        List<FoodOrder> all = orders.findAll();
        List<MenuItem> menu = items.findAll();
        Map<String, Object> report = new LinkedHashMap<>();

        double revenue = all.stream().map(FoodOrder::getTotal).reduce(0.0, Double::sum);
        report.put("totalOrders", all.size());
        report.put("totalRevenue", round(revenue));
        report.put("averageMenuPrice", round(menu.stream().mapToDouble(MenuItem::getPrice).average().orElse(0)));

        Map<String, Double> byCategory = all.stream()
                .flatMap(o -> o.getLines().stream())
                .collect(Collectors.groupingBy(l -> l.getItem().getCategory().name(), TreeMap::new,
                        Collectors.summingDouble(OrderLine::getAmount)));
        report.put("revenueByCategory", byCategory);

        Map<String, String> costliest = menu.stream()
                .collect(Collectors.groupingBy(i -> i.getCategory().name(), TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(MenuItem::getPrice)),
                                best -> best.map(MenuItem::toString).orElse("-"))));
        report.put("costliestInCategory", costliest);

        Map<String, Integer> platesSold = all.stream()
                .flatMap(o -> o.getLines().stream())
                .collect(Collectors.groupingBy(l -> l.getItem().getName(), Collectors.summingInt(OrderLine::getQuantity)));
        report.put("topSellers", topN(platesSold.entrySet(), Map.Entry.<String, Integer>comparingByValue().reversed(), 3)
                .stream().map(e -> e.getKey() + " - " + e.getValue() + " plates").toList());

        Map<String, Double> spent = all.stream()
                .collect(Collectors.groupingBy(o -> o.getMember().getName(), Collectors.summingDouble(FoodOrder::getTotal)));
        report.put("bestCustomers", topN(spent.entrySet(), Map.Entry.<String, Double>comparingByValue().reversed(), 3)
                .stream().map(e -> e.getKey() + " - Rs " + round(e.getValue())).toList());

        report.put("vegUnder60", menu.stream()
                .filter(i -> i.getCategory() == Category.VEG && i.getPrice() < 60)
                .sorted(Comparator.comparing(MenuItem::getPrice))
                .map(MenuItem::getName)
                .toList());

        report.put("runningLow", menu.stream()
                .filter(i -> i.getStock() < 10)
                .map(i -> i.getName() + " (" + i.getStock() + " left)")
                .toList());

        Set<String> regulars = new HashSet<>();
        all.forEach(o -> regulars.add(o.getMember().getName()));
        report.put("uniqueCustomers", regulars.size());
        return report;
    }

    public Map<String, Object> fortnight() {
        List<MenuItem> menu = items.findAll(Sort.by("id"));
        LocalDate today = LocalDate.now();
        int[][] thisWeek = salesGrid(menu, today.minusDays(6), today);
        int[][] lastWeek = salesGrid(menu, today.minusDays(13), today.minusDays(7));

        double[][] priceRow = new double[1][menu.size()];
        for (int i = 0; i < menu.size(); i++) {
            priceRow[0][i] = menu.get(i).getPrice();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dishes", menu.stream().map(MenuItem::getName).toList());
        result.put("days", today.minusDays(6) + " to " + today);
        result.put("thisWeek", thisWeek);
        result.put("lastWeek", lastWeek);
        result.put("bothWeeks", SalesMatrix.add(thisWeek, lastWeek));
        result.put("change", SalesMatrix.subtract(thisWeek, lastWeek));
        result.put("dailyRevenueThisWeek", SalesMatrix.multiply(priceRow, thisWeek)[0]);
        return result;
    }

    private int[][] salesGrid(List<MenuItem> menu, LocalDate from, LocalDate to) {
        Map<Long, Integer> row = new HashMap<>();
        for (int i = 0; i < menu.size(); i++) {
            row.put(menu.get(i).getId(), i);
        }
        int[][] grid = new int[menu.size()][7];
        for (FoodOrder order : orders.findByOrderDateBetween(from, to)) {
            int col = (int) ChronoUnit.DAYS.between(from, order.getOrderDate());
            for (OrderLine line : order.getLines()) {
                Integer r = row.get(line.getItem().getId());
                if (r != null) {
                    grid[r][col] += line.getQuantity();
                }
            }
        }
        return grid;
    }

    public static <T> List<T> topN(Collection<? extends T> source, Comparator<? super T> order, int n) {
        List<T> sorted = new ArrayList<>(source);
        sorted.sort(order);
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    private static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }
}
