package banking;

public enum Menu {
    OUT("1. Create an account\n" +
            "2. Log into account\n" +
            "0. Exit"),
    IN("1. Balance\n" +
            "2. Add income\n" +
            "3. Do transfer\n" +
            "4. Close account\n" +
            "5. Log out\n" +
            "0. Exit");

    private String text;

    Menu(String text) {
        this.text = text;
    }

    public void getText() {
        System.out.println(text);
    }
}