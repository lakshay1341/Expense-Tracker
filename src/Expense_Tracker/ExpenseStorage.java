package Expense_Tracker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseStorage {
    private static final String FILENAME = "expenses.txt";

    public static void saveExpenses(ArrayList<Expense> expenses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            for (Expense e : expenses) {
                writer.println(CsvUtil.toLine(e.getDate(), e.getDescription(),
                        String.valueOf(e.getAmount()), e.getCategory()));
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    public static ArrayList<Expense> loadExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> parts = CsvUtil.parseLine(line);
                if (parts.size() < 4) {
                    System.out.println("Skipping malformed line: " + line);
                    continue;
                }
                try {
                    expenses.add(new Expense(parts.get(0), parts.get(1),
                            Double.parseDouble(parts.get(2)), parts.get(3)));
                } catch (NumberFormatException nfe) {
                    System.out.println("Skipping line with invalid amount: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // No saved expenses yet — start with an empty list.
        } catch (IOException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
        return expenses;
    }
}
