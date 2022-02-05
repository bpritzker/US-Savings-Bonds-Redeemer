package org.benp.svngs.data;


import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;

public class BondIssueDate {


    public BondIssueDate(String month, String year) {
        this.month = month;
        this.year = year;
    }


    private String month;
    private String year;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String toFormString() {
        return month + "/" + year;
    }

    public boolean isValid() {

        if (!(NumberUtils.isDigits(month) && NumberUtils.isDigits(year))) {
            return false;

        }

        if (month.length() != 2) {
            return false;
        }

        if (year.length() != 4) {
            return false;
        }

        int yearInt = Integer.parseInt(year);
        if (yearInt > 2021 || yearInt < 1950) {
            return false;
        }

        return true;

    }
}