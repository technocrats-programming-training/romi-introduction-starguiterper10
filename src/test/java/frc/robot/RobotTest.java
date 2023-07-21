package frc.robot;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALValue;
import edu.wpi.first.hal.simulation.NotifyCallback;
import edu.wpi.first.wpilibj.simulation.CallbackStore;
import edu.wpi.first.wpilibj.simulation.PWMSim;
import lib.Romi;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;


public class RobotTest {
    PWMSim simLeftMotor;
    PWMSim simRightMotor;
    Robot robot;

    @Before
    public void setup() {
        assert HAL.initialize(500, 0);
        simLeftMotor = new PWMSim(0);
        simRightMotor = new PWMSim(1);
        robot = new Robot();
        Romi.initRomiSubsystems();
    }

    @Test
    public void testUsesLeftMotor() throws InterruptedException {
        AtomicBoolean leftMotorHasBeenUsed = new AtomicBoolean(false);
        NotifyCallback callback = (String name, HALValue value) -> {
            if (value.getType() == HALValue.kDouble && value.getDouble() != 0) {
                leftMotorHasBeenUsed.set(true);
            }
        };
        CallbackStore store = simLeftMotor.registerSpeedCallback(callback, false);
        Thread autoThread = robot.getNewAutoThread();
        autoThread.start();
        Thread.sleep(15000);
        autoThread.interrupt();
        assert leftMotorHasBeenUsed.get();
    }
}
