//@author Jon Warner
// used with permission :)

package com.proetsch.ann;

import java.io.File;
import java.util.Scanner;

class Parse {
    
//    this is only to test out the parse code, comment out when incorporating into a project
//    public static void main(String[] args){
//	Parse p = new Parse("1");
//        
//        for(int i=0; i<654; i++){
//            System.out.println("open "+p.getOpen(i)+" close "+p.getOpen(i)+" high "+p.getOpen(i)+" low "+p.getOpen(i)+" vol "+p.getVol(i));
//        }
//    }    
            

	
        
        
        double[] open = new double[654];
        double[] high = new double[654];
        double[] low = new double[654];
        double[] close = new double[654];
        //volume is a number of transactions, this will always be an integer number; however, the ANN will expect doubles, so we let that happen here instead of casting later...
        double[] volume = new double[654];
        
        //should be the same yyyy-mm-dd in all of the files... check this for adjusting alignment
        String EarliestDay = new String();
        
        Parse(String filename){
    
            try{		
				Scanner fin = new Scanner(new File(filename));
                
                fin.nextLine(); //remove header
                fin.useDelimiter(",|\n");
                    //654 days in the data
                    
                for(int i=0; i<654; i++){
                    EarliestDay=fin.next();
                    System.out.println("\nED "+EarliestDay+"\n");
                    open[653-i]=fin.nextDouble();
                    high[653-i]=fin.nextDouble();
                    low[653-i]=fin.nextDouble();
                    close[653-i]=fin.nextDouble();
                    volume[653-i]=fin.nextDouble();
                    fin.next();//thow away adj.close
                }
                for(int i=0; i<654; i++){
                    System.out.println("C"+close[i]);
                }
                
		}catch(Exception e){System.out.println("Bummer, shucks, darn!\n"+e);}
    
        }
        
        //high of day i
        public double getHigh(int i){
            return high[i];
        }
        
        //low of day i
        public double getLow(int i){
            return low[i];
        }
        
        //open of day i
        public double getOpen(int i){
            return open[i];
        }
        
        //closing of day i
        public double getClose(int i){
            return close[i];
        }
        
        //number of transactions for this stock
        public double getVol(int i){
            return volume[i];
        }
        
        //all of the parsed files should have the same first day, all days "i" are days after this date, so get__(o) returns the __ of this day, get__(1) gets the __ of one day after this day
        public String getFirstDay(){
            return EarliestDay;
        }
}
