package com.makaan.service;

import com.makaan.pojo.TaxonomyCard;
import com.makaan.request.selector.Selector;

import java.util.ArrayList;
import java.util.List;

import static com.makaan.constants.StringConstants.AFFORDABLE_PROPERTIES;
import static com.makaan.constants.StringConstants.*;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 13/01/16.
 */
public class TaxonomyService implements MakaanService {

    private Double minAffodablePrice = 4000000D,
            maxAffodablePrice = 8000000D,
            minLuxuryPrice = 15000000D,
            maxBudgetPrice = 5000000D;



    public List<TaxonomyCard> getTaxonomyCardForCity(Long cityId) {
        List<TaxonomyCard> taxonomyCardList = new ArrayList<>();


        TaxonomyCard luxuryCard = new TaxonomyCard();
        taxonomyCardList.add(luxuryCard);
        Selector luxurySelector = new Selector();
        luxuryCard.label1 = LUXURY_PROPERTIES;
        luxuryCard.label2 = LUXURY_PROPERTIES_MSG;
        luxuryCard.selector = luxurySelector;

        luxurySelector.term(CITY_ID, cityId.toString()).range(PRICE, minLuxuryPrice, null);

        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        Selector affordableSelector = new Selector();
        affordableCard.label1 = AFFORDABLE_PROPERTIES;
        affordableCard.label2 = AFFORDABLE_PROPERTIES_MSG;
        affordableCard.selector = affordableSelector;

        affordableSelector.term(CITY_ID, cityId.toString()).range(PRICE, minAffodablePrice, maxAffodablePrice);

        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        Selector budgetSelector = new Selector();
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.selector = budgetSelector;

        budgetSelector.term(CITY_ID, cityId.toString()).range(PRICE, null, maxBudgetPrice);



        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        Selector bestSelector = new Selector();
        bestCard.label1 = BEST_PROPERTIES;
        bestCard.label2 = BEST_PROPERTIES_MSG;
        bestCard.selector = bestSelector;

        bestSelector.term(CITY_ID, cityId.toString()).sort(LISTING_QUALITY_SCORE,SORT_DESC);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        Selector rentalSelector = new Selector();
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.selector = rentalSelector;

        rentalSelector.term(CITY_ID, cityId.toString()).term(LISTING_CATEGORY,RENTAL).sort(LISTING_CREATED_AT, SORT_DESC);


        return taxonomyCardList;
    }


    public List<TaxonomyCard> getTaxonomyCardForLocality(Long localityId) {
        List<TaxonomyCard> taxonomyCardList = new ArrayList<>();


        TaxonomyCard luxuryCard = new TaxonomyCard();
        taxonomyCardList.add(luxuryCard);
        Selector luxurySelector = new Selector();
        luxuryCard.label1 = LUXURY_PROPERTIES;
        luxuryCard.label2 = LUXURY_PROPERTIES_MSG;
        luxuryCard.selector = luxurySelector;

        luxurySelector.term(LOCALITY_ID, localityId.toString()).range(PRICE, minLuxuryPrice, null);

        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        Selector affordableSelector = new Selector();
        affordableCard.label1 = AFFORDABLE_PROPERTIES;
        affordableCard.label2 = AFFORDABLE_PROPERTIES_MSG;
        affordableCard.selector = affordableSelector;

        affordableSelector.term(LOCALITY_ID, localityId.toString()).range(PRICE, minAffodablePrice, maxAffodablePrice);

        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        Selector budgetSelector = new Selector();
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.selector = budgetSelector;

        budgetSelector.term(LOCALITY_ID, localityId.toString()).range(PRICE, null, maxBudgetPrice);



        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        Selector bestSelector = new Selector();
        bestCard.label1 = BEST_PROPERTIES;
        bestCard.label2 = BEST_PROPERTIES_MSG;
        bestCard.selector = bestSelector;

        bestSelector.term(LOCALITY_ID, localityId.toString()).sort(LISTING_QUALITY_SCORE,SORT_DESC);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        Selector rentalSelector = new Selector();
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.selector = rentalSelector;

        rentalSelector.term(LOCALITY_ID, localityId.toString()).term(LISTING_CATEGORY,RENTAL).sort(LISTING_CREATED_AT, SORT_DESC);


        return taxonomyCardList;
    }

}
