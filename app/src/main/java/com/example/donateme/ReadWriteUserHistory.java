package com.example.donateme;
public class ReadWriteUserHistory {
    public String item , description , category;

    public ReadWriteUserHistory(String item, String description, String category) {
        this.item = item;
        this.description = description;
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public ReadWriteUserHistory (){};
}
