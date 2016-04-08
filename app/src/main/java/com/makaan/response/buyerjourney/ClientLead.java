package com.makaan.response.buyerjourney;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientLead {
    public Long id;
    public Long createdAt;
    public Long companyId;
    public ArrayList<PropertyRequirement> propertyRequirements;
    public ClientActivity clientActivity;

    public class PropertyRequirement {
        public Long id;
        public Long listingId;
        public Long projectId;
        public Long leadId;
        public Long localityId;
        public Long suburbId;
        public Integer bedroom;
        public Float latitude;
        public Float longitude;
        public Integer radiusKm;
        public Long minBudget;
        public Long maxBudget;
        public Long minSize;
        public Long maxSize;
        public Integer areaUnitTypeId;
    }

    public class ClientActivity {
        public int phaseId;
    }
}
