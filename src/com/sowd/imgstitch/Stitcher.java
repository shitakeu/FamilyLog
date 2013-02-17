package com.sowd.imgstitch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;

public class Stitcher {
	static double angle_to_pixel_muls = 200 ;

	
	
	public static Bitmap stitch(Bitmap[] bmps,double angle_d){
	    int img_num = bmps.length ;
	    IntBuffer[] ibs = new IntBuffer[img_num] ;
	    int[] xbases = new int[img_num+1] ;

	    for( int i=0;i<img_num;++i ){
	    	ibs[i] = getBmpBits(bmps[i]) ;
	    	xbases[i] = (int)(i * angle_d * angle_to_pixel_muls) ; 
	    }
	    xbases[img_num] = (int)(img_num * angle_d * angle_to_pixel_muls) ;
	    
	    img_tgt_w = xbases[img_num] + img_w ;
	    
	    Bitmap bm_tgt = Bitmap.createBitmap(img_tgt_w , img_h , img_config);
	    IntBuffer ib_tgt = IntBuffer.allocate(img_tgt_w*img_h) ;

	    for( int i=0;i<img_num;++i ){
	    	stitch_main(ibs[i],xbases[i],i==0?0:xbases[i-1]+img_w-xbases[i],ibs[(i+1)%img_num],xbases[i+1],ib_tgt) ;
	    }

	    ib_tgt.rewind();
	    bm_tgt.copyPixelsFromBuffer(ib_tgt);   
 
		return Bitmap.createBitmap(bm_tgt,img_w/2,0,img_tgt_w-img_w,img_h,null,false) ;
	}
	static protected int img_w , img_h ;
	static protected Bitmap.Config img_config ;
	static protected int img_tgt_w ;
	static protected void stitch_main( IntBuffer ibL , int xbaseL , int xbaseL_offs, IntBuffer ibR , int xbaseR , IntBuffer ibTgt ){
		for( int y=0;y<img_h;++y ){
			ibL.position( y*img_w ) ;
			ibR.position( y*img_w ) ;
			ibTgt.position( y*img_tgt_w + xbaseL ) ;
			int x = xbaseL ;
			for( ;x<xbaseL+xbaseL_offs;++x){
				ibL.get() ;
				ibTgt.get() ;
			}
			for( ; x < xbaseR ; ++x){
				if( x >= xbaseL+img_w ) break ;
				ibTgt.put( ibL.get() ) ;
			}
			for( ; x < xbaseL+img_w ; ++x ){
				float prop = (x-xbaseR)/(float)(xbaseL+img_w-xbaseR) ;
				ibTgt.put( blend(ibL.get(),ibR.get(),prop) ) ;
			}
			for( ; x < xbaseR+img_w ; ++x)
				ibTgt.put( ibR.get() ) ;
		}
	}
	static protected int blend(int in1, int in2,float prop) {
		float prop1 = 1-prop ;
		int r = (int)(Color.red(in1) * prop1 + Color.red(in2) * prop) ; 
		int g = (int)(Color.green(in1) * prop1 + Color.green(in2) * prop) ; 
		int b = (int)(Color.blue(in1) * prop1 + Color.blue(in2) * prop) ;
		return Color.rgb(r,g,b) ;
	}
	static protected IntBuffer getBmpBits(Bitmap bm){
	    img_w = bm.getWidth() ; 
	    img_h = bm.getHeight() ;
	    img_config = bm.getConfig() ;
	    IntBuffer ret = IntBuffer.allocate(img_w*img_h) ;
	    bm.copyPixelsToBuffer(ret);
	    
	    return ret ;
	}
}
