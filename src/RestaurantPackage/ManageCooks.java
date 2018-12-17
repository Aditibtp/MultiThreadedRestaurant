package RestaurantPackage;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManageCooks {

    private enum Machines  {
        BURGER, FRIES, SODA;
    }

    private static int numberOfCooks = 0;

    private static int burgerOrders[];
    private static int friesOrders[];
    private static int sodaOrders[];

    /* creating locks for all 3 machines so that it can be used only by one cook at a time */
    private static Lock burgerMachineLock = new ReentrantLock();

    private static Lock friesMachineLock = new ReentrantLock();

    private static Lock sodaMachineLock = new ReentrantLock();

    private static Semaphore cookSemaphore;

    public ManageCooks(Semaphore sem, int numCooks, int[] burgers, int[] fries, int[] sodas){
        cookSemaphore = sem;
        numberOfCooks = numCooks;
        burgerOrders = burgers;
        friesOrders = fries;
        sodaOrders = sodas;
    }

    public static void assignCookToDiner(int dinerNumber, Semaphore tableSemaphore, Date resStartTime, boolean stopDinerThreads[]){
        boolean soda = sodaOrders[dinerNumber] == 1;
        int cookNumber = numberOfCooks - cookSemaphore.availablePermits();
        try{
            if(cookSemaphore.tryAcquire(2, TimeUnit.HOURS)){

                Date cookAssignedTime = new Date();
                String cookAssignStr = ManageDiners.returnTimeString(cookAssignedTime, resStartTime);

                Thread cookThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //because cook can operate only one machine at a time
                        //so once the cook acquires the machine he cannot use other machines
                        Lock cookLock = new ReentrantLock();

                        boolean burgersDone = false;
                        boolean friesDone = false;
                        boolean sodaDone = !soda;

                        System.out.println(cookAssignStr + " - Cook " + (cookNumber +1) + " processes diner " + (dinerNumber+1) + "'s order");

                        while(!sodaDone || ! burgersDone || !friesDone){
                            if(!burgersDone && burgerMachineLock.tryLock() && cookLock.tryLock()){

                                Date burgerMachineTime = new Date();
                                String burgerMachineTimeStr = ManageDiners.returnTimeString(burgerMachineTime, resStartTime);

                                String food = Machines.BURGER.toString();
                                System.out.println(burgerMachineTimeStr +"-cook " +(cookNumber +1) + " uses the " + food + " machine for diner "+ (dinerNumber +1));

                                Thread machineThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            Thread.sleep(burgerOrders[dinerNumber]*5*60*1000);
                                        } catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                machineThread.start();
                                try {
                                    machineThread.join();
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                }

                                burgersDone = true;
                                burgerMachineLock.unlock();
                                cookLock.unlock();
                            }

                            if(!friesDone && friesMachineLock.tryLock() && cookLock.tryLock()){

                                Date friesMachineTime = new Date();
                                String friesMachineStr = ManageDiners.returnTimeString(friesMachineTime, resStartTime);

                                String food = Machines.FRIES.toString();
                                System.out.println(friesMachineStr +"-cook " +(cookNumber +1) + " uses the " + food + " machine for diner "+ (dinerNumber +1));
                                Thread machineThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            Thread.sleep(friesOrders[dinerNumber]*60*3*1000);
                                        } catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                machineThread.start();
                                try {
                                    machineThread.join();
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                }

                                friesDone = true;
                                friesMachineLock.unlock();
                                cookLock.unlock();
                            }

                            if(!sodaDone && sodaMachineLock.tryLock() && cookLock.tryLock()){

                                Date sodaMachineTime = new Date();
                                String sodaMachineTimeStr = ManageDiners.returnTimeString(sodaMachineTime, resStartTime);

                                String food = Machines.SODA.toString();
                                System.out.println(sodaMachineTimeStr +"-cook " +(cookNumber +1) + " uses the " + food + " machine for diner "+ (dinerNumber +1));

                                Thread machineThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            Thread.sleep(60*1000);
                                        } catch (InterruptedException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                machineThread.start();
                                try {
                                    machineThread.join();
                                } catch (InterruptedException e){
                                    e.printStackTrace();
                                }

                                sodaDone = true;
                                sodaMachineLock.unlock();
                                cookLock.unlock();
                            }
                        }

                        // the diner's order is ready
                        if(sodaDone && burgersDone && friesDone){

                            cookSemaphore.release();

                            Thread dinerEatingThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Date dinerEatingStart = new Date();
                                    String dinerEatingTimStr = ManageDiners.returnTimeString(dinerEatingStart, resStartTime);
                                    System.out.println(dinerEatingTimStr + "-Diner "+ (dinerNumber +1) + " food is ready");
                                    System.out.println(dinerEatingTimStr + "-Diner "+ (dinerNumber +1) + " started eating");
                                    try {
                                        Thread.sleep(30*60*1000);
                                    } catch (InterruptedException e){
                                        e.printStackTrace();
                                    }
                                }
                            });

                            dinerEatingThread.start();
                            try {
                                dinerEatingThread.join();
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }

                            tableSemaphore.release();

                            Date dinerEatingDone = new Date();
                            String dinerEatingDoneStr = ManageDiners.returnTimeString(dinerEatingDone, resStartTime);

                            System.out.println(dinerEatingDoneStr + "-Diner "+ (dinerNumber +1) + " is done eating the delicious meal and is leaving the restaurant");
                            stopDinerThreads[dinerNumber] = true;
                            if(dinerNumber+1 == stopDinerThreads.length){
                                System.out.println(dinerEatingDoneStr + "-last diner left the restaurant");
                            }
                        }
                    }
                });
                cookThread.start();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }

    }

}
