package com.makaan.service;

import com.makaan.pojo.SerpRequest;
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

    private Double
            defaultMinAffordablePrice = 4000000D,
            defaultMaxAffordablePrice = 8000000D,
            defaultMinLuxuryPrice = 15000000D,
            defaultMaxBudgetPrice = 5000000D;


    private void applyPricingDefaults(Double minAffordablePrice, Double maxAffordablePrice, Double minLuxuryPrice, Double maxBudgetPrice) {
        if (null == minAffordablePrice) {
            minAffordablePrice = defaultMinAffordablePrice;
        }
        if (null == maxAffordablePrice) {
            maxAffordablePrice = defaultMaxAffordablePrice;
        }
        if (null == minLuxuryPrice) {
            minLuxuryPrice = defaultMinLuxuryPrice;
        }
        if (null == maxBudgetPrice) {
            maxBudgetPrice = defaultMaxBudgetPrice;
        }
    }

    public List<TaxonomyCard> getTaxonomyCardForCity(Long cityId, Double minAffordablePrice, Double maxAffordablePrice, Double minLuxuryPrice, Double maxBudgetPrice) {
        //applyPricingDefaults(minAffordablePrice, maxAffordablePrice, minLuxuryPrice, maxBudgetPrice);

        if (null == minAffordablePrice) {
            minAffordablePrice = defaultMinAffordablePrice;
        }
        if (null == maxAffordablePrice) {
            maxAffordablePrice = defaultMaxAffordablePrice;
        }
        if (null == minLuxuryPrice) {
            minLuxuryPrice = defaultMinLuxuryPrice;
        }
        if (null == maxBudgetPrice) {
            maxBudgetPrice = defaultMaxBudgetPrice;
        }

        List<TaxonomyCard> taxonomyCardList = new ArrayList<>();


        TaxonomyCard luxuryCard = new TaxonomyCard();
        taxonomyCardList.add(luxuryCard);
        luxuryCard.label1 = LUXURY_PROPERTIES;
        luxuryCard.label2 = LUXURY_PROPERTIES_MSG;
        luxuryCard.serpRequest = new SerpRequest();
        luxuryCard.serpRequest.setCityId(cityId);
        luxuryCard.serpRequest.setMinBudget(minLuxuryPrice == null ? 0 : minLuxuryPrice.longValue());


        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        affordableCard.label1 = AFFORDABLE_PROPERTIES;
        affordableCard.label2 = AFFORDABLE_PROPERTIES_MSG;
        affordableCard.serpRequest = new SerpRequest();
        affordableCard.serpRequest.setCityId(cityId);
        affordableCard.serpRequest.setMinBudget(minAffordablePrice == null ? 0 : minAffordablePrice.longValue());


        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.serpRequest = new SerpRequest();
        budgetCard.serpRequest.setCityId(cityId);
        budgetCard.serpRequest.setMaxBudget(maxBudgetPrice == null ? 0 : maxBudgetPrice.longValue());


        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        bestCard.label1 = BEST_PROPERTIES;
        bestCard.label2 = BEST_PROPERTIES_MSG;
        bestCard.serpRequest = new SerpRequest();
        bestCard.serpRequest.setCityId(cityId);
        //TODO set sorting order
        //bestSelector.term(CITY_ID, cityId.toString()).sort(LISTING_QUALITY_SCORE, SORT_DESC);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.serpRequest = new SerpRequest();
        rentalCard.serpRequest.setCityId(cityId);
        rentalCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_RENT);
        rentalCard.serpRequest.setSort(SerpRequest.Sort.DATE_POSTED_DESC);

        return taxonomyCardList;
    }


    public List<TaxonomyCard> getTaxonomyCardForLocality(Long localityId, Double minAffordablePrice, Double maxAffordablePrice, Double minLuxuryPrice, Double maxBudgetPrice) {
        //applyPricingDefaults(minAffordablePrice, maxAffordablePrice, minLuxuryPrice, maxBudgetPrice);
        if (null == minAffordablePrice) {
            minAffordablePrice = defaultMinAffordablePrice;
        }
        if (null == maxAffordablePrice) {
            maxAffordablePrice = defaultMaxAffordablePrice;
        }
        if (null == minLuxuryPrice) {
            minLuxuryPrice = defaultMinLuxuryPrice;
        }
        if (null == maxBudgetPrice) {
            maxBudgetPrice = defaultMaxBudgetPrice;
        }

        List<TaxonomyCard> taxonomyCardList = new ArrayList<>();


        TaxonomyCard luxuryCard = new TaxonomyCard();
        taxonomyCardList.add(luxuryCard);
        luxuryCard.label1 = LUXURY_PROPERTIES;
        luxuryCard.label2 = LUXURY_PROPERTIES_MSG;
        luxuryCard.serpRequest = new SerpRequest();
        luxuryCard.serpRequest.setLocalityId(localityId);
        luxuryCard.serpRequest.setMinBudget(minLuxuryPrice==null?0:minLuxuryPrice.longValue());


        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        affordableCard.label1 = AFFORDABLE_PROPERTIES;
        affordableCard.label2 = AFFORDABLE_PROPERTIES_MSG;
        affordableCard.serpRequest = new SerpRequest();
        affordableCard.serpRequest.setLocalityId(localityId);
        affordableCard.serpRequest.setMinBudget(minAffordablePrice==null?0:minAffordablePrice.longValue());


        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.serpRequest = new SerpRequest();
        budgetCard.serpRequest.setLocalityId(localityId);
        budgetCard.serpRequest.setMaxBudget(maxBudgetPrice==null?0:maxBudgetPrice.longValue());


        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        bestCard.label1 = BEST_PROPERTIES;
        bestCard.label2 = BEST_PROPERTIES_MSG;
        bestCard.serpRequest = new SerpRequest();
        bestCard.serpRequest.setLocalityId(localityId);
        //TODO set sorting order
        //bestSelector.term(CITY_ID, cityId.toString()).sort(LISTING_QUALITY_SCORE, SORT_DESC);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.serpRequest = new SerpRequest();
        rentalCard.serpRequest.setLocalityId(localityId);
        rentalCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_RENT);
        rentalCard.serpRequest.setSort(SerpRequest.Sort.DATE_POSTED_DESC);


        return taxonomyCardList;
    }

}
