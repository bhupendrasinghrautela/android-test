package com.makaan.analytics;

/**
 * Created by sunil on 17/02/16.
 */
public class MakaanTrackerConstants {

    public static final String segmentSdkKey = "xMHqomsTMdGwOiJByBsKUbH78Akhbaku";

    public static final String SCREEN_NAME = "Screen name";
    public static final String KEYWORD = "Keywords";
    public static final String LISTING_POSITION = "Listing position";

    public enum Category{
        property("Buyer_Property"),propertyFilter("Buyer_Property_Filter"),propertyMap("Buyer_Property_Map"),
        buyerPropertyDetails("Buyer_Property_Details"),buyerPropertyCall("Buyer_Property_Call"),buyerPropertyGetCallBack("Buyer_Property_Call_Get Callback"),
        buyerProjectCall("Buyer_Project_Call"),buyerProjectGetCallBack("Buyer_Project_Call_Get Callback"),buyerSerp("Buyer_Serp"),buyerProject("Buyer_Project"),
        buyerHome("Buyer_Home"), buyerCity("Buyer_City"),searchMap("Search_Map"),searchFilter("Search_Filter"),buyerLocality("Buyer_Locality"),userLogin("User_Login"),
        buyerPyr("Buyer_PYR"),errorBuyer("Error_Buyer"),buyerDashboard("Buyer_Dashboard"),errorUsability("Error_Usability");

        private String value;

        Category(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum Action{

        searchPropertyBuy("SEARCH_Property_Buy"),searchPropertyBuyRank("SEARCH_Property_Buy_Rank"),searchPropertyBuyLength("SEARCH_Property_Buy_Length")
        , searchPropertyRent("SEARCH_Property_Rent"),searchPropertyRentRank("SEARCH_Property_Rent_Rank"),searchPropertyRentLength("SEARCH_Property_Rent_Length"),
        searchPropertyBuyRent("SEARCH_Property_Buy_Rent"),

        searchHomeBuy("SEARCH_Home_Buy"),searchHomeBuyRank("SEARCH_Home_Buy_Rank"),searchHomeBuyLength("SEARCH_Home_Buy_Length")
        , searchHomeRent("SEARCH_Home_Rent"),searchHomeRentRank("SEARCH_Home_Rent_Rank"),searchHomeRentLength("SEARCH_Home_Rent_Length"),
        searchHomeBuyRent("SEARCH_Home_Buy_Rent"),

        searchProjectBuy("SEARCH_Project_Buy"),searchProjectBuyRank("SEARCH_Project_Buy_Rank"),searchProjectBuyLength("SEARCH_Project_Buy_Length")
        , searchProjectRent("SEARCH_Project_Rent"),searchProjectRentRank("SEARCH_Project_Rent_Rank"),searchProjectRentLength("SEARCH_Project_Rent_Length"),
        searchProjectBuyRent("SEARCH_Project_Buy_Rent"),

        searchSerpBuy("SEARCH_Serp_Buy"),searchSerpBuyRank("SEARCH_Serp_Buy_Rank"),searchSerpBuyLength("SEARCH_Serp_Buy_Length")
        , searchSerpRent("SEARCH_Serp_Rent"),searchSerpRentRank("SEARCH_Serp_Rent_Rank"),searchSerpRentLength("SEARCH_Serp_Rent_Length"),
        searchSerpBuyRent("SEARCH_Serp_Buy_Rent"),

        sortProperty("SORT_Property"),clickPropertyViewSelection("CLICK_Property_View selection"),clickSerpViewSelection("CLICK_Serp_View selection"),
        selectFilterBudget("SELECT_Property_Filters_Budget"),selectFilterBedroom("SELECT_Property_Filters_Bedroom"),
        selectFilterMore("SELECT_Property_Filters_More filters"),mapProperty("MAP_Property"),mapLocality("MAP_Locality"),clickPropertyOverview("CLICK_Property_Overview"),
        clickPropertyCallConnect("CLICK_Property_Call Connect time"),fillPropertyCall("FILL_Property_Call"),fillPropertyGetCallBack("FILL_Property_Get Call Back"),
        fillProjectCall("FILL_Project_Call"),clickProjectCallConnect("CLICK_Project_Call Connect time"),fillProjectGetCallBack("FILL_Project_Get Call Back"),
        clickSerpCallConnect("CLICK_Serp_Call Connect time"),fillSerpCall("FILL_Serp_Call"),fillSerpGetCallBack("FILL_Serp_Get Call Back"),
        selectCityPropertyRange("SELECT_City_Property Range"),clickProject("CLICK_Project"),clickProperty("CLICK_Property"),clickSerp("CLICK_Serp"),
        mapPropertyLocality("MAP_Property_Locality_KYN Nearby Headers"),clickProjectConfiguration("CLICK_Project_Configuration"),clickProjectOverView("CLICK_Project_Overview"),
        mapProjectLocality("MAP_Project_Locality_KYN Nearby Headers"),mapLocalityNeighbourhood("MAP_Locality_Neighbourhood_Nearby Headers"),
        clickProjectViewOtherSellers("CLICK_Project_View other sellers"),clickProjectSimilarProjects("CLICK_Project_Similiar Projects"),clickProjectPriceTrends("CLICK_Project_Price Trends"),
        clickLocality("CLICK_Locality"),clickLocalityPriceTrends("CLICK_Locality_Price Trends"),clickLocalityTopSellers("CLICK_Locality_Top Sellers"),
        clickLocalityTopBuilders("CLICK_Locality_Top Builders"),clickLocalityNearbyLocalities("CLICK_Locality_Nearby Localities"),callSerpProperty("CALL_Serp_Property"),
        clickCityPriceTrends("CLICK_City_Price Trends"),clickCity("CLICK_City"),login("Login"),clickProjectImages("CLICK_Project_Images"),clickPropertyImages("CLICK_Property_Images"),
        clickPropertyViewOtherSellers("CLICK_Property_View other sellers"),clickCityOverView("CLICK_City_Overview"),clickProjectConstructionImages("CLICK_Project_Construction Images"),
        clickSerpPropertyShortList("CLICK_Serp_Property Shortlist"),clickLocalityOverView("CLICK_Locality_Overview"),selectPropertiesLocality("SELECT_Properties in Locality"),
        clickSerpPropertySeller("CLICK_Serp_Property Seller"),clickSerpPropertyView("CLICK_Serp_Property View"),signUpClose("SIGNUP_Close"),signUpSocial("SIGNUP_Social"),
        submitCityTypePyr("SUBMIT_City_PYR"),submitLocalityTypePyr("SUBMIT_Locality_PYR"),submitProjectTypePyr("SUBMIT_Project_PYR"),
        submitDashboardTypePyr("SUBMIT_BuyerDashboard_PYR"),submitJarvisTypePyr("SUBMIT_Jarvis_PYR"),selectCityPyrSellers("SELECT_City_PYR_Sellers"),
        selectLocalityPyrSellers("SELECT_Locality_PYR_Sellers"),selectProjectPyrSellers("SELECT_Project_PYR_Sellers"),
        selectBuyerDashboardPyrSellers("SELECT_BuyerDashboard_PYR_Sellers"), selectJarvisPyrSellers("VIEW_Jarvis_PYR_Sellers"),viewCityPyrSellers("VIEW_City_PYR_Sellers"),
        viewLocalityPyrSellers("VIEW_Locality_PYR_Sellers"), viewProjectPyrSellers("VIEW_Project_PYR_Sellers"), viewBuyerDashboardPyrSellers("VIEW_BuyerDashboard_PYR_Sellers"),
        viewJarvisPyrSellers("VIEW_Jarvis_PYR_Sellers"),selectCityPyrOtp("SELECT_City_PYR_OTP"),selectLocalityPyrOtp("SELECT_Locality_PYR_OTP"),
        selectProjectPyrOtp("SELECT_Project_PYR_OTP"), selectBuyerDashboardPyrOtp("SELECT_BuyerDashboard_PYR_OTP"), selectJarvisPyrOtp("VIEW_Jarvis_PYR_OTP"),
        errorPyr("ERROR_PYR"),clickSavedSearches("CLICK_Savedsearches"),clickShortlist("CLICK_Shortlist"),clickSiteVisits("CLICK_Sitevisits"),clickHomeLoan("CLICK_Homeloan"),
        clickUnitBook("CLICK_Unitbook"),clickPossession("CLICK_Possession"),clickRegistration("CLICK_Registration"),clickShortListFavourite("CLICK_Shortlist_Favourite"),
        clickShortListRecentlyViewed("CLICK_Shortlist_Recentlyviewed"),errorLogin("ERROR_Login"),errorPassword("ERROR_Password"),errorSignUp("ERROR_Sign Up"),
        sorryNoMatchingResultFound("Error_sorry, no matching results found"),errorNa("Error_N.A."),noPropertyMatchingSearch("Error_found no properties matching your search criteria"),
        clickCashback("CLICK_CashBack"),clickCashBackSeller("CLICK_Cashback_Seller"),clickCashBackListing("CLICK_Cashback_Listing"),
        clickCashBackSellerRating("CLICK_Cashback_Seller Rating ");

        private String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    public enum Label{
        Price("Price"),sellerRating("seller rating"),datePosted("date posted"),map("Map"),listView("List view"),
        addtoFavourite("Add to Favourite"),localityDetails("Locality details"),sellerDetails("Seller details"),contactSeller("Call Seller"),
        connectNow("Connect Now"),getCallBack("Get Callback"),country("Country"),mobile("Mobile"),name("Name"),email("Email"),number("number"),
        backToHome("Back to Home"),myAccount("My account"),login("Login"),chat("M_Project (Chat)"),builderMore("Builder More"),builderLess("Builder Less"),
        descriptionMore("Description More"),descriptionLess("DescriptionLess"),viewAllPropertiesToBuyIn("View all properties to buy in_"),specifications("Specifications"),
        right("Right"),left("Left"),checked("Checked"),unChecked("UnChecked"),propertiesForSale("Properties for sale in_"),buy("buy"),rent("rent"),
        facebookSuccess("FACEBOOK_Success"),facebookFailed("FACEBOOK_Failed"), googleSuccess("GOOGLE_Success"),googleFail("GOOGLE_Fail"),
        loginWithEmailSuccess("LOGIN_WITH_email_success"),loginWithEmailFail("LOGIN_WITH_email_failed"),callNow("CALL_now"),
        registrationSuccess("REGISTRATION_Success"),registrationFailed("REGISTRATION_Failed"),getCallBackFromSeller("GET_CALL_BACK_from_seller"),
        closeSignUp("Close_SignUp"),dontHaveSocial("I don't have social"),facebook("Facebook"),google("Google"),backSocial("Back_social"),closeSocial("Close_social"),
        city("City"),locality("Locality"),project("Project"),buyerDashBoard("BuyerDashboard"),jarvis("Jarvis"),back("Back"),close("Close"),success("Success"),
        failure("Failure"),resend("Resend"),skip("Skip"),incorrectOtp("Incorrect OTP"),builder("Builder"),enquired("Enquired"),favourite("Favourite"),recent("Recent"),
        phoneNumberRequired("phone number is required"),nameRequired("name is required"),emailRequired("email is required"),phoneNumberInvalid("phone number is invalid"),
        errorWhileSubmitting("error while submitting"),notRegisteredUser("sorry! you are not a registered user."),searchString("Search string"),leadForm("Lead form"),
        getCashBack("Get Cashback"),addAnotherListing("Add Another Listing");

        private String value;

        Label(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        @Override
        public String toString() {
            return value;
        }

    }

}
