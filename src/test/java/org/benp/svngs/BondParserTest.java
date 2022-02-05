package org.benp.svngs;


import org.benp.svngs.data.BondIssueDate;
import org.benp.svngs.data.BondSeries;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class BondParserTest extends BondParser {


    public BondParserTest(Map<String, BondSeries> bondSeriesOverrideMap) {
        super(bondSeriesOverrideMap);
    }

    @Test
    public void getIssueDateTest() throws Exception {


        List<String> inputData = createDefaultInputData();

        BondIssueDate actual = getIssueDate(inputData);



    }

    private List<String> createDefaultInputData() {
        List<String> result = new ArrayList<>();
        result.add("NVUD STATES SAVINGS BOND");
        result.add("200");
        result.add("THE UNIIED STATES OFAMERICA");
        result.add("SERIES EE");
        result.add("TWO HUNDRED DOLGLARS");
        result.add("INTEREST CEASES 30 YEARS");
        result.add("FROM ISSUE DATE");
        result.add("000 00 0000");
        result.add("SOC SEC/EMP ID NO");
        result.add("PATRIOT BOND");
        result.add("10"); // This is line 10, 0 based
        result.add("2003");
        result.add("ISSUE DATE");
        result.add("To");
        result.add("Homer Simpson");
        result.add("%D");
        result.add("FRB BUFF");
        result.add("10 m2403:");
        result.add("1");
        result.add("742 EVERGREEN Tr");
        result.add("Springfield");
        result.add("MA");
        result.add("11111-1111");
        result.add("DATING STAMP");
        result.add("Il LLU.LULLLLli LU");
        result.add("RDS-M 11111111-0000 11111111 1111111 111");
        result.add("1111111111");
        result.add("R111111111EE");
        result.add("THE");
        result.add("OF");
        result.add("Secretary of the Treasury");
        result.add("INDEPENDENCE HALL");
        result.add("ATMEN");
        result.add(":1111111111:05");
        result.add("1111111111");
        return result;

    }


}