package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FCMData implements Serializable {
    @SerializedName("data_title")
    private String dataTitle;
    @SerializedName("data_content")
    private String dataContent;

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getDataContent() {
        return dataContent;
    }

    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }
}
