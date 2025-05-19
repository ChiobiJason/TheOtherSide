# 🐣 The Other Side

**The Other Side** is a fast-paced dodge-and-collect mobile game where players control a chicken dodging falling carts while collecting coins. The game tests reflexes, timing, and strategic movement across multiple lanes.

---

## 🎮 Game Description

You play as a determined chicken trying to survive and score high in a chaotic world where carts fall randomly across lanes. Dodge them and collect as many coins as you can!

## 🔑 Core Features

- 🐔 Chicken character controlled with swipe gestures
- 🚧 Randomly falling carts that the player must avoid
- 💰 Coins falling in different lanes to be collected for score
- 🧠 Tutorial and joke screens to enhance user experience
- 💾 High score tracking
- 🛒 Shop screen for future power-ups or cosmetic upgrades

---

## 👥 Team Members

- [Chisom Chiobi](https://github.com/ChiobiJason)
- [Cherie Eze](https://github.com/cherieeze)
- [Victor Jason-Nwachukwu](https://github.com/jaxonsxx)

---

## 📁 Project Structure

```
com.example.theotherside/
│
├── Cart.java                # Defines falling cart behavior
├── Chicken.java             # Player character logic
├── Coin.java                # Coin logic and collection
├── GameActivity.java        # Main game loop and control
├── GameObject.java          # Superclass for game entities
├── GameView.java            # Handles rendering & updates
├── HUD.java                 # Displays score, lives, etc.
├── Screen1.java             # Game screen (possibly legacy)
├── ScreenGameOver.java      # Game over screen
├── ScreenHighScore.java     # Shows highest score
├── ScreenJokePunchline1/2.java  # Joke screens for engagement
├── ScreenTitle.java         # Main title screen
├── Shop.java                # Shop screen logic
├── SoundManager.java        # Game sound effects
├── SwipeGestureDetector.java # Handles swipe input
└── Tutorial.java            # How-to-play tutorial screen

res/
├── layout/
│   ├── screen_game_over.xml
│   ├── screen_high_score.xml
│   ├── screen_how_to_play.xml
│   ├── screen_joke_punchline_1.xml
│   ├── screen_joke_punchline_2.xml
│   ├── screen_joke_setup.xml
│   ├── screen_shop.xml
│   └── screen_title.xml
├── drawable/                # Game assets and images
├── font/                    # Custom fonts
├── raw/                     # Sound files
├── values/                  # Colors, strings, styles
└── xml/                     # Additional configuration
```

---

## 🔧 Setup Instructions

1. **Clone the repo:**
   ```bash
   git clone https://github.com/ChiobiJason/TheOtherSide.git
   ```

2. **Open in Android Studio**

3. **Build and Run on Emulator or Device**

4. **Play!**

---

## 🛠 Tech Stack

- Language: **Java**
- IDE: **Android Studio**
- Platform: **Android (min supported version configurable)**
- UI/UX: **XML layouts + Figma prototypes**
- Input: **Touch gestures (swipe)**

---

## 🧑‍🎨 Design File

[Figma Design File](https://www.figma.com/design/cSiCp7Lri3HcPnQKwbGGIg/The-Other-Side?node-id=0-1&t=Zh2rSxcid5ejz7h3-1)

---

## 🚧 Future Plans

- Introduce power-ups (e.g., temporary invincibility)
- Add chicken customization
- Implement difficulty scaling
- Integrate Firebase for cloud save and leaderboard
- Implement Shop

---

## 📃 License

This game is developed as part of a course project and is for educational purposes only.