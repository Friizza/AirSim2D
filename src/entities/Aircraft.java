package entities;

import main.Simulation;
import physics.Vector2D;

public class Aircraft {

    Simulation sim;

    public double mass = 70000;
    public double wingArea = 100;
    public double tailArea = 24;

    public double alpha = 2.0;
    public double pitch = 2.0;
    public double pitchRate = 0.0;
    public double elevatorAngle = 2.522;
    public double Izz = 8000000.0;

    public boolean autoTrim = false;

    public double wingLiftCoeff = 0;
    public double tailLiftCoeff = 0;
    public double liftCoefficient = 0.4;
    public double dragCoefficient = 0.035;

    public double altitude = 10000;
    public double airDensity = 0.41;

    public double maxSeaLevelThrust = 240000;
    public double throttle = 0.74;
    public double currentAvailableThrust = 0;

    public Vector2D thrust = new Vector2D(0, 0);
    public Vector2D position;
    public Vector2D velocity;
    public Vector2D acceleration;
    public Vector2D netForce;

    public double currentDrag = 0;
    public double currentLift = 0;
    public double currentWingLift = 0;
    public double currentTailLift = 0;

    public Aircraft(Simulation sim) {
        this.sim = sim;
        this.position = new Vector2D(((double) sim.screenWidth /2) - 260,((double) sim.screenHeight /2) - 168);
        this.velocity = new Vector2D(258.7, 0);
        this.acceleration = new Vector2D(0, 0);
        this.netForce = new Vector2D(0, 0);
    }

    public void update(double dt) {
        this.netForce = new Vector2D(0, 0);

        this.altitude -= this.velocity.y * dt;
        if (this.altitude < 0) {
            this.altitude = 0;
            this.velocity.y = 0;
        }

        // modello ISA troposfera/stratosfera
        double seaLevelDensity = 1.225;
        if (this.altitude < 11000) {
            airDensity = seaLevelDensity * Math.pow(1 - 0.0000225577 * this.altitude, 4.2561);
        } else {
            double densityAt11k = seaLevelDensity * Math.pow(1 - 0.0000225577 * 11000, 4.2561);
            airDensity = densityAt11k * Math.exp(-0.000157 * (this.altitude - 11000));
        }

        double windX = 0;
        double windY = 0;
        double v_inf_X = this.velocity.x - windX;
        double v_inf_Y = this.velocity.y - windY;
        double v_inf_mag = Math.sqrt((v_inf_X * v_inf_X) + (v_inf_Y * v_inf_Y));
        if (v_inf_mag == 0) v_inf_mag = 0.001;

        updateAerodynamics(v_inf_X, v_inf_Y, dt);

        double vMag = this.velocity.mag();
        if (vMag == 0) vMag = 0.001;

        double q = 0.5 * airDensity * (v_inf_mag * v_inf_mag); // Pressione dinamica

        this.currentWingLift = q * wingArea * wingLiftCoeff;
        this.currentTailLift = q * tailArea * tailLiftCoeff;
        this.currentLift = currentWingLift + currentTailLift;

        // bracci CG: posizioni in pixel convertite con scala 0.0713 m/px
        // ala a px 314, coda a px 61, CG a px 260 (da disegno 3-viste)
        double distWing = (314 - 260) * 0.0713;
        double distTail = (61 - 260) * 0.0713;

        double dampingMoment = -800000.0 * pitchRate;
        double pitchingMoment = (currentWingLift * distWing) + (currentTailLift * distTail) + dampingMoment;
        double angularAccel = Math.toDegrees(pitchingMoment / Izz);

        this.pitchRate += angularAccel * dt;
        this.pitch += this.pitchRate * dt;

        applyForce(new Vector2D(0, this.mass * sim.GRAVITY));

        // spinta scalata con densità (motori a flusso d'aria)
        this.currentAvailableThrust = maxSeaLevelThrust * (airDensity / seaLevelDensity);
        double actualThrust = currentAvailableThrust * throttle;
        double pitchRad = Math.toRadians(this.pitch);
        this.thrust.x = actualThrust * Math.cos(pitchRad);
        this.thrust.y = -actualThrust * Math.sin(pitchRad);
        netForce.add(thrust);

        this.currentDrag = q * (wingArea + tailArea) * dragCoefficient;
        double dragX = -this.currentDrag * (v_inf_X / v_inf_mag);
        double dragY = -this.currentDrag * (v_inf_Y / v_inf_mag);
        applyForce(new Vector2D(dragX, dragY));

        double liftX = this.currentLift * (v_inf_Y / v_inf_mag);
        double liftY = this.currentLift * (-v_inf_X / v_inf_mag);
        applyForce(new Vector2D(liftX, liftY));

        this.acceleration = this.netForce.multiply(1.0 / mass);
        this.velocity.add(this.acceleration.multiply(dt));

//        Vector2D positionChange = this.velocity.multiply(dt);
//        this.position.add(positionChange);

        this.altitude -= this.velocity.y * dt;
        if (this.altitude < 0) {
            this.altitude = 0;
            this.velocity.y = 0;
        }

        if (this.altitude < 11000) {
            airDensity = 1.225 * Math.pow(1 - 0.0000225577 * this.altitude, 4.2561);
        } else {
            double densityAt11k = 1.225 * Math.pow(1 - 0.0000225577 * 11000, 4.2561);
            airDensity = densityAt11k * Math.exp(-0.000157 * (this.altitude - 11000));
        }
    }

    private void updateAerodynamics(double v_inf_X, double v_inf_Y, double dt) {
        double gamma = Math.toDegrees(Math.atan2(-v_inf_Y, v_inf_X));
        double rawAlpha = this.pitch - gamma;

        // normalizza AoA tra -180 e 180
        rawAlpha = rawAlpha % 360.0;
        if (rawAlpha > 180.0) {
            rawAlpha -= 360.0;
        } else if (rawAlpha < -180.0) {
            rawAlpha += 360.0;
        }
        this.alpha = rawAlpha;

        if (this.alpha >= -15.0 && this.alpha <= 15.0) {
            this.wingLiftCoeff = 0.2 + (0.1 * this.alpha);
        } else if (this.alpha > 15.0) {
            // Stallo positivo
            this.wingLiftCoeff = 1.7 - (0.15 * (this.alpha - 15.0));
            if (this.wingLiftCoeff < 0) this.wingLiftCoeff = 0;
        } else {
            // Stallo negativo
            this.wingLiftCoeff = -1.3 + (0.15 * (-this.alpha - 15.0));
            if (this.wingLiftCoeff > 0) this.wingLiftCoeff = 0;
        }

        if (autoTrim) {
            double distWing = (314 - 260) * 0.0713;
            double distTail = (61 - 260) * 0.0713;

            double targetTailLiftCoeff = -(wingArea * wingLiftCoeff * distWing) / (tailArea * distTail);
            double targetElevatorAngle = (targetTailLiftCoeff / 0.1) - this.alpha;

            targetElevatorAngle = Math.max(-25.0, Math.min(25.0, targetElevatorAngle));

            double trimSpeed = 15.0;
            if (this.elevatorAngle < targetElevatorAngle) {
                this.elevatorAngle += trimSpeed * dt;
                if (this.elevatorAngle > targetElevatorAngle) this.elevatorAngle = targetElevatorAngle;
            } else if (this.elevatorAngle > targetElevatorAngle) {
                this.elevatorAngle -= trimSpeed * dt;
                if (this.elevatorAngle < targetElevatorAngle) this.elevatorAngle = targetElevatorAngle;
            }
        }

        double alphaTail = this.alpha + this.elevatorAngle;
        if (alphaTail <= 15.0 && alphaTail >= -15.0) {
            this.tailLiftCoeff = 0.1 * alphaTail;
        } else if (alphaTail > 15.0) {
            this.tailLiftCoeff = 1.5 - (0.15 * (alphaTail - 15.0));
            if (this.tailLiftCoeff < 0) this.tailLiftCoeff = 0;
        } else {
            this.tailLiftCoeff = -1.5 - (0.15 * (alphaTail + 15.0));
            if (this.tailLiftCoeff > 0) this.tailLiftCoeff = 0;
        }

        // Cl globale media pesata sulle superfici
        this.liftCoefficient = (this.wingLiftCoeff * wingArea + this.tailLiftCoeff * tailArea) / (wingArea + tailArea);

        // Drag polare
        double cd0 = 0.028;
        double k = 0.04375;
        this.dragCoefficient = cd0 + (k * Math.pow(this.liftCoefficient, 2));

        if (this.alpha > 15.0) {
            this.dragCoefficient += 0.05 * (this.alpha - 15.0);
        }
    }

    public void applyForce(Vector2D force) {
        this.netForce.add(force);
    }
}