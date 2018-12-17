package RestaurantPackage;

import java.util.Date;

public class ManageDiners {

    private static int numberOfDiners = 0;
    public static Thread dinerThreads[];

    public static boolean stopDinerThreads[];
    public ManageDiners(int numDiners){
        numberOfDiners = numDiners;
        dinerThreads =  new Thread[numDiners];
        stopDinerThreads = new boolean[numDiners];
    }

    /* function to print the time difference from start of rest to further activity*/
    public static String returnTimeString(Date d1, Date d2){
        long diff = d1.getTime() - d2.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        String hour = diffHours >= 10 ? ""+diffHours : "0"+diffHours;
        String minutes = diffMinutes >= 10 ? ""+diffMinutes : "0"+diffMinutes;
        //String seconds = diffSeconds >= 10 ? ""+diffSeconds : "0"+diffSeconds;

        return hour + ":" + minutes;
    }

    public static void startDinerThreads(int dinerNumber, Date resStartTime){

        dinerThreads[dinerNumber] = new Thread(new Runnable() {
            @Override
            public void run() {
                stopDinerThreads[dinerNumber] = false;
                Date dinerEntryTime = new Date();
                String dinerEntryTimeStr = returnTimeString(dinerEntryTime, resStartTime);
                System.out.println(dinerEntryTimeStr + " - Diner " + (dinerNumber+1) + " has entered the restaurant");
                ManageTables.acquireTable(dinerNumber, resStartTime, stopDinerThreads);
            }
        });
        dinerThreads[dinerNumber].start();

        try{
            dinerThreads[dinerNumber].join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
