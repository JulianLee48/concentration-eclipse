# 🃏 Concentration Game

A simple **Java concentration (memory) game** built for a university project.  
Flip cards, match pairs, and clear the board before you run out of clicks.

Even though this started as a homework assignment, it demonstrates **OOP**, a basic **GUI**, **event handling**, **audio**, and **unit testing**.

---

## 🎮 Features
- 52-card deck with dynamic shuffling
- Click to flip cards and match pairs
- Displays score, time, and remaining clicks
- Win / lose end-game screens
- Background music streamed via `javax.sound.sampled`
  
---

## 🛠️ Tech Stack
- **Language:** Java  
- **GUI:** `javalib.impworld` / `javalib.worldimages`  
- **Audio:** `javax.sound.sampled` (streams a `.wav` file via HTTP)
- **Testing:** `tester` library

---

## 📂 Project Structure
concentration-game/
├─ src/
│  └─ ConcentrationGame.java      
├─ assets/
│  ├─ pokemon (1).png            
│  ├─ Victory.png                 
│  └─ wasted.png                  
├─ README.md                     
└─ .gitignore                     

---

## 📝 Notes

- Images and audio are placeholders chosen for the assignment.
- The project was designed to be contained in a **single `.java` file** per the original assignment requirements.

### Possible future improvements
- Split `Card`, `Deck`, and `ConcentrationWorld` into separate files
- Replace placeholder assets with custom graphics
- Add difficulty levels and a cleaner UI
- Package resources properly on the classpath

## 📌 What This Project Demonstrates

- **Object-Oriented Programming:** `Card`, `DeckOfCards`, `ConcentrationWorld`
- **Event-driven GUI:** mouse clicks, ticks, and key events
- **Integration of images and audio** in Java
- **Unit testing** for logic validation
- Handling of state, scoring, and game-over conditions

---

