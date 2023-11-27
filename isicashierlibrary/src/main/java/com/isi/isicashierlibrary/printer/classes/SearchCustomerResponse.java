package com.isi.isicashierlibrary.printer.classes;

import com.google.gson.annotations.SerializedName;
import com.isi.isicashierlibrary.printer.exceptions.BadRequestGYBException;
import com.isi.isicashierlibrary.printer.exceptions.OutOfTimeGybException;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchCustomerResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("code")
    private String code;
    @SerializedName("vatNumber")
    private String vatNumber;
    @SerializedName("taxCode")
    private String taxCode;
    @SerializedName("description")
    private String description;
    @SerializedName("address")
    private String address;
    @SerializedName("city")
    private String city;
    @SerializedName("zipCode")
    private String zipCode;
    @SerializedName("province")
    private String province;
    @SerializedName("country")
    private String country;
    @SerializedName("pec")
    private String pec;
    @SerializedName("intermediaryType")
    private int intermediaryType;
    @SerializedName("hasConvention")
    private boolean hasConvention;

    public SearchCustomerResponse(String json) throws JSONException, OutOfTimeGybException, BadRequestGYBException {
        if (json.trim().equals("delay")) {
            throw new OutOfTimeGybException();
        } else if (json.trim().equals("bad")) {
            throw new BadRequestGYBException();
        } else {
            JSONObject jsonObject = new JSONObject(json);
            this.id = jsonObject.optInt("id");
            this.code = jsonObject.optString("code");
            this.vatNumber = jsonObject.optString("vatNumber");
            this.taxCode = jsonObject.optString("taxCode");
            this.description = jsonObject.optString("description");
            this.address = jsonObject.optString("address");
            this.city = jsonObject.optString("city");
            this.zipCode = jsonObject.optString("zipCode");
            this.province = jsonObject.optString("province");
            this.country = jsonObject.optString("country");
            this.pec = jsonObject.optString("pec");
            this.intermediaryType = jsonObject.optInt("intermediaryType");
            this.hasConvention = jsonObject.optBoolean("hasConvention");
        }
    }

    public SearchCustomerResponse(String code, String vatNumber, String taxCode, String description, String address, String city, String zipCode, String province, String country, String pec) {
        this.id = 0;
        this.code = code;
        this.vatNumber = vatNumber;
        this.taxCode = taxCode;
        this.description = description;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.province = province;
        this.country = country;
        this.pec = pec;
        this.hasConvention = false;
        this.intermediaryType = 0;
    }

    public int getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getVatNumber() {
        return this.vatNumber;
    }

    public String getTaxCode() {
        return this.taxCode;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCity() {
        return this.city;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public String getProvince() {
        return this.province;
    }

    public String getCountry() {
        return this.country;
    }

    public String getPec() {
        return this.pec;
    }

    public int getIntermediaryCode() {
        return this.intermediaryType;
    }

    public boolean isHasConvention() {
        return this.hasConvention;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setVatNumber(String vatNumber) {
        this.vatNumber = vatNumber;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPec(String pec) {
        this.pec = pec;
    }

    public void setIntermediaryType(int intermediaryType) {
        this.intermediaryType = intermediaryType;
    }
}

