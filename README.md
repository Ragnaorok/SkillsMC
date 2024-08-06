# SkillsMC: an RPG plugin (1.21.X)

## Overview
This Minecraft plugin introduces custom skills for different tools and weapons based off what class you choose, providing unique abilities to enhance gameplay. Key features include shield surfing, player bounties, and mob death effects.

## Version Information
- Current Version: 1.0.0 (Alpha)
- Major Updates: Initial release with core features.


## Features
- **Shield Surfing**: Allows players to surf using a shield, applying a dynamic particle effect and custom sound upon landing.
- **Classes**: Choose from 4 available classes: Warrior, Mage, Cleric, Archer
- **Custom Items and Recipes**: Adds the new Blast Axe, and mob death effects.

## Installation
1. Download the plugin JAR file.
2. Place the JAR file into the `plugins` directory of your Minecraft server.
3. Restart the server to load the plugin.

## Usage
### Shield Surfing
- Block while crouching to initiate shield surfing.
- Upon landing, if player would take fall damage you would bounce in the direction you're looking instead.
- Shield surfing is disabled if the shield has only 1 durability left to prevent shield from breaking.
  
### Class Modifiers
- Warrior: +1 Heart
- Archer : +.2 Walkspeed
- Cleric : Passive Rejuvation 1 potion effect
- Mage   : -1 Heart & Mana Shield (reduce incoming damage by 10% at the cost of mana)

### Loadouts
- Different combinations of main and offhand result in different skills 
- Current available loadouts: Mainhand Sword, Sword and Shield, Mainhand Shield, Dual Swords, Axe/Blast Axe, Crossbow, Blaze Rod, Netherite Hoe

## Custom Items and Recipes
### Blast Axe
- Combines an Iron Axe and Flint & Steel to create the Blast Axe.
- Hit a monster while sprinting for a powerful strike.
  
### Souls System (Planned Feature)
- This feature is currently in development and will be available in future updates.
- Planned functionality: Kill certain mobs for a chance to obtain souls. Souls can be used to improve or unlock new skills.

  
## Commands
- `/souls` admin command to modify soul count
- `/kaboom` gag command
- `/class` choose your class
- `/profile` view a player's class stats & currency
- `/guide` gives the player a guidebook

## Dependencies
- Minecraft server running Paper
- EffectLib (for particle effects)

## Development
### Main Class
- `Main` class handles the initialization and registration of all event listeners and commands.

### How to Contribute
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact
For any questions or suggestions, please reach out to [Tung Nguyen](https://github.com/Ragnaorok).

