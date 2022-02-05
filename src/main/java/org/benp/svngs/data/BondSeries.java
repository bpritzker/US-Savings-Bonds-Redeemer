package org.benp.svngs.data;

public enum BondSeries {

    EE("EE", "NA"),
    I("I", "NA");


    public String getStringVal() {
        return stringVal;
    }

    private final String stringVal;
    private final String formValue;
    BondSeries(String stringVal, String formValue) {
        this.stringVal = stringVal;
        this.formValue = formValue;
    }


    public static BondSeries fromString(String stringVal) {
        for (BondSeries currBondSeries : BondSeries.values()) {
            if (currBondSeries.stringVal.equals(stringVal)) {
                return currBondSeries;
            }
        }
        return null;
    }

    public String getFormValue() {
        return formValue;
    }
}
