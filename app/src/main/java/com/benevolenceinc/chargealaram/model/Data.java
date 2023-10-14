package com.benevolenceinc.chargealaram.model;


/**
 * Created by Hitesh on 3/4/19.
 */
public class Data {
    private int _id;
    private int percentage;
    private int status;
    private int once_notified;

    public Data(int _id, int percentage, int status, int once_notified) {
        this._id = _id;
        this.percentage = percentage;
        this.status = status;
        this.once_notified = once_notified;
    }

    public int getOnce_notified() {
        return once_notified;
    }

    public void setOnce_notified(int once_notified) {
        this.once_notified = once_notified;
    }



    public Data() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
