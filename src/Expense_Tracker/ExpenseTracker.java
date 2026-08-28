package Expense_Tracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class ExpenseTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Expense> expenses = ExpenseStorage.loadExpenses();
        Map<String, Double> budgets = BudgetStorage.loadBudgets();

        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("0. Exit");
            System.out.println("1. Add Expense");
            System.out.println("2. Update Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. View All Expenses");
            System.out.println("5. View Summary of All Expenses");
            System.out.println("6. View Summary of Expenses for a Specific Month");
            System.out.println("7. Filter Expenses by Category");
            System.out.println("8. Set/View Monthly Budget");
            System.out.println("9. Export Expenses to CSV");
            System.out.println("10. View Summary Grouped by Category");
            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number from the menu.");
                continue;
            }

            switch (choice) {
                case 0:
                    ExpenseStorage.saveExpenses(expenses);
                    System.out.println("Expenses saved. Exiting...");
                    return;
                case 1:
                    addExpense(scanner, expenses, budgets);
                    break;
                case 2:
                    updateExpense(scanner, expenses, budgets);
                    break;
                case 3:
                    deleteExpense(scanner, expenses);
                    break;
                case 4:
                    viewAllExpenses(expenses);
                    break;
                case 5:
                    viewSummary(expenses);
                    break;
                case 6:
                    viewMonthlySummary(scanner, expenses);
                    break;
                case 7:
                    filterByCategory(scanner, expenses);
                    break;
                case 8:
                    manageBudget(scanner, expenses, budgets);
                    break;
                case 9:
                    exportCsv(expenses);
                    break;
                case 10:
                    viewCategorySummary(expenses);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addExpense(Scanner scanner, ArrayList<Expense> expenses,
                                   Map<String, Double> budgets) {
        String date = readValidDate(scanner, "Enter date (YYYY-MM-DD): ");
        if (date == null) return;
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Expense not added.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero. Expense not added.");
            return;
        }
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        expenses.add(new Expense(date, description, amount, category));
        ExpenseStorage.saveExpenses(expenses);
        System.out.println("Expense added.");
        warnIfOverBudget(expenses, budgets, monthKey(date));
    }

    private static void updateExpense(Scanner scanner, ArrayList<Expense> expenses,
                                      Map<String, Double> budgets) {
        System.out.print("Enter the index of the expense to update: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid index.");
            return;
        }
        if (index >= 0 && index < expenses.size()) {
            String oldMonth = monthKey(expenses.get(index).getDate());
            String date = readValidDate(scanner, "Enter new date (YYYY-MM-DD): ");
            if(date == null) return;
            System.out.print("Enter new description: ");
            String description = scanner.nextLine();
            System.out.print("Enter new amount: ");
            double amount;
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Expense not updated.");
                return;
            }
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero. Expense not updated.");
                return;
            }
            System.out.print("Enter new category: ");
            String category = scanner.nextLine();
            expenses.set(index, new Expense(date, description, amount, category));
            ExpenseStorage.saveExpenses(expenses);
            System.out.println("Expense updated.");
            // An update can change the amount or move the expense to another
            // month, so re-check both the new month and the one it left.
            String newMonth = monthKey(date);
            warnIfOverBudget(expenses, budgets, newMonth);
            if (oldMonth != null && !oldMonth.equals(newMonth)) {
                warnIfOverBudget(expenses, budgets, oldMonth);
            }
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void deleteExpense(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.print("Enter the index of the expense to delete: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid index.");
            return;
        }
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            ExpenseStorage.saveExpenses(expenses);
            System.out.println("Expense deleted.");
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void viewAllExpenses(ArrayList<Expense> expenses) {
        for (int i = 0; i < expenses.size(); i++) {
            System.out.println(i + ": " + expenses.get(i));
        }
    }

    private static void viewSummary(ArrayList<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        System.out.printf("Total expenses: %.2f%n", total);
    }

    private static void viewMonthlySummary(Scanner scanner, ArrayList<Expense> expenses) {
        String month = readValidMonth(scanner, "Enter month (YYYY-MM): ");
        if(month == null) return;
        double total = 0;
        for (Expense e : expenses) {
            if (month.equals(monthKey(e.getDate()))) {
                total += e.getAmount();
            }
        }
        System.out.printf("Total for %s: %.2f%n", month, total);
    }

    /** Lists expenses in a chosen category with their running total (issue #2). */
    private static void filterByCategory(Scanner scanner, ArrayList<Expense> expenses) {
        System.out.print("Enter category to filter by: ");
        String category = scanner.nextLine().trim();
        double total = 0;
        boolean found = false;
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            if (e.getCategory() != null && e.getCategory().equalsIgnoreCase(category)) {
                System.out.println(i + ": " + e);
                total += e.getAmount();
                found = true;
            }
        }
        if (!found) {
            System.out.println("No expenses found in category '" + category + "'.");
        } else {
            System.out.printf("Total for category '%s': %.2f%n", category, total);
        }
    }

    /** Displays all expenses grouped by category with their totals and counts (issue #24). */
    private static void viewCategorySummary(ArrayList<Expense> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }

        Map<String, Double> categoryTotals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<String, Integer> categoryCounts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        double grandTotal = 0;

        for (Expense e : expenses) {
            String category = e.getCategory();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            } else {
                category = category.trim();
            }

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + e.getAmount());
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            grandTotal += e.getAmount();
        }

        System.out.println("\n--- Summary by Category ---");
        for (String cat : categoryTotals.keySet()) {
            double total = categoryTotals.get(cat);
            int count = categoryCounts.get(cat);
            System.out.printf("%s: %.2f (%d expense%s)%n", cat, total, count, count == 1 ? "" : "s");
        }
        System.out.printf("Total expenses: %.2f%n", grandTotal);
    }

    /** Sets and/or views the budget for a month, then reports spend (issue #3). */
    private static void manageBudget(Scanner scanner, ArrayList<Expense> expenses,
                                     Map<String, Double> budgets) {
        String month = readValidMonth(scanner, "Enter month (YYYY-MM): ");
        if (month == null) return;
        System.out.print("Enter budget amount (leave blank to just view): ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                budgets.put(month, amount);
                BudgetStorage.saveBudgets(budgets);
                System.out.println("Budget for " + month + " set to " + amount);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
                return;
            }
        }

        double spent = monthTotal(expenses, month);
        if (budgets.containsKey(month)) {
            double budget = budgets.get(month);
            System.out.printf("Budget for %s: %.2f | Spent: %.2f | Remaining: %.2f%n",
                    month, budget, spent, budget - spent);
            if (spent > budget) {
                System.out.println("You are over budget!");
            }
        } else {
            System.out.printf("No budget set for %s. Spent so far: %.2f%n", month, spent);
        }
    }

    /** Warns if the given month's total has exceeded its budget (issue #3). */
    private static void warnIfOverBudget(ArrayList<Expense> expenses,
                                         Map<String, Double> budgets, String month) {
        if (month == null || !budgets.containsKey(month)) {
            return;
        }
        double total = monthTotal(expenses, month);
        double budget = budgets.get(month);
        if (total > budget) {
            System.out.printf("Warning: expenses for %s total %.2f, exceeding the budget of %.2f.%n",
                    month, total, budget);
        }
    }

    /** Sums expenses whose date falls in the given YYYY-MM month. */
    private static double monthTotal(ArrayList<Expense> expenses, String month) {
        double total = 0;
        for (Expense e : expenses) {
            if (month.equals(monthKey(e.getDate()))) {
                total += e.getAmount();
            }
        }
        return total;
    }

    /** Extracts the YYYY-MM month key from a YYYY-MM-DD date string. */
    private static String monthKey(String date) {
        if (date != null && date.length() >= 7) {
            return date.substring(0, 7);
        }
        return date;
    }

    /** Writes all expenses to a CSV file (issue #4). */
    private static void exportCsv(ArrayList<Expense> expenses) {
        if (ExpenseStorage.exportToCsv(expenses)) {
            System.out.println("Expenses exported to " + ExpenseStorage.EXPORT_FILENAME);
        }
    }
    
    /** Prompts for a valid ISO date (YYYY-MM-DD). Returns null if blank to allow cancellation. */
    private static String readValidDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) return null;
            try {
                LocalDate.parse(input);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    /** Prompts for a valid ISO month (YYYY-MM). Returns null if blank to allow cancellation. */
    private static String readValidMonth(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if(input.isEmpty()) return null;
            try {
                YearMonth.parse(input);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid month format. Please use YYYY-MM.");
            }
        }
    }
}
