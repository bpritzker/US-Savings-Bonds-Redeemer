package org.benp.svngs.data;


import org.benp.svngs.BondParserUtils;

import java.util.Date;

public class BondData {


    BondSeries series;

    @Override
    public String toString() {
        return "BondData{" +
                "series=" + series +
                ", denomination=" + denomination +
                ", bondSerialNumber='" + bondSerialNumber + '\'' +
                ", issueDate=" + issueDate.toFormString() +
                ", source='" + source + '\'' +
                '}';
    }

    BondDenomination denomination;
    String bondSerialNumber;
    BondIssueDate issueDate;

    private Date valueAsOfDate;
    private String valueOnValueAsOfDate;




    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    String source;

    public BondSeries getSeries() {
        return series;
    }

    public void setSeries(BondSeries series) {
        this.series = series;
    }

    public BondDenomination getDenomination() {
        return denomination;
    }

    public void setDenomination(BondDenomination denomination) {
        this.denomination = denomination;
    }

    public String getBondSerialNumber() {
        return bondSerialNumber;
    }

    public void setBondSerialNumber(String bondSerialNumber) {
        this.bondSerialNumber = bondSerialNumber;
    }

    public BondIssueDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(BondIssueDate issueDate) {
        this.issueDate = issueDate;
    }


    public Date getValueAsOfDate() {
        return valueAsOfDate;
    }

    public void setValueAsOfDate(Date valueAsOfDate) {
        this.valueAsOfDate = valueAsOfDate;
    }

    public String getValueOnValueAsOfDate() {
        return valueOnValueAsOfDate;
    }

    public void setValueOnValueAsOfDate(String valueOnValueAsOfDate) {
        this.valueOnValueAsOfDate = valueOnValueAsOfDate;
    }
}