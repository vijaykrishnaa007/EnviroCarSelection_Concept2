package org.andresoviedo.app.model3D;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class vehicledetails {

    @SerializedName("links")
    @Expose
    private List<Link> links = null;
    @SerializedName("tsn")
    @Expose
    private String tsn;
    @SerializedName("commercialName")
    @Expose
    private String commercialName;
    @SerializedName("allotmentDate")
    @Expose
    private String allotmentDate;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("power")
    @Expose
    private Integer power;
    @SerializedName("engineCapacity")
    @Expose
    private Integer engineCapacity;
    @SerializedName("axles")
    @Expose
    private Integer axles;
    @SerializedName("poweredAxles")
    @Expose
    private Integer poweredAxles;
    @SerializedName("seats")
    @Expose
    private Integer seats;
    @SerializedName("maximumMass")
    @Expose
    private Integer maximumMass;

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public String getTsn() {
        return tsn;
    }

    public void setTsn(String tsn) {
        this.tsn = tsn;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getAllotmentDate() {
        return allotmentDate;
    }

    public void setAllotmentDate(String allotmentDate) {
        this.allotmentDate = allotmentDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(Integer engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public Integer getAxles() {
        return axles;
    }

    public void setAxles(Integer axles) {
        this.axles = axles;
    }

    public Integer getPoweredAxles() {
        return poweredAxles;
    }

    public void setPoweredAxles(Integer poweredAxles) {
        this.poweredAxles = poweredAxles;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Integer getMaximumMass() {
        return maximumMass;
    }

    public void setMaximumMass(Integer maximumMass) {
        this.maximumMass = maximumMass;
    }

}
