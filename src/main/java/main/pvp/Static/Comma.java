package main.pvp.Static;

public class Comma {
    public static String Comma(Integer number) {
        String numberWithCommas = String.format("%,d", number);
        return numberWithCommas;
    }
}
