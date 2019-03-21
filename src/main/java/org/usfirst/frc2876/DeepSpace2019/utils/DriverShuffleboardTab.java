/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc2876.DeepSpace2019.utils;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc2876.DeepSpace2019.Robot;
import org.usfirst.frc2876.DeepSpace2019.commands.CGDriveOffHatch;
import org.usfirst.frc2876.DeepSpace2019.commands.CGDriveOffCargo;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.shuffleboard.SendableCameraWrapper;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class DriverShuffleboardTab {

    public ShuffleboardTab driveTeamTab;

    private WPI_TalonSRX talonSRX5;
    private WPI_TalonSRX talonSRX6;
    private WPI_TalonSRX talonSRX8;
    
    private WPI_TalonSRX master;
    private TalonSrxEncoder encoder;

    private WPI_TalonSRX masterArm;
    private WPI_TalonSRX followerArm;

    private NetworkTableEntry nteInverseDriveToggle;
    private NetworkTableEntry nteIsVectorFound;
    private NetworkTableEntry ntePIDSetpointHatch;
    private NetworkTableEntry nteSetPositionHatch;
    private NetworkTableEntry nteSetPositionArm;
    private NetworkTableEntry ntePIDSetpointArm;

    public DriverShuffleboardTab(){
        
    }

    public void setup(SendableChooser<Command> chooser){
        /////////////////////////
        // DRIVE TEAM COMMANDS //
        /////////////////////////

        driveTeamTab = Shuffleboard.getTab("DriveTeam");

        // Camera
        driveTeamTab.add("Camera", SendableCameraWrapper.wrap(Robot.driveTrain.server.getSource()))
        .withSize(17, 21).withPosition(0, 0);

        // Gyro

        // Auto Leave Platform

        driveTeamTab.add("Sandstorm", chooser).withPosition(17, 10).withSize(15, 3);

        //I CHANGED NAME TO "Auto Leave Platform"
        driveTeamTab.add("Auto Drive Cargo", new CGDriveOffCargo())
        .withSize(7, 3).withPosition(17, 6);

        driveTeamTab.add("Auto Drive Hatch", new CGDriveOffHatch())
        .withSize(7, 3).withPosition(25, 6);

        // Starting Position (Hatch-front or Cargo-front)
        // nteInverseDriveToggle = driveTeamTab.add("Hatch-Side Forward", Robot.driveTrain.getToggleInverseDrive()).withPosition(17, 12)
        // .withWidget(BuiltInWidgets.kToggleSwitch).getEntry();
        nteInverseDriveToggle = driveTeamTab.add("Hatch-Side Forward", Robot.driveTrain.getToggleInverseDrive())
        .withSize(7,6).withPosition(25, 0).getEntry();

        // Is Vector Found
        nteIsVectorFound = driveTeamTab.add("IsVectorFound", false)
        .withSize(7, 6).withPosition(17, 0).getEntry();

        // Arm (encoder position + set-point)

        // Zero Arm Pos

        // Hatch (encoder postion + set-point)
        ntePIDSetpointHatch = driveTeamTab.add("HatchSetpoint", 0)
        .withSize(5, 5).withPosition(33, 0).getEntry();

        // Zero Hatch Pos

    }

    public void setupSecond(){
        // Arm Encoder

        // Zero Arm Pos

        // Hatch (encoder postion + set-point)
        //driveTeamTab.add("HatchEncoder", encoder).withPosition(18, 0).withSize(10, 6);
        // ntePIDSetpoint = driveTeamTab.add("HatchSetpoint", 0)
        // .withSize(7, 3).withPosition(17, 9).getEntry();

        // Zero Hatch Pos
        // ShuffleboardLayout hatchCommand = driveTeamTab.getLayout("Commands", BuiltInLayouts.kList).withSize(7, 10)
        //         .withProperties(Map.of("Label position", "HIDDEN")).withPosition(0, 0);
        // hatchCommand.add(new HatchZeroPosition());
    }

    public void init(){
        
    }

    public void periodic() {

        nteIsVectorFound.setBoolean(Robot.vision.isVectorPresent());
        ntePIDSetpointHatch.setDouble(Robot.hatch.getSetpoint());
        nteInverseDriveToggle.setBoolean(Robot.driveTrain.getToggleInverseDrive());


    }

}
