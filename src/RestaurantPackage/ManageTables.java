package RestaurantPackage;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ManageTables {

    private static int numberOfTables = 0;

    private static Semaphore tableSemaphore;

    public ManageTables(Semaphore sem, int numOfTables){
        numberOfTables = numOfTables;
        tableSemaphore = sem;
    }

    private static int tableCount  = 0;


    public static void acquireTable(int dinerNumber, Date resStartTime, boolean stopDinerThreads[]){

        try{
            if(tableSemaphore.tryAcquire(2, TimeUnit.HOURS)){
                tableCount = tableCount +1;
                tableCount = (tableCount)%numberOfTables > 0 ? (tableCount)%numberOfTables : numberOfTables;
                Date dinerSeatedAt = new Date();
                String seatingTime = ManageDiners.returnTimeString(dinerSeatedAt, resStartTime);
                System.out.println(seatingTime + " - Diner " + (dinerNumber+1) + " seated on table " + tableCount);
                System.out.println("Trying to get the diner a cook");

                ManageCooks.assignCookToDiner(dinerNumber, tableSemaphore,resStartTime, stopDinerThreads);

            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
