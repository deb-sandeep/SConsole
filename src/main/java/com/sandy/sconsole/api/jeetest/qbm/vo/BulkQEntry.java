package com.sandy.sconsole.api.jeetest.qbm.vo;

import java.util.ArrayList ;
import java.util.List ;

import com.sandy.sconsole.api.jeetest.qbm.helper.BulkQuestionEntryHelper.FileInfo ;

public class BulkQEntry {

    private String  qRef     = "" ;
    private String  qType    = "SCA" ;
    private String  aText    = "" ;
    private Integer latLevel = 3 ;
    private Integer projTime = 120 ;
    private Boolean saved    = false ;
    
    private List<String> imgPaths  = new ArrayList<>() ;
    private List<String> imgNames  = new ArrayList<>() ;
    
    public BulkQEntry() {
    }
    
    public BulkQEntry( FileInfo fi ) {
        this.qRef = fi.qRef ;
    }
    
    public String getqRef() {
        return qRef ;
    }
    public void setqRef( String qRef ) {
        this.qRef = qRef ;
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

    public List<String> getImgNames() {
        return imgNames ;
    }

    public void setImgNames( List<String> imgNames ) {
        this.imgNames = imgNames ;
    }

    public List<String> getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths( List<String> imgPaths ) {
        this.imgPaths = imgPaths;
    }
}
