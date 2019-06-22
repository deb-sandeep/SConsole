package com.sandy.sconsole.api.jeetest.qbm;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.codec.binary.Hex ;
import org.apache.commons.io.FileUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.sconsole.SConsole ;
import com.sandy.sconsole.dao.entity.master.TestQuestion ;
import com.sandy.sconsole.util.QuestionTextFormatter ;

public class TestQuestionEx extends TestQuestion {
    
    public static class ImageData {
        private String imageName = null ;
        private String hexEncodedImageData = null ;
        
        public String getImageName() {
            return imageName ;
        }
        public void setImageName( String imageName ) {
            this.imageName = imageName ;
        }
        
        public String getHexEncodedImageData() {
            return hexEncodedImageData ;
        }
        public void setHexEncodedImageData( String imgData ) {
            this.hexEncodedImageData = imgData ;
        }
    }
    
    private List<ImageData> embeddedImages = new ArrayList<>() ;
    
    public TestQuestionEx() {
    }

    public TestQuestionEx( TestQuestion tq ) 
        throws Exception {
        
        setSubject               ( tq.getSubject()               ) ;
        setTopic                 ( tq.getTopic()                 ) ;
        setBook                  ( tq.getBook()                  ) ;
        setHash                  ( tq.getHash()                  ) ;
        setTargetExam            ( tq.getTargetExam()            ) ;
        setQuestionType          ( tq.getQuestionType()          ) ;
        setQuestionRef           ( tq.getQuestionRef()           ) ;
        setLateralThinkingLevel  ( tq.getLateralThinkingLevel()  ) ;
        setProjectedSolveTime    ( tq.getProjectedSolveTime()    ) ;
        setLctContext            ( tq.getLctContext()            ) ;
        setQuestionText          ( tq.getQuestionText()          ) ;
        setQuestionFormattedText ( tq.getQuestionFormattedText() ) ;
        setAnswerText            ( tq.getAnswerText()            ) ;
        
        populateAttachments( getQuestionText() ) ;
        if( StringUtil.isNotEmptyOrNull( getLctContext() ) ) {
            populateAttachments( getLctContext() ) ;
        }
    }
    
    private void populateAttachments( String questionText ) 
        throws Exception {
        
        QuestionTextFormatter qtf = new QuestionTextFormatter() ;
        List<String> imageNames = qtf.getEmbeddedImageNames( questionText ) ;
        
        if( !imageNames.isEmpty() ) {
            for( String imgName : imageNames ) {
                
                File imgFile = new File( SConsole.JEETEST_IMG_DIR, imgName ) ;
                if( !imgFile.exists() ) {
                    throw new Exception( "Invalid embedded image found. " + 
                          "Name = " + imgName + ". Aborting sync." ) ;
                }
                
                byte[] fileData = FileUtils.readFileToByteArray( imgFile ) ;
                String hexed = Hex.encodeHexString( fileData ) ;
                
                ImageData imgData = new ImageData() ;
                imgData.setImageName( imgName ) ;
                imgData.setHexEncodedImageData( hexed ) ;
                
                this.embeddedImages.add( imgData ) ;
            }
        }
    }

    public List<ImageData> getEmbeddedImages() {
        return embeddedImages ;
    }

    public void setEmbeddedImages( List<ImageData> embeddedImages ) {
        this.embeddedImages = embeddedImages ;
    }

    public void populate( TestQuestion tq ) {
        
        tq.setSubject               ( this.getSubject()               ) ;
        tq.setTopic                 ( this.getTopic()                 ) ;
        tq.setBook                  ( this.getBook()                  ) ;
        tq.setHash                  ( this.getHash()                  ) ;
        tq.setTargetExam            ( this.getTargetExam()            ) ;
        tq.setQuestionType          ( this.getQuestionType()          ) ;
        tq.setQuestionRef           ( this.getQuestionRef()           ) ;
        tq.setLateralThinkingLevel  ( this.getLateralThinkingLevel()  ) ;
        tq.setProjectedSolveTime    ( this.getProjectedSolveTime()    ) ;
        tq.setLctContext            ( this.getLctContext()            ) ;
        tq.setQuestionText          ( this.getQuestionText()          ) ;
        tq.setQuestionFormattedText ( this.getQuestionFormattedText() ) ;
        tq.setAnswerText            ( this.getAnswerText()            ) ;
    }
}
