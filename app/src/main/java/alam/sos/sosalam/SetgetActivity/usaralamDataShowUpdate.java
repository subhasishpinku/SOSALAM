package alam.sos.sosalam.SetgetActivity;

public class usaralamDataShowUpdate {
    private String backupId;
    private String text;
    private String dateId;
    private String timeId;
    private String Imageulr;
    public usaralamDataShowUpdate(){

    }

    public usaralamDataShowUpdate(String backupId,String text,String dateId,String timeId,String Imageulr){
        this.backupId=backupId;
        this.text = text;
        this.dateId = dateId;
        this.timeId= timeId;
        this.Imageulr=Imageulr;
    }

    public String getBackupId() {
        return backupId;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }

    public String getImageulr() {
        return Imageulr;
    }

    public void setImageulr(String imageulr) {
        Imageulr = imageulr;
    }
}
