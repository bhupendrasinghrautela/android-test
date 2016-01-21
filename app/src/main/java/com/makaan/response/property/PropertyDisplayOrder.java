package com.makaan.response.property;

import java.util.List;

/**
 * Created by aishwarya on 20/01/16.
 */
public class PropertyDisplayOrder {
    public PropertyTypeDisplay primary;
    public PropertyTypeDisplay rental;
    public PropertyTypeDisplay resale;

    public class PropertyTypeDisplay {
        public UnitTypeDisplay apartment;
        public UnitTypeDisplay builderFloor;
        public UnitTypeDisplay independentHouse;
        public UnitTypeDisplay villa;
        public UnitTypeDisplay plot;
        public UnitTypeDisplay pentHouse;
        public UnitTypeDisplay studioApartment;
        public UnitTypeDisplay farmHouse;
    }

    private class UnitTypeDisplay {
        public List<String> overview;
        public List<Integer> amenity;
        public List<Integer> furnish;

    }
}
