package it.unical.ea_project_javafx.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BookingModel {

    private final StringProperty activity;
    private final StringProperty dateTime;
    private final StringProperty location;
    private final StringProperty status;

    public BookingModel(String activity, String dateTime, String location, String status) {
        this.activity = new SimpleStringProperty(activity);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.location = new SimpleStringProperty(location);
        this.status = new SimpleStringProperty(status);
    }

    public StringProperty activityProperty() {
        return activity;
    }

    public StringProperty dateTimeProperty() { 
        return dateTime; 
    }

    public StringProperty locationProperty() { 
        return location; 
    }
    
    public StringProperty statusProperty() { 
        return status; 
    }

}