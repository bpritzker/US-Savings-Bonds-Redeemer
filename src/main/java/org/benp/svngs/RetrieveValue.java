package org.benp.svngs;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.benp.svngs.data.BondData;
import org.benp.svngs.data.BondDenomination;
import org.benp.svngs.data.BondIssueDate;
import org.benp.svngs.data.BondSeries;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public class RetrieveValue {


    static Logger logger = Logger.getLogger(RetrieveValue.class.getName());


    public static void main(String[] args) {
        RetrieveValue RetrieveValue = new RetrieveValue();
        try {
            RetrieveValue.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() throws Exception {


        List<BondData> allBondData = loadBondData();

        File bondsRetrieveFile = new File("C:\\US-Savingbonds\\BondsRetrieveData.csv");

        List<BondData> bondsToRetrieve = identifyBondsToRetrieve(allBondData, bondsRetrieveFile);

        retrieveValues(bondsToRetrieve, bondsRetrieveFile);





    }

    private List<BondData> identifyBondsToRetrieve(List<BondData> allBondData, File bondsRetrieveFile) throws Exception {

        if (! bondsRetrieveFile.exists()) {
            return allBondData;
        }

        List<List<String>> bondsRetrieveData = loadBondsRetrieveData(bondsRetrieveFile);

        Set<String> allAlreadyRetrievedSources = new HashSet<>();

        List<BondData> resultData = new ArrayList<>();
        for (List<String> currLine : bondsRetrieveData) {
            allAlreadyRetrievedSources.add(currLine.get(0));
        }

        for (BondData currBondData : allBondData) {
            if (! allAlreadyRetrievedSources.contains(currBondData.getSource())) {
                resultData.add(currBondData);
            }
        }

        logger.info("Total to Retrieve: " + resultData.size());
        return resultData;

    }

    private List<List<String>> loadBondsRetrieveData(File bondsRetrieveFile) throws Exception {

        Reader in = new FileReader(bondsRetrieveFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        List<List<String>> resultData = new ArrayList<>();
        for (CSVRecord record : records) {

            List<String> tempLine = new ArrayList<>();
            tempLine.add(record.get("Source"));
            tempLine.add(record.get("Total Value"));
//            tempLine.add(record.get("Redemption Date"));
            resultData.add(tempLine);
        }
        return resultData;

    }

    private void retrieveValues(List<BondData> bondsToRerieve, File bondsRetrieveFile) throws InterruptedException, IOException {

        System.setProperty("webdriver.chrome.driver", "C:\\app\\Applications\\Chromedriver\\96.0.4664.45\\chromedriver.exe");


        FileWriter fileWriter = new FileWriter(bondsRetrieveFile, true);

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try (CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.RFC4180)) {
            //HEADER record
            csvPrinter.printRecord("Source", "Total Value");


            int counter = 0;
            for (BondData currBondData : bondsToRerieve) {
                counter++;
                driver.get("https://www.treasurydirect.gov/BC/SBCPrice");

                System.out.println("Getting value for " + currBondData.getSource() + "  " + counter + " of " + bondsToRerieve.size());

                String value = retrieveBondData(currBondData, "04/2021", driver);

                if (value.equals("-1")) {
                    // Try again...
                    Thread.sleep(5000);
                    value = retrieveBondData(currBondData, "04/2021", driver);
                }

                csvPrinter.printRecord(currBondData.getSource(), value);
                csvPrinter.flush();

                Thread.sleep(2000);


            }

//
//            driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
//            WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3")));
//            System.out.println(firstResult.getAttribute("textContent"));
        } finally {
            driver.quit();
        }








    }

    public List<BondData> loadBondData() throws IOException {

        File inputFile = new File("C:\\US-Savingbonds\\BondsListRaw.csv");


        Reader in = new FileReader(inputFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        List<BondData> resultData = new ArrayList<>();
        int counter = 0;
        for (CSVRecord record : records) {
            counter++;
            BondData tempBondData = new BondData();
            tempBondData.setSeries(BondSeries.fromString(record.get("Series")));
            tempBondData.setSource(record.get("Source"));
            tempBondData.setDenomination(BondDenomination.fromNumberString(record.get("Denomination")));

            BondIssueDate tempIssueDate = new BondIssueDate(record.get("Issue Date Month"), record.get("Issue Data Year"));

            tempBondData.setIssueDate(tempIssueDate);
            tempBondData.setBondSerialNumber(record.get("Bond Serial Number"));


            if (tempIssueDate.isValid()) {
                resultData.add(tempBondData);
            } else {
                logger.warning("Invalid Values: <" + tempBondData.getSource() + ">");
            }

        }
        return resultData;
    }

    private String retrieveBondData(BondData bondData, String valueAsOfDate, WebDriver driver) throws InterruptedException {

        logger.fine("Processing Bond Data: " + bondData.toString());



        WebElement redemptionDateElement = driver.findElement(By.name("RedemptionDate"));
        redemptionDateElement.sendKeys(Keys.CONTROL + "a");
        redemptionDateElement.sendKeys(Keys.DELETE);
        redemptionDateElement.sendKeys(valueAsOfDate);


//        driver.findElement(By.name("RedemptionDate")).sendKeys(valueAsOfDate);
        Select seriesSelect = new Select(driver.findElement(By.name("Series")));
        seriesSelect.selectByValue(bondData.getSeries().getStringVal());



        Select denominationSelect = new Select(driver.findElement(By.name("Denomination")));
        denominationSelect.selectByValue(bondData.getDenomination().getNumberString());

//
//        driver.findElement(By.name("RedemptionDate")).sendKeys(valueAsOfDate);

        driver.findElement(By.name("SerialNumber")).sendKeys(bondData.getBondSerialNumber());

        driver.findElement(By.name("IssueDate")).sendKeys(bondData.getIssueDate().toFormString());


        Thread.sleep(1000);

        driver.findElement(By.name("btnAdd.x")).click();



        //            WebElement firstResult = wait.until(presenceOfElementLocated(By.cssSelector("h3")));



        List<WebElement> tableElements = driver.findElements(By.id("ta1"));

        if (tableElements.size() == 0) {
            logger.warning("  Could not retrieve Data for element: " + bondData.toString());
            return "-1";
        }

        WebElement table = driver.findElement(By.id("ta1"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        List<WebElement> cells = rows.get(1).findElements(By.tagName("td"));

        String value = cells.get(1).getText();

        System.out.println("Value.... " +value );



        return value;


    }

}