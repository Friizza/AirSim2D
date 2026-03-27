# ✈️ A320 2D Flight Dynamics Simulator

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Physics](https://img.shields.io/badge/Physics-Custom_Engine-blue?style=for-the-badge)

Welcome! This is a 2D flight simulator built entirely in Java.

The goal of this project is to accurately simulate the flight dynamics of an Airbus A320 using a custom vector-based engine to handle real aerodynamic forces, rigid-body mechanics, and atmospheric conditions.

## 🚀 Under the hood

This isn't an arcade game. The flight model relies on actual physics:

* **Aerodynamics:** The engine calculates True Airspeed and dynamic Angle of Attack. Lift and Drag are computed using mathematical approximations of the A320's actual polar curves.
* **Dual-Lift & Aerodynamic Damping:** Lift isn't just one magic vector. The engine calculates dynamic pressure and applies lift separately to the main wing and the tailplane based on their respective areas and distances from the CG. It also applies aerodynamic damping to prevent unrealistic infinite tumbling.
* **Rigid Body Dynamics:** Calculates pitching moments and angular acceleration in real-time.
* **Fly-By-Wire (Auto-Trim):** A built-in flight computer automatically adjusts the tail elevator to keep the plane flying level (manual override available).
* **Real Atmospheric Modeling:** Air density uses standard atmospheric equations to model the troposphere (up to 11,000m) and the stratosphere separately, dynamically affecting both drag and maximum available engine thrust.
* **True Stalls:** Stalls happen when the Angle of Attack exceeds 15 degrees, allowing for high-speed accelerated stalls based on actual control inputs.
* **Live Vector Telemetry:** The HUD uses 2D rotation matrices to project and draw the actual physical force vectors (Thrust, Drag, Wing Lift, Tail Lift, Weight) originating directly from the Center of Gravity in real-time.
* **Decoupled Physics Loop:** The physics engine runs at a strict 200 Updates Per Second (UPS), completely independent of the rendering framerate (120 FPS). This ensures the rigid-body integration remains perfectly deterministic and doesn't break if the screen lags.

## 🎮 How to run and play
**The simulation starts with the aircraft already mid-air, in perfectly balanced level flight at an altitude of 10,000 meters (10 km).**

* **Mouse:** Drag the Throttle slider on the bottom left to change engine thrust. Click the "AUTO TRIM" button to toggle the flight computer.
* **W / S:** Manual pitch override (forces the nose up or down).
* **Up / Down Arrows:** Manually adjust the elevator angle (useful if Auto-Trim is off and you want to balance the aerodynamic forces yourself).
* **ENTER:** Reset the simulation if you stall and crash.

## 🔮 Future Plans

This simulator is a continuous work in progress. Here are a few aerodynamic and mechanical features I plan to implement next to push the realism even further:

* **Realistic Control Inputs (Yoke Simulation):** Replacing the manual pitch override (W/S keys) with a simulated yoke. Inputs will directly actuate the elevator control surfaces, allowing the aircraft's pitch to change naturally through aerodynamic pitching moments rather than hardcoded angle adjustments.
* **High-Lift Devices (Flaps & Slats):** Dynamically shifting the lift and drag curves to allow for realistic, low-speed takeoff and landing profiles.
* **Engine Inertia & Position:** Simulating turbofan spool-up/spool-down times (delaying thrust application) and calculating the pitching moment caused by the engines being mounted below the Center of Gravity.
* **Transonic Aerodynamics:** Implementing Mach wave drag for high-altitude cruise speeds.
* **IAS vs TAS:** Calculating Indicated Airspeed (IAS) based on dynamic pressure for the HUD, rather than just displaying True Airspeed (TAS).
* **Ground Mechanics:** Adding landing gear physics, including rolling friction, braking force, and gear drag when extended.