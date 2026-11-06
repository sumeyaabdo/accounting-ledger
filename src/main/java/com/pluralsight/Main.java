package com.pluralsight;
// Import Java classes needed for user input, dates, file handling, and transactions.
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.format.DateTimeParseException;
import java.util.Comparator;


public class Main {
    // ANSI color codes used to style SkyLedger menus and messages.
    public static final String RESET = "\u001B[0m";
    public static final String BURGUNDY = "\u001B[38;5;88m";
    public static final String GOLD = "\u001B[38;5;220m";
    public static final String WHITE = "\u001B[37m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String BOLD = "\u001B[1m";
    public static void main(String[] args) {
        // Read user input from the console.
        Scanner scanner = new Scanner(System.in);

        // Load saved transactions from the CSV file.
        ArrayList<Transaction> savedTransactions = loadTransactions();
        System.out.println("Transactions available in Main: " + savedTransactions.size());

        // Display the SkyLedger welcome banner with gold and white text.
        System.out.println(GOLD + BOLD + "==================================" + RESET);
        System.out.println(WHITE + BOLD + "           SKYLEDGER" + RESET);
        System.out.println(GOLD + "  Airline Financial Operations" + RESET);
        System.out.println(GOLD + BOLD + "==================================" + RESET);
        System.out.println("Welcome to SkyLedger!");
        System.out.println("Your Airport Financial Tracker");

        //Keep showing the Main Meu until the user chooses to exit.
        String choice = "";
        while (!choice.equals("X")) {

            System.out.println();
            System.out.println(GOLD + BOLD + "\n========== MAIN MENU ==========" + RESET);
            System.out.println(WHITE + "D) Add Deposit" + RESET);
            System.out.println(WHITE + "P) Make Payment" + RESET);
            System.out.println(WHITE + "L) Ledger" + RESET);
            System.out.println(WHITE + "X) Exit" + RESET);
            System.out.println();

            System.out.print("Enter your choice: ");
            // Read the user's choice and convert it to uppercase.
            choice = scanner.nextLine().trim().toUpperCase();

            // Run the selected menu option or show an error for invalid input.
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
        // Display the goodbye message and close the scanner.
        System.out.println("Thank you for using SkyLedger!");
        scanner.close();
    }

    // Get deposit information from the user and save it.
    public static void addDeposit(Scanner scanner) {
        System.out.println();
        System.out.println(GOLD + BOLD + "\n========== ADD DEPOSIT ==========" + RESET);
        System.out.println("Welcome to SkyLedger Deposit Services!");

        //Ask the user for the deposit description, vendor, and amount.
        System.out.print("Enter deposit description: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor or source: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter deposit amount: $");
        double amount = Double.parseDouble(scanner.nextLine());

        //Make sure the deposit amount is greater than zero
        while (amount <= 0) {
            System.out.println("Invalid amount. Deposit must be greater than $0.");
            System.out.print("Enter deposit amount: $");
            amount = Double.parseDouble(scanner.nextLine());
        }
        //Get the current date and time for the transaction
        LocalDate date = LocalDate.now();
        System.out.println("Transaction Date: " + date);

        LocalTime time = LocalTime.now();
        System.out.println("Transaction Time: " + time);

        // Create a new transaction using the deposit information.
        Transaction transaction = new Transaction(
                date,
                time,
                description,
                vendor,
                amount
        );
        // Save the deposit to the CSV file and handle any file errors.
        try (FileWriter writer = new FileWriter("transactions.csv", true)) {
            writer.write(date + "|" + time.withNano(0) + "|" +
                    description + "|" + vendor + "|" + amount + "\n");

            // Show a success message or an error if saving fails.
            System.out.println();
            System.out.println(GREEN + BOLD + "✓ Deposit added successfully!" + RESET);
            System.out.printf("Amount: $%.2f%n", amount);

        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    // get payment information from the user and save it.
    public static void makePayment(Scanner scanner) {
        System.out.println();
        System.out.println(GOLD + BOLD + "\n========== MAKE PAYMENT ==========" + RESET);
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
        // Covert the payment to a negative amount because money is going out.
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
            System.out.println(RED + BOLD + "✓ Payment added successfully!" + RESET);
            System.out.printf("Amount Paid: $%.2f%n", -amount);

        } catch (IOException e) {
            System.out.println("Error saving payment: " + e.getMessage());
        }
    }

    // Load saved transactions from the CSV file into a list.
    public static ArrayList<Transaction> loadTransactions() {
        // Create an empty list to store transactions.
        ArrayList<Transaction> transactions = new ArrayList<>();

        System.out.println("Loading SkyLedger transactions...");
        // Open the CSV file and read the saved transactions.
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.csv"))) {
            System.out.println("CSV file opened successfully!");

            String line;
            // Skip the header row in the CSV file.
            reader.readLine();
           // Read each transaction row until the end of the file.
            while ((line = reader.readLine()) != null) {
                // Split the row into date, time, description, vendor, and amount.
                String[] parts = line.split("\\|");

                // Skip rows that do not have all five fields.
                if (parts.length != 5) {
                    System.out.println("Skipping invalid transaction row: " + line);
                    continue;
                }

                // Convert the saved text into the correct data types.
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

                    // Add the transaction to the list.
                    transactions.add(transaction);

                    // Skip transactions with invalid dates, times, or amounts.
                } catch (DateTimeParseException | NumberFormatException e) {
                    System.out.println("Skipping invalid transaction data: " + line);
                }
            }
         // Display an error if the CSV file cannot be read.
        } catch (IOException e) {
            System.out.println("Error opening CSV file: " + e.getMessage());
        }
        // Display the number of transactions loaded and return the list.
        System.out.println("Total transactions loaded: " + transactions.size());
        return transactions;
    }

    // Display the ledger menu and let users view their transaction history.
    public static void showLedgerMenu(Scanner scanner) {
        // Keep the ledger menu running until the user returns home.
        boolean running = true;

        while (running) {
           // Display the ledger menu options.
            System.out.println();
            System.out.println(GOLD + BOLD + "\n========== LEDGER MENU ==========" + RESET);
            System.out.println("Welcome to SkyLedger Transaction History!");
            System.out.println(WHITE + "A) All Transactions" + RESET);
            System.out.println(WHITE + "D) Deposits" + RESET);
            System.out.println(WHITE + "P) Payments" + RESET);
            System.out.println(WHITE + "R) Reports" + RESET);
            System.out.println(WHITE + "H) Home" + RESET);

            // Read the user's choice and convert it to uppercase.
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine().trim().toUpperCase();

            // Open the selected ledger option.
            switch (choice) {
                // Load and display all saved transactions.
                case "A":
                    System.out.println("Opening All Transactions...");
                    ArrayList<Transaction> transactions = loadTransactions();

                    if (transactions.isEmpty()) {
                        System.out.println("No transactions found.");
                        break;
                    }
                    // Sort transactions by date and time, newest first.
                    transactions.sort((t1, t2) -> {
                        int dateCompare = t2.getDate().compareTo(t1.getDate());

                        if (dateCompare != 0) {
                            return dateCompare;
                        }
                        return t2.getTime().compareTo(t1.getTime());
                    });
                    // Display each transaction with its date, time, description, vendor, and amount.
                    for (Transaction transaction : transactions) {
                        System.out.println(transaction.getDate() + " | " +
                                transaction.getTime() + " | " +
                                transaction.getDescription() + " | " +
                                transaction.getVendor() + " | " +
                                String.format("$%.2f", transaction.getAmount()));
                    }
                    break;

                    // Load transactions and display only deposits.
                case "D":
                    System.out.println("Opening Deposits...");
                    ArrayList<Transaction> deposits = loadTransactions();
                    // Check if there are any transactions with positive amounts.
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
                    // Sort deposits from newest to oldest.
                    deposits.sort((t1, t2) -> {
                        int dateCompare = t2.getDate().compareTo(t1.getDate());

                        if (dateCompare != 0) {
                            return dateCompare;
                        }
                        return t2.getTime().compareTo(t1.getTime());
                    });
                    // Display only transactions with positive amounts.
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

                // Load transactions and display only payments.
                case "P":
                    System.out.println("Opening Payments...");
                    ArrayList<Transaction> payments = loadTransactions();
                  // Check if there are any transactions with negative amounts.
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
                    // Sort payments from newest to oldest.
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

              // Open the reports menu.
                case "R":
                    showReportsMenu(scanner);
                    break;

                // Stop the ledger menu and return to the home menu.
                case "H":
                    System.out.println("Returning to Home Menu...");
                    running = false;
                    break;

                // Show an error message for an invalid menu choice.
                default:
                    System.out.println("Invalid choice! Please select A, D, P, R, or H.");
                    break;
            }
        }
    }

    // Display the financial reports menu and handle user selections.
    public static void showReportsMenu(Scanner scanner) {
        // Store the user's menu choice.
        String choice = "";

       // Keep showing the reports menu until the user selects 0.
        while (!choice.equals("0")) {
            // Display all available financial reports and search options.
            System.out.println();
            System.out.println(GOLD + BOLD + "\n========== REPORTS MENU ==========" + RESET);
            System.out.println("Welcome to SkyLedger Financial Reports!");
            System.out.println(WHITE + "1) Month To Date" + RESET);
            System.out.println(WHITE + "2) Previous Month" + RESET);
            System.out.println(WHITE + "3) Year To Date" + RESET);
            System.out.println(WHITE + "4) Previous Year" + RESET);
            System.out.println(WHITE + "5) Search by Vendor" + RESET);
            System.out.println(WHITE + "6) Custom Search" + RESET);
            System.out.println(WHITE + "0) Back" + RESET);

            // Read the user's choice and remove extra spaces.
            System.out.print("\nEnter your choice: ");
            choice = scanner.nextLine().trim();

          // Run the report or search selected by the user.
            switch (choice) {
                case "1":
                    showMonthToDateReport();
                    break;
                case "2":
                    showPreviousMonthReport();
                    break;
                case "3":
                    showYearToDateReport();
                    break;
                case "4":
                    showPreviousYearReport();
                    break;
                case "5":
                    searchByVendor(scanner);
                    break;
                case "6":
                    customSearch(scanner);
                    break;
                case "0":
                    System.out.println("Returning to Ledger Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Display transactions from the first day of the current month to today.
    public static void showMonthToDateReport() {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== MONTH TO DATE REPORT ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Monthly Financial Activity" + RESET);

        // Get today's date and the first day of the current month.
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        System.out.println("Start Date: " + firstDayOfMonth);
        System.out.println("Today's Date: " + today);

        // Load all saved transactions from the CSV file.
        ArrayList<Transaction> transactions = loadTransactions();
        System.out.println("Transactions loaded for report: " + transactions.size());

        // Create a list to store transactions for the current month.
        ArrayList<Transaction> monthlyTransactions = new ArrayList<>();

        // Find transactions between the first day of the month and today.
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            if (!transactionDate.isBefore(firstDayOfMonth)
                    && !transactionDate.isAfter(today)) {
                monthlyTransactions.add(transaction);
            }
        }
        System.out.println("Month To Date transactions: " + monthlyTransactions.size());

        // Sort monthly transactions by date and time, newest first.
        monthlyTransactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );
        // Display a message if no transactions are found.
        if (monthlyTransactions.isEmpty()) {
            System.out.println("No transactions found for this month.");
        } else {
            // Display each matching transaction with its details and amount.
            for (Transaction transaction : monthlyTransactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void showPreviousMonthReport() {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== PREVIOUS MONTH REPORT ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Previous Month Financial Activity" + RESET);

        // Get today's date
        LocalDate today = LocalDate.now();
        // Find the first day of the previous month
        LocalDate firstDayOfPreviousMonth = today.minusMonths(1).withDayOfMonth(1);
        // Find the last day of the previous month
        LocalDate lastDayOfPreviousMonth = today.withDayOfMonth(1).minusDays(1);


        System.out.println("Start Date: " + firstDayOfPreviousMonth);
        System.out.println("End Date: " + lastDayOfPreviousMonth);

        // Load all transactions from the CSV file
        ArrayList<Transaction> transactions = loadTransactions();
        System.out.println("Transactions loaded for report: " + transactions.size());

        // Create a list to store transactions from the previous month
        ArrayList<Transaction> previousMonthTransactions = new ArrayList<>();

        // Check each transaction to see if it belongs to the previous month
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            // Add transactions that fall within the previous month
            if (!transactionDate.isBefore(firstDayOfPreviousMonth)
                    && !transactionDate.isAfter(lastDayOfPreviousMonth)) {
                previousMonthTransactions.add(transaction);
            }
        }
        System.out.println("Previous Month transactions: " + previousMonthTransactions.size());

        previousMonthTransactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );

        System.out.println("Previous Month transactions sorted newest first.");

        if (previousMonthTransactions.isEmpty()) {
            System.out.println("No transactions found for the previous month.");
        } else {
            for (Transaction transaction : previousMonthTransactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void showYearToDateReport() {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== YEAR TO DATE REPORT ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Yearly Financial Activity" + RESET);

        // Get today's date
        LocalDate today = LocalDate.now();
        // Find the first day of the current year
        LocalDate firstDayOfYear = today.withDayOfYear(1);

        System.out.println("Start Date: " + firstDayOfYear);
        System.out.println("Today's Date: " + today);

        // Load all transactions from the CSV file
        ArrayList<Transaction> transactions = loadTransactions();
        System.out.println("Transactions loaded for report: " + transactions.size());

        // Create a list for this year's transactions
        ArrayList<Transaction> yearToDateTransactions = new ArrayList<>();

        // Check each transaction's date
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            // Include transactions from January 1 through today
            if (!transactionDate.isBefore(firstDayOfYear)
                    && !transactionDate.isAfter(today)) {
                yearToDateTransactions.add(transaction);
            }
        }

        System.out.println("Year To Date transactions: " + yearToDateTransactions.size());

        yearToDateTransactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );

        System.out.println("Year To Date transactions sorted newest first.");

        if (yearToDateTransactions.isEmpty()) {
            System.out.println("No transactions found for this year.");
        } else {
            for (Transaction transaction : yearToDateTransactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void showPreviousYearReport() {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== PREVIOUS YEAR REPORT ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Previous Year Financial Activity" + RESET);


        LocalDate today = LocalDate.now();

        LocalDate firstDayOfPreviousYear = today.minusYears(1).withDayOfYear(1);
        LocalDate lastDayOfPreviousYear = today.withDayOfYear(1).minusDays(1);

        System.out.println("Start Date: " + firstDayOfPreviousYear);
        System.out.println("End Date: " + lastDayOfPreviousYear);

        ArrayList<Transaction> transactions = loadTransactions();
        System.out.println("Transactions loaded for report: " + transactions.size());

        ArrayList<Transaction> previousYearTransactions = new ArrayList<>();

        // Go through each transaction
        for (Transaction transaction : transactions) {
            LocalDate transactionDate = transaction.getDate();

            // Check if the transaction happened during the previous year
            if (!transactionDate.isBefore(firstDayOfPreviousYear)
                    && !transactionDate.isAfter(lastDayOfPreviousYear)) {
                previousYearTransactions.add(transaction);
            }
        }

        System.out.println("Previous Year transactions: " + previousYearTransactions.size());

        previousYearTransactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );

        System.out.println("Previous Year transactions sorted newest first.");

        if (previousYearTransactions.isEmpty()) {
            System.out.println("No transactions found for the previous year.");
        } else {
            for (Transaction transaction : previousYearTransactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void searchByVendor(Scanner scanner) {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== SEARCH BY VENDOR ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Vendor Transaction Search" + RESET);
        System.out.print("Enter vendor name: ");
        String vendorName = scanner.nextLine().trim();

        ArrayList<Transaction> transactions = loadTransactions();
        System.out.println("Transactions loaded for vendor search: " + transactions.size());

        // Create an empty list to store matching transactions
        ArrayList<Transaction> matchingTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendorName)) {
                matchingTransactions.add(transaction);
            }
        }
        System.out.println("Matching vendor transactions: " + matchingTransactions.size());

        matchingTransactions.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );
        System.out.println("Matching vendor transactions sorted newest first.");

        if (matchingTransactions.isEmpty()) {
            System.out.println("No transactions found for this vendor.");
        } else {
            for (Transaction transaction : matchingTransactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void customSearch(Scanner scanner) {
        System.out.println();
        System.out.println(GOLD + BOLD + "========== CUSTOM SEARCH ==========" + RESET);
        System.out.println(WHITE + "SkyLedger Advanced Transaction Search" + RESET);

        // Ask for a start date or allow the user to skip
        System.out.print("Enter start date (YYYY-MM-DD) or press Enter to skip: ");
        String startDateInput = scanner.nextLine().trim();

        // Ask for an end date or allow the user to skip
        System.out.print("Enter end date (YYYY-MM-DD) or press Enter to skip: ");
        String endDateInput = scanner.nextLine().trim();

        // Ask for a transaction description
        System.out.print("Enter description or press Enter to skip: ");
        String descriptionInput = scanner.nextLine().trim();

        // Ask for a vendor name
        System.out.print("Enter vendor or press Enter to skip: ");
        String vendorInput = scanner.nextLine().trim();

        // Ask for a transaction amount
        System.out.print("Enter amount or press Enter to skip: ");
        String amountInput = scanner.nextLine().trim();

        // Load all transactions from the CSV file
        ArrayList<Transaction> transactions = loadTransactions();

        System.out.println("Transactions available for search: " + transactions.size());

        // Remove transactions that happened before the start date
        if (!startDateInput.isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateInput);

                transactions.removeIf(transaction ->
                        transaction.getDate().isBefore(startDate));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid start date. Please use YYYY-MM-DD.");
                return;
            }
        }
        System.out.println("Transactions after start date filter: " + transactions.size());

       // Remove transactions that happened after the end date
        if (!endDateInput.isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(endDateInput);

                transactions.removeIf(transaction ->
                        transaction.getDate().isAfter(endDate));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid end date. Please use YYYY-MM-DD.");
                return;
            }
        }
        System.out.println("Transactions after end date filter: " + transactions.size());

        // Keep transactions that contain the entered description
        if (!descriptionInput.isEmpty()) {
            transactions.removeIf(transaction ->
                    !transaction.getDescription().toLowerCase()
                            .contains(descriptionInput.toLowerCase()));
        }
        System.out.println("Transactions after description filter: " + transactions.size());

        // Keep transactions that contain the entered vendor name
        if (!vendorInput.isEmpty()) {
            transactions.removeIf(transaction ->
                    !transaction.getVendor().toLowerCase()
                            .contains(vendorInput.toLowerCase()));
        }
        System.out.println("Transactions after vendor filter: " + transactions.size());

        // Keep transactions that match the entered amount
        if (!amountInput.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountInput);

                transactions.removeIf(transaction ->
                        Double.compare(transaction.getAmount(), amount) != 0);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
                return;
            }
        }
        System.out.println("Transactions after amount filter: " + transactions.size());

        System.out.println();
        System.out.println(GOLD + BOLD + "========== SEARCH RESULTS ==========" + RESET);

        // Check if any transactions match the search filters
        if (transactions.isEmpty()) {
            System.out.println("No matching transactions found.");
        } else {
            transactions.sort(Comparator.comparing(Transaction::getDate)
                    .thenComparing(Transaction::getTime).reversed());

            for (Transaction transaction : transactions) {
                System.out.printf("%s | %s | %s | %s | $%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }
}
