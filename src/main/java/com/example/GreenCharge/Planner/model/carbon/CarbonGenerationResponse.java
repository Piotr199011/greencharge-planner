package com.example.GreenCharge.Planner.model.carbon;

import java.util.List;

public class CarbonGenerationResponse {

    private List<CarbonGenerationData> data;


    public List<CarbonGenerationData> getData() {
        return data;
    }

    public void setData(List<CarbonGenerationData> data) {
        this.data = data;
    }
}
/*storing the entire data report from the API in a ready-to-use form in Java*/