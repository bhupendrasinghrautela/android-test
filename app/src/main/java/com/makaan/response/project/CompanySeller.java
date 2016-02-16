package com.makaan.response.project;

import com.google.gson.annotations.SerializedName;
import com.makaan.response.image.Image;

import java.util.ArrayList;

/**
 * Created by vaibhav on 17/01/16.
 */
public class CompanySeller {

    public Long id;
    public String name;
    public Long activeSince;
    public ArrayList<Seller> sellers;
    public String logo;
    public String coverPicture;
    public float score;

    public class Seller {
        public String sellerType;
        public CompanyUser companyUser;
        public ArrayList<City> cities;

        public class City {
            public Long id;
            public String label;
        }

        public class CompanyUser {

            public SellerListingData sellerListingData;
            public User user;

            public class User {
                public String fullName;
            }

            public class SellerListingData {
                public int localityCount, projectCount;
                public ArrayList<CategoryWiseCount> categoryWiseCount;

                public class CategoryWiseCount {
                    public int listingCount;
                    public String listingCategoryType;
                }
            }
        }
    }
}
