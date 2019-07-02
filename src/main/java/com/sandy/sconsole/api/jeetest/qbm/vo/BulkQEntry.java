package com.sandy.sconsole.api.jeetest.qbm.vo;

public class BulkQEntry {

    private String  qRef     = "" ;
    private String  imgName  = "" ;
    private String  qType    = "SCA" ;
    private String  aText    = "" ;
    private Integer latLevel = 3 ;
    private Integer projTime = 120 ;
    private Boolean saved    = false ;
    private String  imgPath  = "" ;
    
    public String getqRef() {
        return qRef ;
    }
    public void setqRef( String qRef ) {
        this.qRef = qRef ;
    }
    
    public String getImgName() {
        return imgName ;
    }
    public void setImgName( String imgName ) {
        this.imgName = imgName ;
    }
    
    public String getqType() {
        return qType ;
    }
    public void setqType( String qType ) {
        this.qType = qType ;
    }
    
    public String getaText() {
        return aText ;
    }
    public void setaText( String aText ) {
        this.aText = aText ;
    }
    
    public Integer getLatLevel() {
        return latLevel ;
    }
    public void setLatLevel( Integer latLevel ) {
        this.latLevel = latLevel ;
    }
    
    public Integer getProjTime() {
        return projTime ;
    }
    public void setProjTime( Integer projTime ) {
        this.projTime = projTime ;
    }
    
    public Boolean getSaved() {
        return saved ;
    }
    public void setSaved( Boolean saved ) {
        this.saved = saved ;
    }
    
    public String getImgPath() {
        return imgPath ;
    }
    public void setImgPath( String imgPath ) {
        this.imgPath = imgPath ;
    }
}
