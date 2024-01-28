package alam.sos.sosalam.SetgetActivity;

public class AlamDataShowSetGet {
    private String backupId;
    private String dateId;
    private String imageulr;
    private String text;
    private String timeId;

    public AlamDataShowSetGet(){

    }

    public AlamDataShowSetGet(String backupId,String dateId,String imageulr,String text,String timeId){
        this.backupId =backupId;
        this.dateId =dateId;
        this.imageulr =imageulr;
        this.text =text;
        this.timeId = timeId;
    }

    public String getBackupId() {
        return backupId;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getImageulr() {
        return imageulr;
    }

    public void setImageulr(String imageulr) {
        this.imageulr = imageulr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }
}
