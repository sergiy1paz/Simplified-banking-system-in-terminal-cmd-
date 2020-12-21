package banking;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Card {
    final private String NUMBER;
    final private int pin;
    static List<String> numbers;



    public Card() {
        this.NUMBER = checkOriginalsNumber(createNumber());
        this.pin = createPin();
    }

    private String createNumber() {
        Random rand = new Random(System.currentTimeMillis());

        int[] digits = new int[9];
        digits = Arrays.stream(digits).map(e -> e = rand.nextInt(10)).toArray();
        /* Коли перетворюю масив чисел в строку треба позбутись символів коми та квадратних дужок
         * та пробілів між цифрами */
        String str = "400000" + Arrays.toString(digits).replaceAll("[\\[,\\]\\s]", "");

        return String.format(str + "%d", algLuna(str));
    }

    private String checkOriginalsNumber(String number) { // Тут помилка походу
        if (numbers.stream().noneMatch(num -> num.equals(number))) {
            return number;
        } else {
            return checkOriginalsNumber(createNumber());
        }
    }

    static int algLuna(String number) {
        int sum = 0;
        char[] chars = number.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            int digit = Character.getNumericValue(chars[i]);

            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
        }
        return (sum * 9) % 10;
    }

    private int createPin() {
        Random rand = new Random(System.currentTimeMillis());
        return (1000 + rand.nextInt(9000));

    }

    public String getNUMBER() {
        return NUMBER;
    }

    public int getPin() {
        return pin;
    }




}