package com.makaan.response.buyerjourney;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientLead {
    public Long createdAt;
    public Long companyId;
    public ArrayList<PropertyRequirement> propertyRequirements;
    public ClientActivity clientActivity;

    public class PropertyRequirement {
        public Long listingId;
        public Long projectId;
        public Long leadId;
    }

    public class ClientActivity {
        public int phaseId;
    }
}
