package com.inceptedapps.wasabi.ultimateworkouttimerforhiit.PrivateClasses;

/**
 * Created by Wasabi on 8/13/2016.
 */
public class Feedback {
    private String date;
    private String feedback;

    public Feedback(String date, String feedback) {
        this.date = date;
        this.feedback = feedback;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
