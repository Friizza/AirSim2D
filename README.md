# ✈️ A320 2D Flight Dynamics Simulator

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Physics](https://img.shields.io/badge/3--DOF-Rigid%20Body%20Dynamics-blue?style=for-the-badge)
![Atmosphere](https://img.shields.io/badge/ISA-Atmosphere%20Model-9cf?style=for-the-badge)
![UPS](https://img.shields.io/badge/Physics-200%20UPS-green?style=for-the-badge)

A physically rigorous 2D flight dynamics simulator of an Airbus A320, written from scratch in Java. The flight model is built on the full 3-DOF (x, y, θ) equations of motion for a rigid body: translational dynamics driven by the four aerodynamic forces, and rotational dynamics governed by pitching moments about the centre of gravity.

This isn't an arcade game. The flight model relies on actual physics:

---

## Physics Overview

### Equations of Motion

At every timestep `dt`, the simulator integrates Newton's second law in both translational and rotational form:
```
ΣF = m · a          (translational)
ΣM_cg = I_zz · α   (rotational — pitch axis)
```

State vector: `[x, y, θ, vx, vy, ω]` — updated at 200 Hz via Euler integration, fully decoupled from the render loop.

### Aerodynamic Model

Dynamic pressure and aerodynamic forces follow standard compressible-flow relations:
```
q   = ½ · ρ · V²
L_w = q · S_w · C_L(α)
L_t = q · S_t · C_L(α_t)        where α_t = α + δ_e
D   = q · (S_w + S_t) · C_D
```

**Angle of Attack** is computed geometrically at each timestep as the difference between pitch angle θ and the flight path angle γ = atan2(−Vy, Vx), accounting for wind.

**Lift curve** — piecewise linear with stall break at |α| = 15°:
- Linear regime: `C_L = 0.2 + 0.1 · α`
- Post-stall: `C_L` drops linearly (positive and negative stall modelled separately)

**Drag polar** — parabolic, consistent with low-speed aerodynamics:
```
C_D = C_D0 + k · C_L²       (C_D0 = 0.028, k = 0.04375)
```
With an additional induced drag penalty past the stall angle.

### Dual-Surface Lift & Pitching Moment

Lift is not a single resultant vector. The engine computes it separately for the main wing and the tailplane, each with its own area, lift coefficient, and moment arm relative to the CG:
```
M_cg = L_w · d_w + L_t · d_t + M_damp
```

Moment arms (`d_w = +3.84 m`, `d_t = −14.19 m`) are derived from the A320 three-view drawing at a calibrated scale of `0.0713 m/px`. Aerodynamic pitch damping `M_damp = −800 000 · ω` prevents unrealistic divergent oscillations.

### ISA Atmosphere

Air density follows the International Standard Atmosphere — two-layer model:

| Layer | Formula |
|---|---|
| Troposphere (0–11 km) | `ρ = 1.225 · (1 − 2.2557×10⁻⁵ · h)^4.256` |
| Stratosphere (11–20 km) | `ρ = ρ₁₁ · exp(−1.57×10⁻⁴ · (h − 11000))` |

Engine thrust is scaled proportionally to air density, consistent with turbofan behaviour at altitude.

### Auto-Trim (Fly-By-Wire)

A built-in trim computer solves analytically for the elevator deflection `δ_e` that zeroes the net pitching moment at the current AoA:
```
δ_e = [ −(S_w · C_L(α) · d_w) / (S_t · d_t) ] / 0.1 − α
```

The elevator then slews toward the target at a finite rate (15°/s), emulating actuator dynamics. Manual override disengages it instantly.

---

## Rendering & HUD

- **Background** scrolls via a parallax tile system driven by `(vx, vy)`, giving the illusion of motion without moving the aircraft sprite.
- **Aircraft sprite** is rotated about the CG pivot using `Graphics2D.rotate()` at pitch angle θ.
- **Force vectors** are drawn in world coordinates using 2D rotation matrices to project each vector's origin (CG, wing aerodynamic centre, tail aerodynamic centre) correctly as the aircraft rotates.
- **HUD sliders** display throttle, pitch, airspeed, vertical speed, and elevator deflection in real time. The throttle slider is mouse-draggable.

---

## Controls

| Input | Action |
|---|---|
| **Mouse drag** — throttle slider | Engine thrust 0–100% |
| **↑ / ↓ arrows** | Elevator deflection ±25° (disengages auto-trim) |
| **W / S** | Direct pitch override |
| **AUTO TRIM button** | Toggle trim computer |
| **ENTER** | Reset after stall |

The simulation starts trimmed in level flight at **10 000 m**, **258.7 m/s**, with auto-trim on.

---

## Planned Features

- **Yoke simulation** — replace pitch override with proper elevator actuation, so pitch changes emerge from aerodynamic moments rather than hardcoded angle jumps
- **High-lift devices** — flaps/slats shifting the C_L–α and C_D–C_L curves for realistic low-speed flight
- **Engine inertia** — turbofan spool-up/spool-down delay, and pitching moment from under-wing engine placement
- **IAS vs TAS** — Indicated Airspeed computed from dynamic pressure for a more realistic HUD
- **Transonic drag rise** — Mach wave drag onset near M = 0.78
- **Ground mechanics** — landing gear, rolling friction, braking force
