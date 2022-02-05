package org.benp.svngs.data;

import java.util.Arrays;
import java.util.List;

public enum BondDenomination {

    FIFTY(50, "FIFTY", "50", "L", Arrays.asList("FIFTY", "FIETY", "EIETY")),
    SEVENTY_FIVE(75, "SEVENTY", "75", "K", Arrays.asList("SEVENTY", "SEVENTY-FIVE")),
    ONE_HUNDRED(100, "ONE HUNDRED", "100", "C", Arrays.asList("ONE HUNDRED", "ONE HUNIDRED")),
    TWO_HUNDRED(200, "TWO", "200", "R", Arrays.asList("TWO")),
    FIVE_HUNDRED(500, "FIVE HUNDRED", "500", "D", Arrays.asList("FIVE HUNDRED")),
    ONE_THOUSAND(1000, "ONE THOUSAND", "1000", "M", Arrays.asList("ONE THOUSAND"));






    private final int number;

    public int getNumber() {
        return number;
    }

    public String getWord() {
        return word;
    }

    public String getNumberString() {
        return numberString;
    }

    public String getLetter() {
        return letter;
    }

    private final String word;
    private final String numberString;
    private final String letter;

    public List<String> getStringIdentifiers() {
        return stringIdentifiers;
    }

    private final List<String> stringIdentifiers;

    BondDenomination(int number, String word, String numberString, String letter, List<String> stringIdentifiers) {
        this.number = number;
        this.word = word;
        this.numberString = numberString;
        this.letter = letter;
        this.stringIdentifiers = stringIdentifiers;
    }


    public static BondDenomination fromNumberString(String numberString) {
        for (BondDenomination currBondD : BondDenomination.values()) {
            if (currBondD.numberString.equals(numberString)) {
                return currBondD;
            }
        }
        return null;
    }


    public static BondDenomination fromLetter(String letter) {
        for (BondDenomination currBondD : BondDenomination.values()) {
            if (currBondD.letter.equals(letter)) {
                return currBondD;
            }
        }
        return null;
    }

}
