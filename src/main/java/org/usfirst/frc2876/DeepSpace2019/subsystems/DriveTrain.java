package org.usfirst.frc2876.DeepSpace2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import org.usfirst.frc2876.DeepSpace2019.commands.XboxDrive;
import org.usfirst.frc2876.DeepSpace2019.utils.Ramp;
import org.usfirst.frc2876.DeepSpace2019.utils.TalonSrxEncoder;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * 
 */
public class DriveTrain extends Subsystem {

    private WPI_TalonSRX talonSRX4;
    private WPI_TalonSRX talonSRX3;
    private WPI_TalonSRX talonSRX2;
    private WPI_TalonSRX talonSRX1;
    private DifferentialDrive differentialDrive;

    public AHRS navx;

    private WPI_TalonSRX rightMaster;
    private WPI_TalonSRX leftMaster;
    private WPI_TalonSRX rightFollower;
    private WPI_TalonSRX leftFollower;

    private TalonSrxEncoder leftEncoder;
    private TalonSrxEncoder rightEncoder;

    // Calculated this following instructions here:
    // https://phoenix-documentation.readthedocs.io/en/latest/ch14_MCSensor.html#confirm-sensor-resolution-velocity
    //
    // Right Forward 3300
    // Right Reverse 3200
    // Left Forward 3200
    // Left Reverse 3300
    // (100% X 1023) / 3200 = .3197
    //
    private final double kF = .3197f;

    // Don't allow drivetrain to run at max speed. If we did then there is no room
    // for PID to adjust velocity if we have already maxed out.
    private final double MAX_RPM = 3200.0 * .9;

    // Use this to limit how fast velocity can be adjusted.
    private Ramp rampArcadeSpeed;
    private Ramp rampArcadeRotate;
    private Ramp rampTankLeft;
    private Ramp rampTankRight;

    // TODO Declare navx

    public DriveTrain() {

        navx = new AHRS(SPI.Port.kMXP);

        talonSRX4 = new WPI_TalonSRX(4);
        talonSRX3 = new WPI_TalonSRX(3);
        talonSRX2 = new WPI_TalonSRX(2);
        talonSRX1 = new WPI_TalonSRX(1);

        rightMaster = talonSRX1;
        leftMaster = talonSRX3;
        rightFollower = talonSRX2;
        leftFollower = talonSRX4;

        // https://phoenix-documentation.readthedocs.io/en/latest/ch13_MC.html#follower
        rightFollower.follow(rightMaster);
        leftFollower.follow(leftMaster);

        // https://phoenix-documentation.readthedocs.io/en/latest/ch13_MC.html#inverts
        rightMaster.setInverted(true);
        rightFollower.setInverted(InvertType.FollowMaster);
        leftMaster.setInverted(true);
        leftFollower.setInverted(InvertType.FollowMaster);

        // https://phoenix-documentation.readthedocs.io/en/latest/ch14_MCSensor.html#sensor-phase
        leftMaster.setSensorPhase(false);
        rightMaster.setSensorPhase(true);

        leftMaster.setNeutralMode(NeutralMode.Coast);
        leftFollower.setNeutralMode(NeutralMode.Coast);
        rightMaster.setNeutralMode(NeutralMode.Coast);
        rightFollower.setNeutralMode(NeutralMode.Coast);

        differentialDrive = new DifferentialDrive(leftMaster, rightMaster);

        differentialDrive.setSafetyEnabled(false);
        // differentialDrive.setExpiration(0.1);
        differentialDrive.setMaxOutput(1.0);

        // TODO configure pid on talons and stuffs. Example how to use
        // configAllSettings:
        // https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/blob/b71916c131f6b381ba26bb5ac46302180088614d/Java/Config%20All/src/main/java/frc/robot/Configs.java#L19
        TalonSRXConfiguration allConfigs = new TalonSRXConfiguration();
        // rightMaster.configAllSettings(allConfigs);

        leftEncoder = new TalonSrxEncoder(leftMaster);
        rightEncoder = new TalonSrxEncoder(rightMaster);
        
        rampArcadeSpeed = new Ramp(MAX_RPM*.1);
        rampArcadeRotate = new Ramp(MAX_RPM*.1);
        rampTankLeft = new Ramp(MAX_RPM*.1);
        rampTankRight = new Ramp(MAX_RPM*.1);

        // TODO initialize navx variable

    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new XboxDrive());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

        // TODO Call udpate dashboard here

    }
    // TODO Add an update dashboard method

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    // TODO Add adjustSpeed method to control sensitivity of joystick -> drive
    // output.

    public void arcadeDrive(double xSpeed, double zRotation) {
        differentialDrive.arcadeDrive(xSpeed, zRotation);
    }

    public void tankDrive(double leftValue, double rightValue) {
        differentialDrive.tankDrive(leftValue, rightValue);
    }

    public void velocityTankDrive(double leftValue, double rightValue) {
    
        double leftRpm = leftValue * MAX_RPM;
        leftRpm = rampTankLeft.get(leftRpm);
        double rightRpm = rightValue * MAX_RPM;
        rightRpm = rampTankRight.get(rightRpm);

        leftMaster.set(ControlMode.Velocity, leftRpm);
        rightMaster.set(ControlMode.Velocity, rightRpm);
    }

    public void setVelocityArcadeJoysticks(double speed, double rotate) {

        speed = rampArcadeSpeed.get(speed);
        rotate = rampArcadeRotate.get(rotate);

        // speed = adjustSpeed(speed);
        // rotate = adjustRotate(rotate);
        if (speed > 0.0) {
            if (rotate > 0.0) {
                leftMaster.set(ControlMode.Velocity, (speed - rotate) * MAX_RPM);
                rightMaster.set(ControlMode.Velocity, Math.max(speed, rotate) * MAX_RPM);
            } else {
                leftMaster.set(ControlMode.Velocity, Math.max(speed, -rotate) * MAX_RPM);
                rightMaster.set(ControlMode.Velocity, (speed + rotate) * MAX_RPM);
            }
        } else {
            if (rotate > 0.0) {
                leftMaster.set(ControlMode.Velocity, -Math.max(-speed, rotate) * MAX_RPM);
                rightMaster.set(ControlMode.Velocity, (speed + rotate) * MAX_RPM);
            } else {
                leftMaster.set(ControlMode.Velocity, (speed - rotate) * MAX_RPM);
                rightMaster.set(ControlMode.Velocity, -Math.max(-speed, -rotate) * MAX_RPM);
            }
        }
    }
}
