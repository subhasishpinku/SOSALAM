package alam.sos.sosalam.SetgetActivity;

public class RegisterSetGet {
    private String backuupID;
    private String username;
    private String dsignation;
    private String phonenumber;
    private String whatsappno;
    private String emaino1;
    private String emaino2;
    private String Imgurl;
    private String flag;
    private boolean isSelected;
    public RegisterSetGet(){

    }
    public RegisterSetGet(String backuupID,String username,String dsignation,String phonenumber,String whatsappno,String emaino1,String emaino2,String Imgurl,String flag){
        this.backuupID=backuupID;
        this.username =username;
        this.dsignation =dsignation;
        this.phonenumber=phonenumber;
        this.whatsappno=whatsappno;
        this.emaino1=emaino1;
        this.emaino2=emaino2;
        this.Imgurl=Imgurl;
        this.flag=flag;
    }

    public String getBackuupID() {
        return backuupID;
    }

    public void setBackuupID(String backuupID) {
        this.backuupID = backuupID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDsignation() {
        return dsignation;
    }

    public void setDsignation(String dsignation) {
        this.dsignation = dsignation;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getWhatsappno() {
        return whatsappno;
    }

    public void setWhatsappno(String whatsappno) {
        this.whatsappno = whatsappno;
    }

    public String getEmaino1() {
        return emaino1;
    }

    public void setEmaino1(String emaino1) {
        this.emaino1 = emaino1;
    }

    public String getEmaino2() {
        return emaino2;
    }

    public void setEmaino2(String emaino2) {
        this.emaino2 = emaino2;
    }

    public String getImgurl() {
        return Imgurl;
    }

    public void setImgurl(String imgurl) {
        Imgurl = imgurl;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
