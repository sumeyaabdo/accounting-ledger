package com.pluralsight;
import java.util.Scanner;

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
                    System.out.println("Opening Add Deposit...");
                    break;

                case "P":
                    System.out.println("Opening Make Payment...");
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
}
