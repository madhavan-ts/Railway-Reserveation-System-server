package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.models.CompartmentSeat;

public class BookingLogic {

//    public int[][] compartmentAvailableSeats;
    public List<List<CompartmentSeat>> seatAvailability = new ArrayList<>();

    private void combinationUtil(List<List<CompartmentSeat>> combarr, List<CompartmentSeat> arr, List<CompartmentSeat> data, int start,
                                        int end, int index, int r, int[] pref)
    {
        if (index == r)
        {
            List<CompartmentSeat> a = new ArrayList<>(r);
            for (int i = 0; i < r; i++) {
                 a.add(data.get(i));
            }
            combarr.add(a);
            return;
        }
        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            data.set(index,arr.get(i));
            combinationUtil(combarr,arr, data, i+1, end, index+1, r,pref);
        }
    }
   
    private boolean checkACFirstclassPref(List<CompartmentSeat> a, int[] pref) {
        int lowerpref = pref[0];
        int upperpref = pref[2];
        for (int i = 0; i < a.size(); i++) {
            int x = a.get(i).getS();
			if( x %4==1 || x%4 == 3) lowerpref--;
            else if( x %4==2 || x%4 == 0)upperpref--; 
        }
        if(lowerpref<= 0 && upperpref <= 0)
            return true;
        return false;
    }
    
    
    private boolean checkSleeperPref(List<CompartmentSeat>a, int[] pref) {
        int lowerpref = pref[0];
        int middlepref = pref[1];
        int upperpref = pref[2];
        for (int i = 0; i < a.size(); i++) {
            int x = a.get(i).getS();
			if( x %8==1 || x%8 == 4 || x%8==7) lowerpref--;
            else if( x %8==2 || x%8 == 5) middlepref--;
            else if(x %8==6 || x%8 == 3 || x%8==0) upperpref--;
        }
        if(lowerpref<= 0 && upperpref <= 0 && middlepref <= 0)
            return true;
        return false;
    }
    
    private boolean check2TierPreference(List<CompartmentSeat> a,int[] pref) {
    	int lowerpref = pref[0];
        int upperpref = pref[2];
        for (int i = 0; i < a.size(); i++) {
            int j = a.get(i).getS();
			if( j %6==1 || j%6 == 5 ||  j%6 == 3  ) lowerpref--;
            else if(j %6==6 || j%6 == 2 || j%6==4 ) upperpref--;
        }
        if(lowerpref<= 0 && upperpref <= 0)
            return true;
        return false;
    }
    
    private boolean check2SeatingPreference(List<CompartmentSeat> a,int[]pref) {
    	int window = pref[0];
        int middle = pref[1];
        int asile = pref[2];
        for (int i = 0; i < a.size(); i++) {
        	
            int j = a.get(i).getS();
			switch(j%12) {
	            case 1:
	            case 6:
	            case 7:
	            case 0:
	            		window--;break;
	            case 2:
	            case 5:
	            case 11:
	            case 8:
	            		middle--;break;
	            case 3:
	            case 4:
	            case 9: 
	            case 10:
	            		asile--;break;
            }
            
        }
        if(window<= 0 && middle<= 0 && asile<=0)
            return true;
        return false;
    }

    
    public List<CompartmentSeat> findSeatAvailability(List<CompartmentSeat> arr, int[] pref,String className, int noOfSeats) {
        
        int n = arr.size();
        List<List<CompartmentSeat>> combarr = new ArrayList<>();
        CompartmentSeat[] tmp = new CompartmentSeat[noOfSeats];
        List<CompartmentSeat> data = Arrays.asList(tmp);
        List<List<CompartmentSeat>> finalprefarr = new ArrayList<>();
        combinationUtil(combarr,arr, data, 0, n-1, 0, noOfSeats,pref);
//        System.out.println(combarr.toString());
//        System.out.println("Comb arr"+combarr.toString());
//         Check the all possible combination for available with preference array
        for (List<CompartmentSeat> a:
                combarr) {
            if(checkPref(a,pref,className)){
                a.sort(new SortBySeat());	
                finalprefarr.add(a);
            }
        }
//        System.out.println("Final Pref Array  = ,"+finalprefarr.toString());
        // sends it to make the final preference seats to find the minimum distance . ie the closest seats
//        System.out.println("Final pref "+finalprefarr.toString());
        if (minimumDistanceSeats(finalprefarr)) return finalprefarr.get(0);
        
        
        
        // if prefered seats are not available send to retrieve the
        //  After removing preference checks all possible combination and then add it to the final array
        int[] newpref ={0,0,0};
        for (List<CompartmentSeat> a:
                combarr) {
            if(checkPref(a, newpref,className)){
                a.sort(null);
                finalprefarr.add(a);
            }
        }
        
//        System.out.println("Final Pref"+finalprefarr);
        combarr = null;
        System.gc();
        
        
        
        //if prefered seats
        if (minimumDistanceSeats(finalprefarr)) return finalprefarr.get(0);
        return null;
    }
    private boolean checkPref(List<CompartmentSeat> arr,int[] pref,String className) {
    	if(className.equals("SLEEPER")) {
    		return checkSleeperPref(arr, pref);
    	}else if(className.equals("AC FIRST CLASS (1AC)")) {
    		return checkACFirstclassPref(arr, pref);
    	}else if(className.equals("AC TWO TIER (2AC)")) {
    		return check2TierPreference(arr, pref);
    	}else if(className.equals("AC THREE TIER (3AC)")) {
    		return checkSleeperPref(arr, pref);
    	}else if(className.equals("FIRST CLASS (FC)")) {
    		return checkACFirstclassPref(arr, pref);
    	}else if(className.equals("SECOND SITTING (2S)")) {
    		return check2SeatingPreference(arr, pref);
    	}
    	return false;
    }

    private boolean minimumDistanceSeats(List<List<CompartmentSeat>> finalprefarr) {
        // Initially checks the seat availability with preference
        // if the prefered seats are available then sends it to the find the minimum
        if(finalprefarr.size()!=0){
            int minDistance = Byte.MAX_VALUE;
            for (int i = 0; i < finalprefarr.size(); i++) {
                int distance  = finalprefarr.get(i).get(finalprefarr.get(i).size()-1).getS() -  finalprefarr.get(i).get(0).getS();
                if(distance < minDistance){
                    minDistance = distance;
                }
            }
            for (int i = 0; i < finalprefarr.size(); i++) {
                int distance  = finalprefarr.get(i).get(finalprefarr.get(i).size()-1).getS() -  finalprefarr.get(i).get(0).getS();
                if(distance!=minDistance){
                    // removes the non-minimum distance seats
                    finalprefarr.remove(i);
                    i--;
                }
            }
            // and tells that the minimum distance seats are availble
            return true;
        }
        return false;
    }

    public List<CompartmentSeat> Book(List<List<CompartmentSeat>> availableSeats,int[] prefs,String className,int noOfSeats) {
//        int[] pref= {1,2,2};
        int i = 1;
//        HashMap<Byte,List<CompartmentSeat>> map = new HashMap<Byte,List<CompartmentSeat>>();
        int minimumdist = Byte.MAX_VALUE;
        for (List<CompartmentSeat> compartment:
             availableSeats) {
            List<CompartmentSeat> seats  = findSeatAvailability(compartment,prefs,className,noOfSeats);
            if(seats != null){
                int dis = seats.get(seats.size()-1).getS() - seats.get(0).getS();
                if(minimumdist > dis){
                    minimumdist = dis;
                }
                seatAvailability.add(seats);
            }i++;
            
            
        }
        List<CompartmentSeat> allotedSeats = null;
        int ind = 0;
        int startCompartment = Integer.MAX_VALUE;
        
        if(seatAvailability.isEmpty()){
            allotedSeats = new ArrayList<CompartmentSeat>(noOfSeats);
            for (int j = 0; ind < noOfSeats && availableSeats.size()>0 && j < availableSeats.size(); j++) {
                for (int k = 0; ind < noOfSeats && availableSeats.get(j).size() > 0 && k < availableSeats.get(j).size(); k++) {
                    allotedSeats.add(availableSeats.get(j).get(k));
                    ind++;
                }
                	
            }
            
        }
        for (List<CompartmentSeat> e: seatAvailability) {
            if((e.get(e.size()-1).getS() - e.get(0).getS()) == minimumdist){
//                System.out.println("Compartment : "+e.getKey() +" " +e.getValue().toString());
//                startCompartment = e.getKey();
                allotedSeats = e;
                break;
            }
        }
//        System.out.println(startCompartment);

//        System.out.println(allotedSeats.toString());
        
        return allotedSeats;
    }


}

class SortBySeat implements Comparator<CompartmentSeat> {
	 
    // Method
    // Sorting in ascending order of roll number
    public int compare(CompartmentSeat a, CompartmentSeat b)
    {
 
        return a.getS() - b.getS();
    }
}