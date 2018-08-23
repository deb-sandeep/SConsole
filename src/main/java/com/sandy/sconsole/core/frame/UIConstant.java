package com.sandy.sconsole.core.frame;

import java.awt.* ;
import java.text.* ;

public class UIConstant {

    public static final Font BASE_FONT = new Font( "Courier", Font.PLAIN, 20 ) ;
    public static final Font SCREENLET_TITLE_FONT = BASE_FONT.deriveFont( Font.BOLD, 60 ) ;

    public static final Color BG_COLOR   = Color.BLACK ;
    public static final Color FN_A_COLOR = Color.decode( "#3C979E" ) ;
    public static final Color FN_B_COLOR = Color.decode( "#7A9B2E" ) ;
    public static final Color FN_C_COLOR = Color.decode( "#B96F1B" ) ;
    public static final Color FN_D_COLOR = Color.decode( "#007C85" ) ;
    public static final Color FN_E_COLOR = Color.decode( "#95ADBE" ) ;
    public static final Color FN_F_COLOR = Color.decode( "#BB545F" ) ;
    public static final Color FN_G_COLOR = Color.decode( "#428131" ) ;
    public static final Color FN_H_COLOR = Color.decode( "#C5852F" ) ;
    
    public static final Color TILE_BORDER_COLOR = Color.DARK_GRAY.darker() ;
    
    public static final SimpleDateFormat DF_TIME_LG = new SimpleDateFormat( "H:mm:ss" ) ;
    public static final SimpleDateFormat DF_TIME_SM = new SimpleDateFormat( "H:mm" ) ;
}
