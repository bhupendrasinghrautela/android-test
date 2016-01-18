package com.makaan.response.pyr;

/**
 * Created by makaanuser on 9/1/16.
 */
public class TopAgentsData {

    int userId;
    private String name;
    CompanyData company;

    public CompanyData getCompany() {
        return company;
    }

    public void setCompany(CompanyData company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public class CompanyData{
        float score;

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }
    }

}
