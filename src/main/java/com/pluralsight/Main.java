package com.pluralsight;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileWriter;
import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
                    System.out.println("Opening Ledger...");
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

}