# 🏆 EventsMania

**EventsMania** is a high-performance competition framework for Minecraft servers. Unlike traditional minigames that
teleport players away to a different world, EventsMania runs **on top** of your existing gameplay, turning everyday
activities into exciting, server-wide races for rewards.

## 🤔 How it Works

EventsMania doesn't host minigames like FFA or TNT Run. Instead, it tracks **player actions** (Statistics) across your
server for a set period.

1. **Preparation:** A countdown begins. Players are notified via titles, sounds, and chat that a challenge is about to
   start.
2. **The Challenge:** For a defined duration (e.g., 10 minutes), the plugin tracks a specific statistic—such as *Most
   Ores Mined*, *Most Fish Caught*, or any custom variable via *PlaceholderAPI*.
3. **Real-Time Race:** Players compete in real-time. You can trigger "milestone" actions (messages, sounds, actionbars)
   at specific times during the event.
4. **Victory:** When the timer hits zero, the top players on the leaderboard are automatically rewarded with custom
   commands, items, or messages.

## ✨ Key Features

* **⚡ Non-Intrusive Gameplay:** Create engagement without interrupting the player's session. No lobbies, no waiting,
  just competition.
* **🎬 Immersive Action System:** Keep players engaged with dynamic Titles, Subtitles, Actionbars, and custom Sound
  triggers throughout the event stages.
* **📊 Flexible Statistic Tracking:** Track progress using internal metrics or any value provided by **PlaceholderAPI**.
  If a plugin has a placeholder, you can turn it into an event!
* **📈 Dynamic Leaderboards:** High-performance tracking that handles your entire player base with minimal impact on
  server TPS.
* **🎁 Automated Rewards:** Fully customizable reward tiers. Give specific prizes for 1st, 2nd, or 3rd place, or
  participation rewards for everyone.
* **🛠 Integrated Editor:** (Powered by Waves) Includes an interactive configuration system with chat-based input for
  easy setup.

## Requirements

* **Platform:** Paper 1.21+
* **Java Version:** 21
* **Core Engine:** [Waves](https://github.com/aquatic-development/Waves) (Required)
* **Hooks:** PlaceholderAPI (Recommended)

## 🧩 Placeholders

### 1. Global PlaceholderAPI (External)

Use these in other plugins (Scoreboards, TAB, Chat) to display general event info.

| Placeholder                                  | Description                                                         |
|:---------------------------------------------|:--------------------------------------------------------------------|
| `%eventsmania_isrunning%`                    | Returns `true` or `false` if an event is active.                    |
| `%eventsmania_leaderboard_rank%`             | The current player's rank in the active event.                      |
| `%eventsmania_leaderboard_wins%`             | Total lifetime wins for the player.                                 |
| `%eventsmania_leaderboard_wins_<place>%`     | Returns the score of the player at the specified rank (e.g., `_1`). |
| `%eventsmania_leaderboard_username_<place>%` | Returns the name of the player at the specified rank (e.g., `_1`).  |

### 2. Internal Placeholders (Event-Only)

These are available within your event configurations for messages, titles, and rewards.

| Placeholder                | Description                                                 |
|:---------------------------|:------------------------------------------------------------|
| `%leaderboard-name-N%`     | Name of player at rank **N** (1-10).                        |
| `%leaderboard-value-N%`    | Score of player at rank **N** (1-10).                       |
| `%leaderboard-name-self%`  | The player's own username.                                  |
| `%leaderboard-value-self%` | The player's current score.                                 |
| `%leaderboard-rank%`       | The player's current numeric rank.                          |
| `%tick%`                   | Current time elapsed in the event.                          |
| `%rank%`                   | Used in **Rewards** to display the winner's final position. |

---

## 💬 Community & Support

Got questions, need help, or want to showcase what you've built with **EventsMania**? Join our community!

[![Discord Banner](https://img.shields.io/badge/Discord-Join%20our%20Server-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://discord.com/invite/ffKAAQwNdC)

* **Discord**: [Join the Aquatic Development Discord](https://discord.com/invite/ffKAAQwNdC)
* **Issues**: Open a ticket on GitHub for bugs or feature requests.