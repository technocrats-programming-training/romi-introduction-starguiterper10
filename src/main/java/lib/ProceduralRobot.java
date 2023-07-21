package lib;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DSControlWord;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;

public abstract class ProceduralRobot extends RobotBase {

    Thread autoThread;

    private class AutonomousRunnable implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println("Starting Autonomous Procedure");
                autonomousProcedure();
            } catch (InterruptedException e) {
                System.out.println("Autonomous Disabled");
            }
        }
    }


    @Override
    public void startCompetition() {
        Romi.initRomiSubsystems();
        System.out.println("***** Procedural Robot Program Initialization Finished *****");
        HAL.observeUserProgramStarting(); // Tell the HAL process (which will tell the driver station) that the robot is
            // ready to be enabled. This is what causes the driver station to change from "No Robot Code" to "Teleoperation Disabled"



        DSControlWord controlWord = new DSControlWord(); // Container for Driver Station control data (is enabled?, etc)

        boolean isAlreadyAutonomous = false; // Was the robot in autonomous mode on the last iteration of the while loop

        while (true) {
            controlWord.update(); // Fetch new data from the driver station
            try {
                if (controlWord.isDisabled()) {
                    HAL.observeUserProgramDisabled();
                    if (isAlreadyAutonomous) {
                        autoThread.interrupt();
                        isAlreadyAutonomous = false;
                    }
                    sleep(20);
                } else if (controlWord.isTeleop()) {
                    HAL.observeUserProgramTeleop();
                    if (isAlreadyAutonomous) {
                        autoThread.interrupt();
                        isAlreadyAutonomous = false;
                    }
                    sleep(20);
                } else if (controlWord.isTest()) {
                    HAL.observeUserProgramTest();
                    if (isAlreadyAutonomous) {
                        autoThread.interrupt();
                        isAlreadyAutonomous = false;
                    }
                    sleep(20);
                } else if (controlWord.isAutonomous()) {
                    if (!isAlreadyAutonomous) {
                        autoThread = new Thread(new AutonomousRunnable());
                        autoThread.start();
                    }
                    isAlreadyAutonomous = true;
                }
            } catch (InterruptedException e) {
                DriverStation.reportError("Main Thread Interrupted", e.getStackTrace());
            }


        }

    }

    public static void sleep(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }

    public static void sleepSeconds(double seconds) throws InterruptedException {
        Thread.sleep((long) (seconds * 1000));
    }

    public abstract void autonomousProcedure() throws InterruptedException;

    public void endCompetition() {

    }

    public static void callCommand(Command command) throws InterruptedException {
        System.out.println("Starting " + command.getName());
        command.initialize();
        while (!command.isFinished()) {
            System.out.println("Running command cycle");
            command.execute();
            sleep(20);
        }
        command.end(false);
        System.out.println("Stopping " + command.getName());
    }

    public Thread getNewAutoThread() {
        return new Thread(new AutonomousRunnable());
    }


}
