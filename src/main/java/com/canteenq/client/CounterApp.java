package com.canteenq.client;

import com.canteenq.dto.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CounterApp {

    private static final String BASE = "http://localhost:8080/api";

    record Dish(Long id, String name, String category, double price, int stock) {
    }

    record Person(Long id, String name, String role, double balance, String details) {
    }

    private final RestTemplate rest = new RestTemplate();
    private final WebClient web = WebClient.create(BASE);
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new CounterApp().start();
    }

    private void start() {
        System.out.println("=================================");
        System.out.println("      CanteenQ  Billing Counter   ");
        System.out.println("=================================");
        while (true) {
            System.out.println();
            System.out.println("1. Show menu");
            System.out.println("2. Show members");
            System.out.println("3. Place order");
            System.out.println("4. Recharge wallet");
            System.out.println("5. Serve next token");
            System.out.println("6. Notice board");
            System.out.println("7. Give feedback");
            System.out.println("0. Exit");
            try {
                int choice = readInt("Enter choice: ", 0, 7);
                switch (choice) {
                    case 1 -> showMenu();
                    case 2 -> showMembers();
                    case 3 -> placeOrder();
                    case 4 -> recharge();
                    case 5 -> serveNext();
                    case 6 -> notices();
                    case 7 -> feedback();
                    default -> {
                        System.out.println("Thank you, counter closed.");
                        return;
                    }
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (HttpStatusCodeException e) {
                System.out.println("Server refused: " + e.getResponseBodyAsString());
            } catch (WebClientResponseException e) {
                System.out.println("Server refused: " + e.getResponseBodyAsString());
            } catch (ResourceAccessException | WebClientRequestException e) {
                System.out.println("Server not reachable. Start CanteenQApplication first.");
            }
        }
    }

    private void showMenu() {
        Dish[] dishes = rest.getForObject(BASE + "/menu", Dish[].class);
        System.out.printf("%-4s %-18s %-10s %8s %6s%n", "ID", "Dish", "Type", "Price", "Stock");
        for (Dish d : dishes) {
            System.out.printf("%-4d %-18s %-10s %8.2f %6d%n", d.id(), d.name(), d.category(), d.price(), d.stock());
        }
    }

    private void showMembers() {
        Person[] people = rest.getForObject(BASE + "/members", Person[].class);
        List<Double> balances = new ArrayList<>();
        for (Person p : people) {
            System.out.printf("%-5d %-45s Rs %.2f%n", p.id(), p.details(), p.balance());
            balances.add(p.balance());
        }
        double total = 0;
        for (Double b : balances) {
            total += b;
        }
        System.out.printf("Money lying in all wallets: Rs %.2f%n", total);
    }

    private void placeOrder() throws InvalidInputException {
        Long memberId = readLong("Member ID: ");
        List<LineRequest> lines = new ArrayList<>();
        while (true) {
            Long itemId = readLong("Dish ID (0 to finish): ");
            if (itemId == 0) {
                break;
            }
            int qty = readInt("Quantity: ", 1, 20);
            lines.add(new LineRequest(itemId, qty));
        }
        if (lines.isEmpty()) {
            throw new InvalidInputException("no dish selected");
        }
        Map<?, ?> offers = rest.getForObject(BASE + "/offers", Map.class);
        System.out.println("Offers: " + offers.keySet());
        System.out.print("Offer code (Enter to skip): ");
        String offer = sc.nextLine().trim();

        Receipt r = rest.postForObject(BASE + "/orders", new OrderRequest(memberId, offer, lines), Receipt.class);
        System.out.println("------------- RECEIPT -------------");
        System.out.println("Token No : " + r.token());
        System.out.println("Customer : " + r.member());
        r.items().forEach(i -> System.out.println("   " + i));
        System.out.printf("Subtotal : Rs %.2f%nDiscount : Rs %.2f (%s)%nTotal    : Rs %.2f%n",
                r.subtotal(), r.discount(), r.offer(), r.total());
        System.out.println("-----------------------------------");
    }

    private void recharge() throws InvalidInputException {
        Long memberId = readLong("Member ID: ");
        System.out.print("Amount: ");
        String raw = sc.nextLine().trim();
        double amount;
        try {
            amount = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + raw + "' is not a valid amount");
        }
        System.out.print("Confirm recharge of Rs " + amount + "? (y/n): ");
        String answer = sc.nextLine().trim();
        if (answer.isEmpty() || Character.toLowerCase(answer.charAt(0)) != 'y') {
            System.out.println("Recharge cancelled");
            return;
        }
        Person p = rest.postForObject(BASE + "/members/{id}/recharge", new RechargeRequest(amount), Person.class, memberId);
        System.out.printf("Done. %s now has Rs %.2f%n", p.name(), p.balance());
    }

    private void serveNext() {
        Receipt r = rest.postForObject(BASE + "/kitchen/serve", null, Receipt.class);
        System.out.println("Token " + r.token() + " for " + r.member() + " is READY");
    }

    private void notices() {
        String[] list = web.get().uri("/menu/notices").retrieve().bodyToMono(String[].class).block();
        if (list == null || list.length == 0) {
            System.out.println("Notice board is empty");
            return;
        }
        for (String n : list) {
            System.out.println("* " + n);
        }
    }

    private void feedback() {
        System.out.print("Write your feedback: ");
        String text = sc.nextLine();
        FeedbackStats s = web.post().uri("/feedback")
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(FeedbackStats.class)
                .block();
        System.out.println("Words: " + s.words() + ", Vowels: " + s.vowels() + ", Consonants: " + s.consonants()
                + ", Digits: " + s.digits() + ", Special: " + s.specialChars());
        System.out.println("Looks like you are " + s.mood());
    }

    private int readInt(String prompt, int min, int max) throws InvalidInputException {
        System.out.print(prompt);
        String raw = sc.nextLine().trim();
        Integer value;
        try {
            value = Integer.valueOf(raw);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + raw + "' is not a number");
        }
        if (value < min || value > max) {
            throw new InvalidInputException("enter a number from " + min + " to " + max);
        }
        return value;
    }

    private Long readLong(String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String raw = sc.nextLine().trim();
        try {
            long value = Long.parseLong(raw);
            if (value < 0) {
                throw new InvalidInputException("id cannot be negative");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("'" + raw + "' is not a valid id");
        }
    }
}
