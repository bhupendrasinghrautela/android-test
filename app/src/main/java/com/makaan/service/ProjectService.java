package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.project.Project;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;

/**
 * Created by vaibhav on 23/01/16.
 */
public class ProjectService implements MakaanService {


    /**
     * http://marketplace-qa.proptiger-ws.com/app/v4/project-detail/506147?sourceDomain=Makaan
     */
    public void getProjectById(Long projectId) {

        if (null != projectId) {
            String projectUrl = ApiConstants.PROJECT.concat("/").concat(projectId.toString());

            Type projectType = new TypeToken<Project>() {
            }.getType();

            MakaanNetworkClient.getInstance().get(projectUrl, projectType, new ObjectGetCallback() {
                @Override
                public void onSuccess(Object responseObject) {
                    Project project = (Project) responseObject;
                    AppBus.getInstance().post(new ProjectByIdEvent(project));
                }
            });
        }
    }


    /**
     * http://marketplace-qa.makaan-ws.com/data/v2/recommendation?type=similar&projectId=506147&selector={"fields":["projectId","URL","imageURL","altText","mainImage","minPrice","minResaleOrPrimaryPrice","id","city","suburb","label","name","type","user","contactNumbers","locality","contactNumber","sellerId","listingCategory","property","currentListingPrice","price","bedrooms","bathrooms","size","unitTypeId","project","projectId","studyRoom","servantRoom","poojaRoom","companySeller","company","companyScore"],"paging":{"start":0,"rows":15}}&sourceDomain=Makaan
     */
    public void getSimilarProjects(Long projectId, int noOfSimilar){

        Selector similarProjectSel = new Selector();
        similarProjectSel.fields(new String[]{"projectId", "URL", "imageURL", "altText", "mainImage", "minPrice", "minResaleOrPrimaryPrice", "id", "city", "suburb", "label", "name", "type", "user", "contactNumbers", "locality", "contactNumber", "sellerId", "listingCategory", "property", "currentListingPrice", "price", "bedrooms", "bathrooms", "size", "unitTypeId", "project", "projectId", "studyRoom", "servantRoom", "poojaRoom", "companySeller", "company", "companyScore"});

        similarProjectSel.page(0,noOfSimilar);
    }

}
