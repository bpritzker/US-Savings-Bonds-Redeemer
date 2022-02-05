package org.benp.svngs;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.benp.svngs.data.BondData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeFiles {

    private static final Logger logger = LoggerFactory.getLogger(MergeFiles.class.getName());


    public static void main(String[] args) {
        MergeFiles MergeFiles = new MergeFiles();
        try {
            MergeFiles.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() throws Exception {

        File bondsListRawFile = new File("C:\\US-Savingbonds\\BondsListRaw.csv");
        RetrieveValue retrieveValue = new RetrieveValue();
        List<BondData> bondData = retrieveValue.loadBondData();

        Map<String, BondData> bondsRawData = new HashMap<>();
        for (BondData currBondData : bondData) {
            bondsRawData.put(currBondData.getSource(), currBondData);
        }

        File bondsRetreieveDataFile = new File("C:\\US-Savingbonds\\BondsRetrieveData.csv");
        Map<String, String> retrieveDataMap = new HashMap<>();
        Reader in = new FileReader(bondsRetreieveDataFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            retrieveDataMap.put(record.get(0), record.get(1));
        }




        File compeltedMergedFile = new File("C:\\US-Savingbonds\\RogerSavingsBondsFile.csv");
        FileWriter fileWriter = new FileWriter(compeltedMergedFile, false);

        try (CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180)) {
            //HEADER record
            csvPrinter.printRecord("Source", "Serial Number", "Denomination", "Series", "Issue Date", "Value on Date of Death");


            for (String currSource : bondsRawData.keySet()) {

                BondData tempBondData = bondsRawData.get(currSource);
                String value = retrieveDataMap.get(currSource);

                csvPrinter.printRecord(
                        currSource,
                        tempBondData.getBondSerialNumber(),
                        tempBondData.getDenomination().getNumber(),
                        tempBondData.getSeries().getStringVal(),
                        tempBondData.getIssueDate().toFormString(),
                        StringUtils.defaultString(value));

            }
        }
    }

}