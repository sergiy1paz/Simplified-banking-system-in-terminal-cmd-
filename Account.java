package banking;

import java.util.Scanner;

class Account {
    final private DataBase db;
    Scanner s;


    public Account(String url) {
        this.db = new DataBase(url);
        this.s = new Scanner(System.in);
        showMenu();
    }

    private void showMenu() {
        Menu.OUT.getText();
        int action = s.nextInt();

        switch (action) {
            case 1:
                createAccount();
                showMenu();
                break;

            case 2:
                logIn();
                //showMenu();
                break;

            default:
                exit();

        }
    }

    private void createAccount() {
        Card.numbers = db.selectNumbers();
        Card card = new Card();
        db.insert(card.getNUMBER(), card.getPin());
        System.out.println("Your card has been created");
        System.out.println("Your card number:\n" + card.getNUMBER());
        System.out.println("Your card PIN:\n" + card.getPin());
    }

    private void logIn() {
        System.out.println("Enter your card number:");
        String number = s.next();
        System.out.println("Enter your PIN:");
        int pin = s.nextInt();

        if (db.selectLog(number, pin)) {
            System.out.println("You have successfully logged in!");
            loggedMenu(number);
        } else {
            System.out.println("Wrong number or PIN!");
            showMenu();
        }
    }

    private void loggedMenu(String number) {
        Menu.IN.getText();
        int action = s.nextInt();

        switch (action) {
            case 1:
                getBalance(number);
                loggedMenu(number);
                break;

            case 2:
                addIncome(number);
                loggedMenu(number);
                break;

            case 3:
                doTransfer(number);
                loggedMenu(number);
                break;

            case 4:
                closeAccount(number);
                loggedMenu(number);
                break;

            case 5:
                System.out.println("You successfully logged out!");
                showMenu();
                break;

            default:
                exit();

        }
    }

    private void getBalance(String number) {
        System.out.println("Balance: " + db.selectBalance(number));
    }

    private void addIncome(String number) {
        System.out.println("Enter income:");
        long income = s.nextLong();
        db.updateBalance(number, income);
        System.out.println("Income was added!");
    }

    private void doTransfer(String number) {
        System.out.println("Transfer\n" + "Enter your card number:");
        String numberForTransfer = s.next();

        if (checkMistake(numberForTransfer)) {
            System.out.println("Probably you made mistake in the card number. Please try again!");
        } else if (number.equals(numberForTransfer)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (db.selectNumbers().stream().noneMatch(num -> num.equals(numberForTransfer))) { //checking on alg luna
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int money = s.nextInt();

            if (money > db.selectBalance(number)) {
                System.out.println("Not enough money!");
            } else {
                db.transfer(number, numberForTransfer, money);
                System.out.println("Success!");
            }
        }

    }

    private boolean checkMistake(String numberForTransfer) { //alg Luna
        if (numberForTransfer.length() < 16) {
            return true;
        }

        int lastDigit = Character.getNumericValue(numberForTransfer.charAt(numberForTransfer.length() - 1));
        String numberWithoutLastDigit = numberForTransfer.substring(0, numberForTransfer.length() - 1);
        if (lastDigit != Card.algLuna(numberWithoutLastDigit)) {
            return true;
        }

        return false;
    }

    private void closeAccount(String number) {
        db.deleteCard(number);
        System.out.println("The account has been closed!");
    }



    private void exit() {
        System.out.println("Bye!");
        System.exit(0);
    }




}