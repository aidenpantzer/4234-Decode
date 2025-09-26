package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class subDrive {



    BNO055IMU imu;
    Orientation orientation;
    BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();


    DcMotorEx frontRight;
    DcMotorEx frontLeft;
    DcMotorEx backRight;
    DcMotorEx backLeft;

    public double offset = Math.PI;

    public subDrive(HardwareMap hardwareMap) {

        // Motor Definitions
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");

        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imu.initialize(parameters);

    }

    public void To (double X_Movement, double Y_Movement, double Rotation, double powerMultiplier, boolean fieldCentric) {

        if ((Math.abs(X_Movement) >= 0.05) || (Math.abs(Y_Movement) >= 0.05) || (Math.abs(Rotation) >= 0.05)) {

            // Orientation
            double angle = getImu();

            // Calculating power variables
            double X = X_Movement * Math.cos(-(angle)) - Y_Movement * Math.sin(-(angle));
            double Y = Y_Movement * Math.cos(-(angle)) + X_Movement * Math.sin(-(angle));

            // Dividing power by a denominator to set maximum output to 1
            double denominator = Math.max(Math.abs(Y) + Math.abs(X) + Math.abs(Rotation), 1);

            double frontLeftPower  = (Y + X + Rotation) / denominator;
            double backLeftPower   = (Y - X + Rotation) / denominator;
            double frontRightPower = (Y - X - Rotation) / denominator;
            double backRightPower  = (Y + X - Rotation) / denominator;

            // Set motor power
            frontLeft.setPower      (frontLeftPower  *   powerMultiplier);
            backLeft.setPower       (backLeftPower   *   powerMultiplier);
            frontRight.setPower     (frontRightPower *   powerMultiplier);
            backRight.setPower      (backRightPower  *   powerMultiplier);

        } else {

            frontRight.setVelocity(0);
            frontLeft.setVelocity(0);
            backRight.setVelocity(0);
            backLeft.setVelocity(0);

        }



    }

    public double getImu() {
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle - offset;
    }

    public double getRawImu(){
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.RADIANS).firstAngle;
    }

    public void setOffset(double off){
        offset = off;
    }

    public void recalibrate() {
        setOffset(Math.PI);
        imu.initialize(parameters);
    }
}
