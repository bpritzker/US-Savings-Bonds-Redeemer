package org.benp.svngs;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.benp.svngs.data.BondData;
import org.benp.svngs.data.BondDenomination;
import org.benp.svngs.data.BondIssueDate;
import org.benp.svngs.data.BondSeries;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SavingsBondTranslate {

    static Logger logger = Logger.getLogger(SavingsBondTranslate.class.getName());
//    private static final Logger logger = LoggerFactory.getLogger(SavingsBondTranslate.class.getName());


    public static void main(String[] args) {
        SavingsBondTranslate SavingsBondTranslate = new SavingsBondTranslate();
        try {
            SavingsBondTranslate.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() throws Exception {

        Set<String> allDataTextFiles = listAllDataTextFiles();

        String fileNameToTest = null;
//        String fileNameToTest = "Batch-2--scan0037.txt";
//        String fileNameToTest = "Batch-3--scan0002.txt";
//        String fileNameToTest = "scan0032";
//        String fileNameToTest = "scan0101";
//        String fileNameToTest = "scan0077";
//        String fileNameToTest = "scan0081";
//        String fileNameToTest = null;
//        String fileNameToTest = null;
//        String fileNameToTest = null;
//        String fileNameToTest = null;




        List<BondData> sBonds = new ArrayList<>();

        BondParser bondParser = new BondParser(bondSeriesOverride());

        int counter = 0;
        for (String currDataTextFile : allDataTextFiles) {
            counter++;


            // First check if one of the Testing files!
            if ((fileNameToTest == null || currDataTextFile.contains(fileNameToTest) ) && (! isExclude(currDataTextFile))) {

                String fileNameKey = FilenameUtils.getName(currDataTextFile);

                if (manualOverrideBonds().containsKey(fileNameKey)) {
                    sBonds.add(manualOverrideBonds().get(fileNameKey));
                } else if (manualOverrideBonds().containsKey(FilenameUtils.getBaseName(fileNameKey))) {
                    sBonds.add(manualOverrideBonds().get(FilenameUtils.getBaseName(fileNameKey)));
                } else {
                    String fileName = FilenameUtils.getName(currDataTextFile);
                    System.out.println("Processing File: " + fileName + "  " + counter + " of " + allDataTextFiles.size());
                    List<String> fileData = loadStringData(currDataTextFile);
                    BondData tempBond = bondParser.parseBondData(fileData, fileName);
                    tempBond.setSource(FilenameUtils.getBaseName(currDataTextFile));
                    if (tempBond == null) {
                        logger.warning("null Bond!");
                    } else {
                        sBonds.add(tempBond);
                    }
                }
            }


        }

        logger.info("All Files Loaded. Creating CVS file...");
        buildCsvFile(sBonds);




    }

    private boolean isExclude(String currDataTextFile) {
        for (String currExcludeName : excludeFiles()) {
            if (currDataTextFile.contains(currExcludeName)) {
                return true;
            }
        }
        return false;
    }

    private void buildCsvFile(List<BondData> sBonds) throws Exception {

        FileWriter fileWriter = new FileWriter("C:\\US-Savingbonds\\BondsListRaw.csv", false);
        try (CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180)) {
            //HEADER record
            csvPrinter.printRecord("Source", "Series", "Denomination", "Bond Serial Number", "Issue Date Month", "Issue Data Year");

            for (BondData currBond : sBonds) {
                //DATA records
                if (currBond == null || currBond.getDenomination() == null || currBond.getSeries() == null) {
                    System.out.println("\n\n");
                    System.out.println("Error with Bond: \n     " + currBond.toString());
                    System.out.println("\n\n");
                } else {
                    csvPrinter.printRecord(currBond.getSource(), currBond.getSeries(),
                            currBond.getDenomination().getNumber(), currBond.getBondSerialNumber(),
                            currBond.getIssueDate().getMonth(),
                            currBond.getIssueDate().getYear());
                    csvPrinter.flush();
                }
            }

        }

        fileWriter.close();

    }

    private List<String> loadStringData(String dataTextFile) throws IOException {

        List<String> resultData = null;
        try (Stream<String> lines = Files.lines(Paths.get(dataTextFile))) {
            resultData = lines.collect(Collectors.toList());
        }
        return resultData;
    }

    private Set<String> listAllDataTextFiles() throws IOException {

        String dir = "C:\\US-Savingbonds\\SingleFileText";
        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.toString());
                }
            }
        }
        return fileList;

    }






    private List<String> excludeFiles() {
        List<String> result = new ArrayList<>();
        result.add("Batch-3--scan0025");
        result.add("Batch-1--scan0100.txt");
        result.add("XXXXXXXXXX");
        result.add("XXXXXXXXXX");



        return result;
    }


    private Map<String, BondSeries> bondSeriesOverride() {
        Map<String, BondSeries> result = new HashMap<>();
        result.put("Batch-1--scan0051.txt", BondSeries.I);
        result.put("Batch-1--scan0030.txt", BondSeries.I);
        result.put("XXXXXXXXXXXXXXXXXXXXXXXXX", BondSeries.I);
        result.put("XXXXXXXXXXXXXXXXXXXXXXXXX", BondSeries.I);


        return result;
    }


    /**
     * Use this for Bonds that are just too hard to parse!
     * @return
     */
    private Map<String, BondData> manualOverrideBonds() {

        Map<String, BondData> resultMap = new HashMap<>();

        // most of these are serial number issues
        resultMap.put("Batch-1--scan0029.txt", BondParserUtils.fromSerialNumber("K111111111I", new BondIssueDate("04", "2009"), "Batch-1--scan0029.txt"));

        resultMap.put("XXXXXXXXXXXXXXXXXXXXX", BondParserUtils.fromSerialNumber("XXXXXXXXXXXX", new BondIssueDate("01", "1111"), "XXXXXXXX"));
        resultMap.put("XXXXXXXXXXXXXXXXXXXXX", BondParserUtils.fromSerialNumber("XXXXXXXXXXXX", new BondIssueDate("01", "1111"), "XXXXXXXX"));
        resultMap.put("XXXXXXXXXXXXXXXXXXXXX", BondParserUtils.fromSerialNumber("XXXXXXXXXXXX", new BondIssueDate("01", "1111"), "XXXXXXXX"));



        return resultMap;


    }







}