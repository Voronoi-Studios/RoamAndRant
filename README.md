<p align="center">
  <img src="https://github.com/user-attachments/assets/c68ad3cf-2537-4689-945a-bc5c9eecebbc" />
</p>

<h1 align="center">Roam and Rant</h1>

<p align="center">
  A lightweight dialogue, npc and crowd system mod for Hytale.<br/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-active-brightgreen"/>
  <img src="https://img.shields.io/badge/version-0.1-blue"/>
  <img src="https://img.shields.io/badge/hytale-mod-orange"/>
</p>

---

## Overview

**Roam and Rant** is a simple but flexible NPC system for Hytale mods.

Currently only the dialog part is done.
In the future this will be the smaller part besides the npc roaming and crowd system.

---

## Features

- Dialogue UI rendering  
- Branching dialogue paths
- trigger animations 
- trigger RootInteractions

---

## Example

```json
"DialogEntries":[
    {
      "Id": "01",
      "IsStart": true,
      "DialogText": "server.dialog.Guard_Example.01",
      "Animation": "Talk",
      "DialogButtons": [
        {
          "ButtonText": "Let me in!",
          "NextId": "02"
        },
        {
          "ButtonText": "What is this maze?",
          "NextId": "04"
        },
        {
          "ButtonText": "Goodbye",
          "Exit": true
        }
      ]
    },
    {
      "Id": "02",
      "NameOverride" : "You",
      "DialogText": "I would love to enter the Maze",
      "NextId": "03"
    },
    {
      "Id": "03",
      "DialogText": "Are you certain? Its very dangerous in there.",
      "Animation": "Talk",
      "DialogButtons": [
        {
          "ButtonText": "Enter the maze",
          "Interaction": "EnterInstanceMaze",
          "Exit": true
        },
        {
          "ButtonText": "Goodbye",
          "Exit": true
        }
      ]
    }
  ]
```

---

## Installation

Add the mod to your Hytale mods folder and load it in your project.


## Usage


## Roadmap / Upcoming Features


## Contributing

Pull requests and issue reports will be welcome, as soon as I'm trough the early phase (May)

## License

MIT License

---

<p align="center">
  Built for experimentation and future expansion.
</p>
