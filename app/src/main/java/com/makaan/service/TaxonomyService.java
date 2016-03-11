package com.makaan.service;

import com.makaan.activity.listing.SerpActivity;
import com.makaan.pojo.SerpRequest;
import com.makaan.pojo.TaxonomyCard;

import java.util.ArrayList;
import java.util.List;

import static com.makaan.constants.StringConstants.RECENT_PROPERTIES;
import static com.makaan.constants.StringConstants.*;

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
        luxuryCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        luxuryCard.serpRequest.setCityId(cityId);
        luxuryCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);
//        luxuryCard.serpRequest.setMinBudget(minLuxuryPrice == null ? 0 : minLuxuryPrice.longValue());
        luxuryCard.serpRequest.setMinBudget(defaultMinLuxuryPrice.longValue());


        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        affordableCard.label1 = RECENT_PROPERTIES;
        affordableCard.label2 = RECENT_PROPERTIES_MSG;
        affordableCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        affordableCard.serpRequest.setCityId(cityId);
        affordableCard.serpRequest.setSort(SerpRequest.Sort.DATE_POSTED_DESC);
        affordableCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        budgetCard.serpRequest.setCityId(cityId);
        budgetCard.serpRequest.setMaxBudget(defaultMaxBudgetPrice.longValue());
        budgetCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        bestCard.label1 = POPULAR_PROPERTIES;
        bestCard.label2 = POPULAR_PROPERTIES_MSG;
        bestCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        bestCard.serpRequest.setCityId(cityId);
        bestCard.serpRequest.setSort(SerpRequest.Sort.SELLER_RATING_DESC);
        bestCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
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
        luxuryCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        luxuryCard.serpRequest.setLocalityId(localityId);
//        luxuryCard.serpRequest.setMinBudget(minLuxuryPrice == null ? 0 : minLuxuryPrice.longValue());
        luxuryCard.serpRequest.setMinBudget(defaultMinLuxuryPrice.longValue());
        luxuryCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard affordableCard = new TaxonomyCard();
        taxonomyCardList.add(affordableCard);
        affordableCard.label1 = RECENT_PROPERTIES;
        affordableCard.label2 = RECENT_PROPERTIES_MSG;
        affordableCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        affordableCard.serpRequest.setLocalityId(localityId);
        affordableCard.serpRequest.setSort(SerpRequest.Sort.DATE_POSTED_DESC);
        affordableCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard budgetCard = new TaxonomyCard();
        taxonomyCardList.add(budgetCard);
        budgetCard.label1 = BUDGET_HOMES;
        budgetCard.label2 = BUDGET_HOMES_MSG;
        budgetCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        budgetCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);
        budgetCard.serpRequest.setLocalityId(localityId);
//        budgetCard.serpRequest.setMaxBudget(maxBudgetPrice == null ? 0 : maxBudgetPrice.longValue());
        budgetCard.serpRequest.setMaxBudget(defaultMaxBudgetPrice.longValue());

        TaxonomyCard bestCard = new TaxonomyCard();
        taxonomyCardList.add(bestCard);
        bestCard.label1 = POPULAR_PROPERTIES;
        bestCard.label2 = POPULAR_PROPERTIES_MSG;
        bestCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        bestCard.serpRequest.setLocalityId(localityId);
        bestCard.serpRequest.setSort(SerpRequest.Sort.SELLER_RATING_DESC);
        bestCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_BUY);


        TaxonomyCard rentalCard = new TaxonomyCard();
        taxonomyCardList.add(rentalCard);
        rentalCard.label1 = NEW_RENTAL_PROPERTIES;
        rentalCard.label2 = NEW_RENTAL_PROPERTIES_MSG;
        rentalCard.serpRequest = new SerpRequest(SerpActivity.TYPE_TAXONOMY);
        rentalCard.serpRequest.setLocalityId(localityId);
        rentalCard.serpRequest.setSerpContext(SerpRequest.CONTEXT_RENT);
        rentalCard.serpRequest.setSort(SerpRequest.Sort.DATE_POSTED_DESC);


        return taxonomyCardList;
    }

}
