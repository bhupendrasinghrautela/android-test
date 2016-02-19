package com.makaan.response.buyerjourney;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/16/16.
 */
public class ClientEvent {
    public Long id;
    public Long agentId;
    public Long performTime;
    public ArrayList<ClientEventListings> clientEventListings;
    public ArrayList<ClientEventProjects> clientEventProjects;

    public class ClientEventListings {
        public Long id;
        public Long listingId;
    }

    public class ClientEventProjects {
        public Long id;
        public Long projectId;
    }
}
