package com.example.donateme;
public class ReadWriteUserFeedbackHistory {
    public String urNm , description;

    public ReadWriteUserFeedbackHistory(String name, String description) {
        this.urNm = urNm;
        this.description = description;
    }

    public String getName() {
        return urNm;
    }

    public void setName(String urNm) {
        this.urNm = urNm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public ReadWriteUserFeedbackHistory (){};
}
