package org.benp.svngs;


import org.benp.svngs.data.BondData;
import org.benp.svngs.data.BondDenomination;
import org.benp.svngs.data.BondIssueDate;
import org.benp.svngs.data.BondSeries;

import java.util.List;
import java.util.regex.Pattern;

public class BondParserUtils {

    public static BondSeries getBondSeriesFromFullSerialNumber(String fullSerialNumber) {

        if (fullSerialNumber.endsWith("EE")) {
            return BondSeries.EE;
        } else if (fullSerialNumber.endsWith("I")) {
            return BondSeries.I;
        }

        return null;
    }

    public static BondDenomination getBondDenominationFromFullSerialNumber(String fullSerialNumber) {
        String bondDenominatonChar = fullSerialNumber.substring(0,1);
        return BondDenomination.fromLetter(bondDenominatonChar);
    }



    public static String getFullSerialNumber(List<String> textData) {


        // First build a list of all the denominations for the regex
        String bondDenominations = "";
        for (BondDenomination currBondDenomination : BondDenomination.values()) {
            bondDenominations += currBondDenomination.getLetter();
        }

        for (BondSeries currSeries : BondSeries.values()) {
            Pattern matchPattern = Pattern.compile("[" + bondDenominations + "]\\d\\d\\d\\d\\d\\d\\d\\d*\\d" + currSeries.getStringVal());
            for (String currLine : textData) {
                if (matchPattern.matcher(currLine).find()) {
                    return currLine;
                }
            }
        }

        return null;

    }



    public static BondData fromSerialNumber(String bondSerialNumber, BondIssueDate issueDate, String fileName) {

        BondData resultBondData = new BondData();
        resultBondData.setBondSerialNumber(bondSerialNumber);
        resultBondData.setIssueDate(issueDate);

        BondDenomination denomination = BondParserUtils.getBondDenominationFromFullSerialNumber(bondSerialNumber);
        resultBondData.setDenomination(denomination);

        BondSeries series = BondParserUtils.getBondSeriesFromFullSerialNumber(bondSerialNumber);
        resultBondData.setSeries(series);
        resultBondData.setSource(fileName);

        return resultBondData;

    }


}