# Smart Shelf Project: Presentation Guide

This guide provides a step-by-step walkthrough for presenting the **Computer Vision-Based Smart Shelf** project. It is designed to help your groupmates understand how to navigate the app and demonstrate the system's core features.

---

## Phase 1: Preparation (Before the Panel Arrives)

### 1. Environment Setup
*   **Terminal 1 (VM/Ubuntu):** Open your virtual environment and run the simulator.
    ```bash
    source venv/bin/activate
    python shelf_simulator.py
    ```
    *Wait for:* `Smart Shelf Listener Active. Waiting for trigger from App...`
*   **Mobile Device:** Open the **Otto Scents** app and log in as an **Admin** (use your group's admin credentials).
*   **Firestore Dashboard:** Keep the Firebase Console open on a laptop to show real-time database updates if asked.

---

## Phase 2: Feature-by-Feature Demonstration

### 1. Dashboard & Status (The "At a Glance" View)
*   **Navigation:** Upon login, you are on the **Home Screen**.
*   **What to show:**
    *   Point out the **Branch selection** at the top (Lipa / San Pablo).
    *   Show the **Temperature Card**. Explain that the hardware (simulator) is streaming this live.
    *   Show the **Inventory Summary** charts. These reflect the total stock across all areas.

### 2. Manual Inventory Scan (The "Heart" of the Project)
*   **Scenario:** Tell the panel you are checking the shelf stock for the Lipa branch.
*   **Navigation:** 
    1. Tap the **"Scan"** or **"Trigger Check"** button on the Home screen.
    2. Select **"Lipa Branch"**.
*   **The Reaction:** 
    *   Show the **Terminal** running the simulator. It will say: `Manual Trigger Received: Lipa`.
    *   It will process the image (e.g., `scenario_full.jpg`).
    *   Back on the **App**, wait 2-3 seconds. The inventory counts will update automatically without refreshing.
*   **Check History:** Navigate to **"Shelf Check History"** to show the timestamped log of the scan you just did.

### 3. Misplaced Item Detection (AI Logic)
*   **Scenario:** Explain that the AI doesn't just count; it checks if a bottle is in the *wrong area*.
*   **Navigation:** 
    1. Trigger another scan for a scenario where items are moved (the simulator handles this sequentially).
    2. Go to the **"Alerts"** screen (Bell icon).
    3. **What to show:** Point to a **"Warning"** alert that says *"Misplaced: Midnight Oud found in Area B"*. Explain that the system detected the bottle's X-coordinate was outside its assigned zone.

### 4. Cooling System Simulation (Hardware Integration)
*   **Scenario:** Show how the system protects the perfumes from high temperatures.
*   **Action:** 
    1. In the **Firestore Console**, manually change the `currentTemperature` in `settings/global_config` to **28.0**.
    2. **Watch the App:** The temperature card will turn red, and a "Fan Active" icon will appear.
    3. **Watch the Terminal:** The simulator will print `[Cooling] Current Temperature: ...` as it simulates the fan cooling it back down to 22.0.

### 5. Product Management
*   **Navigation:** Tap on **"Inventory"** (Box icon) in the bottom bar.
*   **What to show:**
    *   Tap a specific perfume (e.g., "A"). 
    *   Explain the **"Low Stock Threshold"**. You can change it here, and the app will immediately re-calculate if the product is "Low Stock" based on the new number.

---

## Phase 3: Navigational Cheat Sheet

| Feature | Where to find it? |
| :--- | :--- |
| **Trigger Scan** | Home Screen -> "Manual Scan" Button |
| **Alerts/Notifications** | Bottom Bar -> Bell Icon |
| **Detailed Logs** | Home Screen -> "System Activity Logs" |
| **Manage Products** | Bottom Bar -> Inventory Icon |
| **Change Branch** | Top Header -> Branch Dropdown |

---

## Phase 4: Troubleshooting During Demo

*   **App not updating?** Check if Terminal 1 says `Connected to Firestore`. If not, restart the script.
*   **Wrong Counts?** Remind the panel that this is a **prototype** using scenario images to ensure a smooth demo, but it uses the real YOLOv8 model for every scan.
*   **Lag?** Firebase synchronization depends on internet speed. Wait a few seconds before clicking again.

---

## Final Closing Statement
*"In conclusion, Otto Scents integrates AI Vision with Cloud synchronization to provide a robust, real-time monitoring solution that eliminates manual errors in perfume inventory management."*
