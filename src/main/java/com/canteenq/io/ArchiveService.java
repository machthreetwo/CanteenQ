package com.canteenq.io;

import com.canteenq.exception.NotFoundException;
import com.canteenq.repository.FoodOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArchiveService {

    private final FoodOrderRepository orders;
    private final Path folder;

    public ArchiveService(FoodOrderRepository orders, @Value("${canteen.archive-dir:archive}") String folder) {
        this.orders = orders;
        this.folder = Paths.get(folder);
    }

    @Transactional(readOnly = true)
    public int exportBills() throws IOException {
        ArrayList<Bill> bills = orders.findAll().stream()
                .map(Bill::of)
                .collect(Collectors.toCollection(ArrayList::new));
        Files.createDirectories(folder);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(billsFile()))) {
            out.writeObject(bills);
        }
        writeDaySheet(bills);
        return bills.size();
    }

    @SuppressWarnings("unchecked")
    public List<Bill> importBills() throws IOException, ClassNotFoundException {
        File file = billsFile();
        if (!file.exists()) {
            throw new FileNotFoundException("Nothing archived yet, export the bills first");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Bill>) in.readObject();
        }
    }

    public Bill reprint(Long orderId) throws IOException, ClassNotFoundException {
        return importBills().stream()
                .filter(b -> b.getOrderId().equals(orderId))
                .findFirst()
                .map(Bill::duplicate)
                .orElseThrow(() -> new NotFoundException("Bill of order " + orderId + " is not in the archive"));
    }

    public long backup() throws IOException {
        File source = billsFile();
        if (!source.exists()) {
            throw new FileNotFoundException("Nothing to back up, export the bills first");
        }
        long copied = 0;
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(folder.resolve("bills-backup.ser").toFile())) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                copied += read;
            }
        }
        return copied;
    }

    public String readDaySheet() throws IOException {
        File sheet = folder.resolve("day-sheet.txt").toFile();
        if (!sheet.exists()) {
            throw new FileNotFoundException("Day sheet not created yet, export the bills first");
        }
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(sheet))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append(System.lineSeparator());
            }
        }
        return text.toString();
    }

    private void writeDaySheet(List<Bill> bills) throws IOException {
        File sheet = folder.resolve("day-sheet.txt").toFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sheet))) {
            writer.write("CanteenQ Bill Register  (generated " + LocalDateTime.now().withNano(0) + ")");
            writer.newLine();
            writer.write("-".repeat(90));
            writer.newLine();
            for (Bill bill : bills) {
                writer.write(bill.toString());
                writer.newLine();
            }
            writer.write("-".repeat(90));
            writer.newLine();
            double sum = bills.stream().mapToDouble(Bill::getTotal).sum();
            writer.write(String.format("Total bills: %d    Total amount: Rs %.2f", bills.size(), sum));
            writer.newLine();
        }
    }

    private File billsFile() {
        return folder.resolve("bills.ser").toFile();
    }
}
