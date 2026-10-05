
# GhastBoost (PaperMC Plugin)

[![PaperMC API](https://img.shields.io/badge/PaperMC-API_26.1-34D0FB?style=for-the-badge&logo=papermc&logoColor=white)](https://papermc.io)

A lightweight, event-driven Minecraft server plugin built with the PaperMC API that introduces a custom gameplay mechanic: boosting the speed of specific entities using custom-smelted items.

## 🚀 Features & Gameplay Mechanics

* **Custom Crafting Recipe:** Allows players to smelt Sugar in a furnace to obtain a custom item: **Energizing Powder**.
* **Entity Interaction:** Sneaking and right-clicking a `HAPPY_GHAST` with the powder consumes the item and applies a speed boost.
* **Temporary Status Modifier:** The speed boost multiplies the entity's base velocity by 2x for a precise duration of 400 ticks (20 seconds) before reverting to normal.

## 🛠 Technical Specifications & Stack

* **Language:** Java
* **Framework:** PaperMC API (v1.20+)
* **Build Tool:** Maven (or Eclipse project structure)
* **Architecture:** Utilizes the Spigot/Paper Event API (`FurnaceSmeltEvent`, `PlayerInteractEntityEvent`) to handle custom item logic and entity state modifications without performance overhead.

## 📦 Installation & Setup

1. Clone the repository and build the project to generate the `.jar` file.
2. Drop the compiled `.jar` file into your server's `plugins/` directory.
3. Start or restart your PaperMC server to initialize the plugin.


