package com.makaan.service;

import com.google.gson.reflect.TypeToken;
import com.makaan.constants.ApiConstants;
import com.makaan.event.project.ProjectByIdEvent;
import com.makaan.event.project.SimilarProjectGetEvent;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.network.ObjectGetCallback;
import com.makaan.request.selector.Selector;
import com.makaan.response.project.Project;
import com.makaan.response.project.ProjectConfigCallback;
import com.makaan.util.AppBus;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by vaibhav on 23/01/16.
 */
public class ProjectService implements MakaanService {


    /**
     * http://marketplace-qa.proptiger-ws.com/app/v4/project-detail/506147?sourceDomain=Makaan
     * with specifications
     * http://mp-qa1.makaan-ws.com/app/v4/project-detail/506417?sourceDomain=Makaan
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
                    project.getFormattedSpecifications();
                    AppBus.getInstance().post(new ProjectByIdEvent(project));
                }
            }

            );
        }
    }


    /**
     * http://marketplace-qa.makaan-ws.com/data/v2/recommendation?type=similar&projectId=506147&selector={"fields":["projectId","URL","imageURL","altText","mainImage","minPrice","minResaleOrPrimaryPrice","id","city","suburb","label","name","type","user","contactNumbers","locality","contactNumber","sellerId","listingCategory","property","currentListingPrice","price","bedrooms","bathrooms","size","unitTypeId","project","projectId","studyRoom","servantRoom","poojaRoom","companySeller","company","companyScore"],"paging":{"start":0,"rows":15}}&sourceDomain=Makaan
     */
    public void getSimilarProjects(final Long projectId, int noOfSimilar) {
        if (null != projectId) {
            final Selector similarProjectSel = new Selector();
            similarProjectSel.fields(new String[]{"projectId", "URL", "imageURL", "altText", "mainImage", "minPrice", "minResaleOrPrimaryPrice", "id", "city", "suburb", "label", "name", "type", "user", "contactNumbers", "locality", "contactNumber", "sellerId", "listingCategory", "property", "currentListingPrice", "price", "bedrooms", "bathrooms", "size", "unitTypeId", "project", "projectId", "studyRoom", "servantRoom", "poojaRoom", "companySeller", "company", "companyScore"});

            similarProjectSel.page(0, noOfSimilar);
            StringBuilder similarProjectUrl = new StringBuilder(ApiConstants.SIMILAR_PROJECT);
            similarProjectUrl.append("?type=similar&projectId=").append(projectId.toString()).append("&").append(similarProjectSel.build());


            Type projectListType = new TypeToken<ArrayList<Project>>() {
            }.getType();


            MakaanNetworkClient.getInstance().get(similarProjectUrl.toString(), projectListType, new ObjectGetCallback() {
                @Override
                @SuppressWarnings("unchecked")
                public void onSuccess(Object responseObject) {
                    ArrayList<Project> projectList = (ArrayList<Project>) responseObject;
                    AppBus.getInstance().post(new SimilarProjectGetEvent(projectId, projectList));
                }
            }, true);
        }
    }


    /**
     * http://mp-qa1.makaan-ws.com/app/v1/project-configuration/500773?sourceDomain=Makaan
     */
    public void getProjectConfiguration(Long projectId) {

        if (null != projectId) {
            String projectConfigUrl = ApiConstants.PROJECT_CONFIG.concat("/").concat(projectId.toString());
            MakaanNetworkClient.getInstance().get(projectConfigUrl, new ProjectConfigCallback());
        }
    }

}
