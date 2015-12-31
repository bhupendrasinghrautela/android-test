package com.makaan.response.listing;

/**
 * Created by vaibhav on 23/12/15.
 */
public class ApiListingDetail {

    private Long id;
    private boolean negotiable;
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
