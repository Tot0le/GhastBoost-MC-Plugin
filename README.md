# GhastBoost (PaperMC Plugin)

[![PaperMC API](https://img.shields.io/badge/PaperMC-API_26.1-34D0FB?style=for-the-badge&logo=papermc&logoColor=white)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

A lightweight, event-driven Minecraft server plugin built with the PaperMC API that introduces a custom gameplay mechanic: boosting the flying speed of Happy Ghasts using custom-smelted items with cumulative duration stacking.

---

## 🚀 Features & Gameplay Mechanics

### 1. Smelt Energizing Powder
Smelt standard Sugar in any furnace to craft official **Energizing Powder**.
* **Exploit Protection:** The item is securely stamped using Bukkit's `PersistentDataContainer` (NBT). Regular sugar renamed in an anvil cannot trigger the boost.

<img width="750" alt="craft" src="https://github.com/user-attachments/assets/9cbc8152-e5db-4518-bb36-3f239ad3ebd7" />

### 2. 2x Speed Boost
Sneak and right-click a `HAPPY_GHAST` holding the powder to activate a **2x flying speed boost** for 20 seconds.
* In Survival mode, one powder is consumed per feed.
* In Creative mode, items are preserved without consumption.

<img width="750" alt="use" src="https://github.com/user-attachments/assets/26b86323-7781-4770-bbe3-24fe4f9d50e4" />

### 3. Expiration Alert
Receive an instant in-game chat alert as soon as the speed boost runs out, safely restoring the Ghast's base speed.

<img width="750" alt="expires" src="https://github.com/user-attachments/assets/d02478e0-b6ed-4f24-8ed0-7fec2b882156" />

### 4. Stack Duration (Up to 3 Minutes)
Feed multiple powders consecutively to stack the speed boost duration.
* Each powder adds **+20 seconds** to the active timer.
* Total boost duration is capped at **3 minutes** (180s) to prevent resource waste and maintain gameplay balance.
* Chat messages display remaining seconds upon each extension and notify the player if the maximum cap is reached.

<img width="750" alt="stack" src="https://github.com/user-attachments/assets/6349616c-3b62-4c59-ba83-5539d4d89c06" />

---

## 🛠 Architecture & Technical Stack

The codebase adheres to the **Single Responsibility Principle (SRP)** and is decoupled into dedicated domain and infrastructure services:

* **`Main`**: Plugin lifecycle entrypoint, dependency wiring, and custom `FurnaceRecipe` registration.
* **`PowderItem`**: Encapsulates item factory logic, `NamespacedKey` tagging, and PDC signature validation.
* **`GhastInteractListener`**: Handles `PlayerInteractEntityEvent`, isolates main-hand interaction checks, sneak state, and gamemode handling.
* **`BoostService`**: Coordinates speed modifier application using non-destructive `AttributeModifier` (`ADD_SCALAR`).
* **`BoostSession`**: Domain model managing temporal logic, duration calculations, stacking, and maximum caps.
* **`BoostScheduler`**: Wraps Paper's `BukkitTask` scheduler, handling task scheduling, replacement, and cancellation.

```
Language:     Java 25
Platform:     PaperMC API 26.1+
Build System: Maven
```

---

## 📦 Installation & Setup

### 1. Build from Source
Ensure you have JDK 25 and Maven installed, then run:

```bash
mvn clean package
```

The compiled artifact will be generated in `target/GhastBoost-1.0.jar`.

### 2. Deploy to Server
1. Place the generated `.jar` file into your server's `plugins/` directory.
2. Start or restart your PaperMC server.
3. Verify initialization with `/plugins` or `/version GhastBoost`.

---

Enjoy! (^-^)/
<p align="center">
  <img width="100%" alt="enjoy" src="https://github.com/user-attachments/assets/64f7c296-a804-4eec-98df-88d3db3e094a" />
</p>
