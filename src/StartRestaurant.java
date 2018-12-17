import java.util.Date;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import RestaurantPackage.*;

public class StartRestaurant {

    private static int numberOfDiners = 0;

    private static int numberOfCooks = 0;

    private static int numberOfTables = 0;

    private static int restOpenDuration = 0;

    private static int burgers[];
    private static int fries[];
    private static int cokes[];

    private static int dinerArrivalTim[];

    int timeLapsed = 0;

    public static boolean getAllTheInputs(){

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the duration of more than 30 mins " +
                "for which the res will be open (in minutes)..");
        int durValue = sc.nextInt();
        if(durValue < 30){
            System.out.println("Please enter duration of more than 30 mins..\n Closing restaurant..Please try" +
                    " opening again");
            return false;
        }else{
            restOpenDuration = durValue;
        }

        System.out.println("Enter the number of diners who would like to eat the restaurant..");
        int numDiners = sc.nextInt();
        if(numDiners < 1){
            System.out.println("Please enter valid number of diners...\n Closing restaurant..Please try" +
                    " opening again");
            return false;
        }else{
            numberOfDiners = numDiners;
        }

        System.out.println("Number tables available..");

        int numTables = sc.nextInt();
        if(numTables < 1){
            System.out.println("Please enter valid number of tables to sit...\n Closing restaurant..Please try" +
                    " opening again");
            return false;
        }else{
            numberOfTables = numTables;
        }

        System.out.println("Number of cooks available at restaurant..");
        int numCooks = sc.nextInt();
        if(numCooks< 1){
            System.out.println("Please enter 1 or more cooks..\n Closing restaurant..Please try" +
            " opening again");
            return false;
        }else{
            numberOfCooks = numCooks;
        }

        burgers = new int[numberOfDiners];
        fries = new int[numberOfDiners];
        cokes = new int[numberOfDiners];
        dinerArrivalTim = new int[numberOfDiners];

        System.out.println("Let's enter information for each diner");

        for(int i=0;i<numberOfDiners;i++){
            int lastTimeInput =  i==0 ? 0 : dinerArrivalTim[i-1];

            //burgers > 0
            // fries >= 0
            //0<=coke<=1
            //Time >= 0
            System.out.println("Details for diner "+ (i+1));
            System.out.println("Time at which diner "+(i+1)+" enters (in minutes): ");
            int timeEntered = sc.nextInt();

            if(timeEntered <0 || timeEntered > restOpenDuration-30 || timeEntered < lastTimeInput){
                System.out.println("Ooops you entered a wrong value for time...\n Closing the restaurant " +
                 "Try opening again ");
                 return false;
            }else{
                dinerArrivalTim[i] = timeEntered;
            }

            System.out.println("Number of Burgers for diner "+(i+1)+" : ");
            int numOfBurgers = sc.nextInt();
            if(numOfBurgers < 1){
                System.out.println("Please order more than one burger..\n Closing the restaurant..Please try opening again");
                break;
            }else{
                burgers[i] = numOfBurgers;
            }

            System.out.println("Fries for diner "+(i+1)+" : ");
            int numOfFries = sc.nextInt();
            if(numOfFries < 0){
                System.out.println("Please order positive number of fries..\n Closing the restaurant..Please try opening again");
                return false;
            }else{
                fries[i] = numOfFries;
            }

            System.out.println("Coke for diner "+(i+1)+" : ");
            int numOfCokes = sc.nextInt();
            if(numOfCokes < 0 || numOfCokes > 1){
                System.out.println("Please order either 1 or 0 soda..\n Closing the restaurant..Please try opening again");
                return false;
            }else{
                cokes[i] = numOfCokes;
            }

            System.out.println("******Diner " + (i+1) + " over****");
            System.out.println();
        }
        return true;
    }

    /*
    * start diner threads using user inputs
    * */
    public void startMainRestaurantThread(){

        Semaphore tableSemaphore = new Semaphore(numberOfTables);
        Semaphore cookSemaphore = new Semaphore(numberOfCooks);

        ManageTables manageTables = new ManageTables(tableSemaphore, numberOfTables);

        ManageCooks manageCooks = new ManageCooks(cookSemaphore, numberOfCooks, burgers, fries, cokes);

        ManageDiners manageDiners = new ManageDiners(numberOfDiners);

        Date resStartTime = new Date();

        Timer resMainThreadTimer = new Timer();
        TimerTask resMainTaskThread = new TimerTask(){

            public void run(){

                for(int i=0;i<numberOfDiners;i++){
                    if(timeLapsed == dinerArrivalTim[i]){

                        // call the static function to start diner thread
                        ManageDiners.startDinerThreads(i, resStartTime);
                    }
                }

                if(timeLapsed == restOpenDuration){
                    System.out.println("Restaurant duration over....Closing the restaurant");
                    this.cancel();
                    resMainThreadTimer.cancel();
                    return;
                }
                timeLapsed++;
            }
        };

        resMainThreadTimer.scheduleAtFixedRate(resMainTaskThread, 1000, 1000*60);
    }

    public static void main(String args[]) throws InterruptedException{
        //Main starter thread starts by taking all inputs

        System.out.println("Lets's start the restaurant");

        /* start the diner threads if all inputs are valid. Else exit*/
        if(getAllTheInputs()){
            StartRestaurant sr = new StartRestaurant();
            sr.startMainRestaurantThread();
        }else {
           System.out.println("Have a great day...Bye bye!!!");
        }
    }

}

