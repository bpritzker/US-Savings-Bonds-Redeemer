package org.benp.svngs;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.benp.svngs.data.BondData;
import org.benp.svngs.data.BondDenomination;
import org.benp.svngs.data.BondIssueDate;
import org.benp.svngs.data.BondSeries;


import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BondParser {

    static Logger logger = Logger.getLogger(BondParser.class.getName());

    private final Map<String, BondSeries> bondSeriesOverrideMap;

    public BondParser(Map<String, BondSeries> bondSeriesOverrideMap) {
        this.bondSeriesOverrideMap = bondSeriesOverrideMap;
    }


    public BondData parseBondData(List<String> textData, String fileName) throws Exception {

//        if (fileName.contains("Batch-3--scan0002")) {
//            System.out.println("Break");
//        }

        BondSeries  series;



        if (bondSeriesOverrideMap.containsKey(fileName)) {
            series = bondSeriesOverrideMap.get(fileName);
        } else {
            series = getSeries(textData);
        }

        BondDenomination denomination = getDenomination(textData);
//        System.out.println("   Denomination: " + denomination.getNumberString());


        String bondSerialNumber = getBondSerialNumber(textData, denomination, series);
        System.out.println("   Serial Number: " + bondSerialNumber);


        BondIssueDate issueDate = getIssueDate(textData);
        if (! issueDate.isValid()) {
            throw new Exception("Error getting VALID Issue Date " + fileName);
        }

        BondData resultBond = new BondData();
        resultBond.setBondSerialNumber(bondSerialNumber);
        resultBond.setDenomination(denomination);
        resultBond.setIssueDate(issueDate);
        resultBond.setSeries(series);

        return resultBond;

    }












    private BondDenomination getDenomination(List<String> textData) {

        // First check if it's the first couple of rows
        for (int i=2; i < 5; i++) {
            if (BondDenomination.fromNumberString(textData.get(i).trim()) != null) {
                return BondDenomination.fromNumberString(textData.get(i).trim());
            }
        }


        // Next check if any of the words are found
        for (String currLine : textData) {
            for (BondDenomination currBondDemoniation : BondDenomination.values()) {
                for (String currBondIdentifier : currBondDemoniation.getStringIdentifiers()) {
                    if (currLine.contains(currBondIdentifier)) {
                        return currBondDemoniation;
                    }

                }
            }
        }


        String completeSerialNumber = BondParserUtils.getFullSerialNumber(textData);
        if (completeSerialNumber != null) {
            for (BondDenomination currBondDenomination : BondDenomination.values()) {
                if (completeSerialNumber.startsWith(currBondDenomination.getLetter())) {
                    return currBondDenomination;
                }
            }
        }



        logger.warning("   ****** MISSING DENOMINATION  ****** ");
        System.out.println("   ****** MISSING DENOMINATION  ****** ");
        return null;


    }

    private BondSeries getSeries(List<String> textData) throws Exception {

        String stringVal = null;
        for (String currLine : textData) {
            if (currLine.contains("SERIES") && currLine.length() > 7) {
                stringVal = currLine.substring(7);
            }
        }

        BondSeries result =  BondSeries.fromString(stringVal);
        if (result != null) {
            return result;
        }


            String completeSerialNumber = BondParserUtils.getFullSerialNumber(textData);
            if (completeSerialNumber != null) {
                for (BondSeries currBondSeries : BondSeries.values()) {
                    if (completeSerialNumber.endsWith(currBondSeries.getStringVal())) {
                        return currBondSeries;
                    }
                }
            }

        throw new Exception("   Could not find Serial Number!");

    }





    /**
     * First look for a 2 digit number then a 4 digit number on the next line
     */
    protected BondIssueDate getIssueDate(List<String> textData) throws Exception {

        Pattern twoDigitPattern = Pattern.compile("^\\d.?\\d$");
        Pattern fourDigitPattern = Pattern.compile("^\\d\\d\\d\\d$");


        BondIssueDate result = null;

        for (int i=0; i < textData.size(); i++) {
            String currLine = textData.get(i);
            if (fourDigitPattern.matcher(currLine).find()) {

                if (twoDigitPattern.matcher(textData.get(i-1)).find()) {
                    // it's possible to have a space in-between the numbers. This will remove that.
                    String month = textData.get(i-1).replaceAll("\\s+", "");
                    result = new BondIssueDate(month, currLine);
                    return result;
                } else if  (twoDigitPattern.matcher(textData.get(i-2)).find()) {
                    // it's possible to have a space in-between the numbers. This will remove that.
                    String month = textData.get(i-2).replaceAll("\\s+", "");
                    result = new BondIssueDate(month, currLine);
                    return result;
                }
            }
        }

     // If we don't find the issue date, it is still possible to the get from the "Issue Date"
        Pattern issueDatePattern = Pattern.compile("\\d\\d-\\d\\d-\\d\\d");
        for (String currLine : textData) {
            if (issueDatePattern.matcher(currLine).find()) {

                String cleanLine = currLine.replaceAll("[^0-9]", "");


                String month = cleanLine.substring(0,2);
                String year = getYear(cleanLine.substring(4));
                result = new BondIssueDate(month, "19" + year);
            }
        }



        if (result == null) {
            logger.warning("   Issue Date not found!!!");
            throw new Exception("Could not find Issue Date!");

        }

        if (!(NumberUtils.isDigits(result.getMonth()) && NumberUtils.isDigits(result.getYear()))) {
            logger.warning("   Issue Date found but not valid!");
            throw new Exception("Issue Date found but not valid");
        }
        return result;
    }

    private String getYear(String twoDigitYear) {

        int year = Integer.parseInt(twoDigitYear);
        if (year > 25) {
            return "19" + twoDigitYear;
        } else
            return "20" + twoDigitYear;



    }

    private String getBondSerialNumber(List<String> textData, BondDenomination denomination, BondSeries series) throws Exception {

        String partialSerialNumber = getPartialBondSerialNumber(textData);

        if (partialSerialNumber == null) {
            throw new Exception("Could not find PARTIAL serial Number!");
        }

        return denomination.getLetter() + partialSerialNumber + series.toString();
    }


    /**
     * Example: L641897260EE
     *
     * Best way to find serial number is to look for the number sequence most likely to parse.
     * "0213000190001 092807 515" If you find this, the line after will be what you are looking for
     *
     *
     */
    private String getPartialBondSerialNumber(List<String> textData) {

        Pattern pattern1 = Pattern.compile("\\d\\d\\d*. \\d\\d\\d\\d\\d\\d \\d\\d\\d");
        Pattern pattern2 = Pattern.compile("\\d\\d\\d*. \\d\\d\\d\\d\\d\\d \\d\\d");
        String resultPartialSerialNumber = null;
        for (int i=0; i < textData.size(); i++) {
            String currLine = textData.get(i);

            boolean foundPattern1 = pattern1.matcher(currLine).find();
            boolean foundPattern2 = pattern2.matcher(currLine).find();
            if (foundPattern1 || foundPattern2) {

                // Now that we found the pattern the value we are looking for is either above or below this line

                if (NumberUtils.isDigits(textData.get(i-1))) {
                    resultPartialSerialNumber = textData.get(i-1).substring(1);
                    return resultPartialSerialNumber;
                } else if (NumberUtils.isDigits(textData.get(i+1))) {
                    resultPartialSerialNumber = textData.get(i+1).substring(1);
                    return resultPartialSerialNumber;

                } else if (NumberUtils.isDigits(textData.get(i-2))) {
                    resultPartialSerialNumber = textData.get(i-2).substring(1);
                    return resultPartialSerialNumber;

                } else if (NumberUtils.isDigits(textData.get(i+2))) {
                    resultPartialSerialNumber = textData.get(i+2).substring(1);
                    return resultPartialSerialNumber;

                }
            }
        }

        // If that all fails, look for just the number
        Pattern singlePattern = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        for (String currLine : textData) {
            boolean foundSinglePattern = singlePattern.matcher(currLine).find();
            if (foundSinglePattern) {
                return currLine;
            }
        }



        return null;
    }








}