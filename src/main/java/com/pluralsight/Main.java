package com.pluralsight;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.format.DateTimeParseException;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Transaction> savedTransactions = loadTransactions();
        System.out.println("Transactions available in Main: " + savedTransactions.size());


        System.out.println("==================================");
        System.out.println("           SKYLEDGER              ");
        System.out.println("  Airline Financial Operations    ");
        System.out.println("==================================");
        System.out.println("Welcome to SkyLedger!");
        System.out.println("Your Airport Financial Tracker");


        String choice = "";

        while (!choice.equals("X")) {

            System.out.println();
            System.out.println("========== MAIN MENU ==========");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.println();

            System.out.print("Enter your choice: ");
            choice = scanner.nextLine().trim().toUpperCase();


            switch (choice) {
                case "D":
                    addDeposit(scanner);
                    break;

                case "P":
                    makePayment(scanner);
                    break;

                case "L":
                    showLedgerMenu(scanner);
                    break;

                case "X":
                    System.out.println("Exiting SkyLedger...");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter D, P, L, or X.");
                    break;
            }


        }
        System.out.println("Thank you for using SkyLedger!");
        scanner.close();

    }

    public static void addDeposit(Scanner scanner) {
        System.out.println();
        System.out.println("========== ADD DEPOSIT ==========");
        System.out.println("Welcome to SkyLedger Deposit Services!");

        System.out.print("Enter deposit description: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor or source: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter deposit amount: $");
        double amount = Double.parseDouble(scanner.nextLine());


        while (amount <= 0) {
            System.out.println("Invalid amount. Deposit must be greater than $0.");
            System.out.print("Enter deposit amount: $");
            amount = Double.parseDouble(scanner.nextLine());
        }

        LocalDate date = LocalDate.now();
        System.out.println("Transaction Date: " + date);

        LocalTime time = LocalTime.now();
        System.out.println("Transaction Time: " + time);


        Transaction transaction = new Transaction(
                date,
                time,
                description,
                vendor,
                amount
        );

        try (FileWriter writer = new FileWriter("transactions.csv", true)) {
            writer.write(date + "|" + time.withNano(0) + "|" +
                    description + "|" + vendor + "|" + amount + "\n");

            System.out.println();
            System.out.println("Deposit saved successfully!");
            System.out.printf("Amount: $%.2f%n", amount);

        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }


    }


    public static void makePayment(Scanner scanner) {
        System.out.println();
        System.out.println("========== MAKE PAYMENT ==========");
        System.out.println("Welcome to SkyLedger Payment Services!");


        System.out.print("Enter payment description: ");
        String description = scanner.nextLine();


        System.out.print("Enter vendor receiving payment: ");
        String vendor = scanner.nextLine();


        System.out.print("Enter payment amount: $");
        double amount = Double.parseDouble(scanner.nextLine());


        while (amount <= 0) {
            System.out.println("Invalid amount. Payment must be greater than $0.");
            System.out.print("Enter payment amount: $");
            amount = Double.parseDouble(scanner.nextLine());
        }

        amount = -amount;

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        System.out.println("Transaction Date: " + date);
        System.out.println("Transaction Time: " + time);


        Transaction transaction = new Transaction(
                date,
                time,
                description,
                vendor,
                amount
        );


        try (FileWriter writer = new FileWriter("transactions.csv", true)) {
            writer.write(date + "|" + time.withNano(0) + "|" +
                    description + "|" + vendor + "|" + amount + "\n");


            System.out.println();
            System.out.println("Payment saved successfully!");
            System.out.printf("Amount Paid: $%.2f%n", -amount);

        } catch (IOException e) {
            System.out.println("Error saving payment: " + e.getMessage());
        }

    }


    public static ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        System.out.println("Loading SkyLedger transactions...");

        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            System.out.println("CSV file opened successfully!");

            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");


                if (parts.length != 5) {
                    System.out.println("Skipping invalid transaction row: " + line);
                    continue;
                }

                try {
                LocalDate date = LocalDate.parse(parts[0]);
                LocalTime time = LocalTime.parse(parts[1]);
                String description = parts[2];
                String vendor = parts[3];
                double amount = Double.parseDouble(parts[4]);


                Transaction transaction = new Transaction(
                        date,
                        time,
                        description,
                        vendor,
                        amount
                );

                transactions.add(transaction);

                } catch (DateTimeParseException | NumberFormatException e) {
                    System.out.println("Skipping invalid transaction data: " + line);
                }


            }

        }
        catch (IOException e) {
            System.out.println("Error opening CSV file: " + e.getMessage());
        }

        System.out.println("Total transactions loaded: " + transactions.size());
        return transactions;
    }


    public static void showLedgerMenu(Scanner scanner) {
        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("========== LEDGER MENU ==========");
            System.out.println("Welcome to SkyLedger Transaction History!");
            System.out.println("A) All Transactions");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "A":
                    System.out.println("Opening All Transactions...");
                    ArrayList<Transaction> transactions = loadTransactions();


                    if (transactions.isEmpty()) {
                        System.out.println("No transactions found.");
                        break;
                    }

                    transactions.sort((t1, t2) -> {
                        int dateCompare = t2.getDate().compareTo(t1.getDate());

                        if (dateCompare != 0) {
                            return dateCompare;
                        }

                        return t2.getTime().compareTo(t1.getTime());
                    });



                    for (Transaction transaction : transactions) {
                        System.out.println(transaction.getDate() + " | " +
                                transaction.getTime() + " | " +
                                transaction.getDescription() + " | " +
                                transaction.getVendor() + " | " +
                                String.format("$%.2f", transaction.getAmount()));

                    }

                    break;


                case "D":
                    System.out.println("Opening Deposits...");
                    ArrayList<Transaction> deposits = loadTransactions();


                    boolean hasDeposits = false;

                    for (Transaction transaction : deposits) {
                        if (transaction.getAmount() > 0) {
                            hasDeposits = true;
                            break;
                        }
                    }

                    if (!hasDeposits) {
                        System.out.println("No deposits found.");
                        break;
                    }



                    deposits.sort((t1, t2) -> {
                        int dateCompare = t2.getDate().compareTo(t1.getDate());

                        if (dateCompare != 0) {
                            return dateCompare;
                        }

                        return t2.getTime().compareTo(t1.getTime());
                    });


                    for (Transaction transaction : deposits) {
                        if (transaction.getAmount() > 0) {
                            System.out.println(transaction.getDate() + " | " +
                                    transaction.getTime() + " | " +
                                    transaction.getDescription() + " | " +
                                    transaction.getVendor() + " | " +
                                    String.format("$%.2f", transaction.getAmount()));
                        }
                    }

                    break;

                case "P":
                    System.out.println("Opening Payments...");
                    ArrayList<Transaction> payments = loadTransactions();


                    boolean hasPayments = false;

                    for (Transaction transaction : payments) {
                        if (transaction.getAmount() < 0) {
                            hasPayments = true;
                            break;
                        }
                    }

                    if (!hasPayments) {
                        System.out.println("No payments found.");
                        break;
                    }



                    payments.sort((t1, t2) -> {
                        int dateCompare = t2.getDate().compareTo(t1.getDate());

                        if (dateCompare != 0) {
                            return dateCompare;
                        }

                        return t2.getTime().compareTo(t1.getTime());
                    });


                    for (Transaction transaction : payments) {
                        if (transaction.getAmount() < 0) {
                            System.out.println(transaction.getDate() + " | " +
                                    transaction.getTime() + " | " +
                                    transaction.getDescription() + " | " +
                                    transaction.getVendor() + " | " +
                                    String.format("$%.2f", transaction.getAmount()));
                        }
                    }

                    break;

                case "R":
                    System.out.println("Opening Reports...");
                    break;

                case "H":
                    System.out.println("Returning to Home Menu...");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice! Please select A, D, P, R, or H.");
                    break;


            }


        }
    }

}