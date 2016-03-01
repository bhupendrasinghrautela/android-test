package com.makaan.response.city;

import java.util.HashMap;

/**
 * Created by vaibhav on 09/01/16.
 */
public class EntityDesc {


    public Long id;
    public EntityDescCategory entityDescriptionCategories;
    public HashMap<String, String> masterDescriptionCategories = new HashMap<>();
    public String description;
    public String name;
    public String createdAt;

    public class EntityDescCategory {
        public MasterDescCategory masterDescriptionCategory;
    }

    public class MasterDescCategory {
        public String name;
    }
}
