# Easy Farming

Highlights and Overlays to make farming runs easier.

## Features

### Smart Item Tracking
- **Real-time inventory monitoring** - Tracks all required items for your selected farming run
- **Tool Leprechaun integration** - Automatically checks stored compost at Tool Leprechauns
- **Rune pouch support** - Recognizes runes stored in both regular and divine rune pouches
- **Combination rune compatibility** - Properly handles combination runes (dust, mist, mud, lava, steam, smoke)
- **Visual item highlighting** - Shows missing items with quantities needed

### Supported Farming Locations
- **Herb runs** - 9 locations including Ardougne, Catherby, Falador, Farming Guild, Harmony Island, Kourend, Morytania, Troll Stronghold, and Weiss
- **Tree runs** - 6 locations including Falador, Farming Guild, Gnome Stronghold, Lumbridge, Taverley, and Varrock
- **Fruit tree runs** - 6 locations including Brimhaven, Catherby, Farming Guild, Gnome Stronghold, Lletya, and Tree Gnome Village

### Flexible Teleport Options
- **Multiple teleport methods** for each location including:
  - Portal Nexus (PoH)
  - Standard spellbook teleports
  - Teleport tabs
  - Achievement diary rewards (Ardougne cloaks, Explorer's rings)
  - Special items (Ectophial, Xeric's Talisman, etc.)
  - Jewellery box options
- **Customizable teleport preferences** - Choose your preferred method for each location

### Visual Enhancements
- **Color-coded highlighting** for different interaction types:
  - Blue for left-click actions
  - Green for right-click actions  
  - Pink for "Use" item actions
- **Adjustable transparency** for overlay visibility
- **Info box integration** with helpful tips and status updates

### Extensive Configuration
- **Location toggles** - Enable/disable specific patches based on your requirements
- **Compost selection** - Choose between regular, super, ultra, or bottomless compost
- **Tool preferences** - Optional rake and seed dibber inclusion
- **Limpwurt support** - Include limpwurt seeds in herb runs where applicable

## Usage

### Getting Started
1. **Open the plugin panel** by clicking the Easy Farming icon in the sidebar
2. **Configure your preferences** in the plugin settings:
   - Select which locations to include in your runs
   - Choose your preferred teleport methods
   - Set your compost type and tool preferences
3. **Start a farming run** by clicking one of the three buttons:
   - "Herb Run" for herb patches
   - "Tree Run" for tree patches  
   - "Fruit Tree Run" for fruit tree patches

### During Your Run
- **Item collection phase**: The overlay will show missing items with quantities needed
- **Teleport guidance**: Once all items are collected, teleport overlays will appear
- **Visual feedback**: Patches and items will be highlighted based on your color preferences

### Tips for Optimal Use
- **Rune pouch compatibility**: The plugin recognizes runes stored in rune pouches and combination runes
- **Tool Leprechaun storage**: Store compost at Tool Leprechauns to save inventory space
- **Bottomless compost bucket**: Highly recommended for efficiency
- **Achievement diary rewards**: Use Ardougne cloaks and Explorer's rings for free teleports

## Configuration Options

### General Settings
- **Highlight colors**: Customize left-click, right-click, and "Use" item colors
- **Transparency**: Adjust overlay opacity (0-255)
- **Compost type**: Choose your preferred compost method
- **Tools**: Toggle rake and seed dibber inclusion
- **Protection**: Enable farmer payment reminders

### Location Settings
Each farming type has its own section where you can:
- Enable/disable specific locations
- View quest and level requirements
- Configure teleport preferences

### Teleport Configuration
For each location, you can select from multiple teleport options:
- Portal Nexus (requires PoH)
- Standard spellbook teleports
- Teleport tabs
- Achievement diary rewards
- Special items and jewellery

## Requirements

### Quest Requirements
- **Harmony Island**: Elite Morytania diary
- **Troll Stronghold**: My Arm's Big Adventure
- **Weiss**: Making Friends with My Arm + The Fire of Nourishment
- **Lletya**: Start of Mourning's End Part I

### Level Requirements
- **Farming Guild (Herbs)**: 65 Farming + 60% Hosidious favour
- **Farming Guild (Trees)**: 65 Farming
- **Farming Guild (Fruit Trees)**: 85 Farming

## Troubleshooting

### Common Issues
- **Items not showing**: Ensure you have the correct teleport method selected in settings
- **Overlay not appearing**: Check that the farming run is active and items are missing
- **Rune pouch not recognized**: Make sure runes are properly stored in the pouch

### Getting Help
If you encounter any issues:
1. Check the plugin configuration matches your setup
2. Verify you have the required quests/levels for specific locations
3. Ensure your teleport preferences are correctly configured

## Architecture Notes

### Teleport Model

The plugin uses a canonical teleport model (`com.easyfarming.core.Teleport`) for all teleport-related functionality:

- **Canonical Model**: `com.easyfarming.core.Teleport` is the single source of truth for teleport data
- **Location Integration**: `Location` class stores and returns `core.Teleport` instances directly
- **Data-Driven Approach**: Location data is defined in the `locations.*` package using `LocationData` classes
- **Factory Pattern**: `LocationFactory` creates `Location` instances from `LocationData`

When adding new locations or teleport options:
1. Create a `LocationData` class in the appropriate subdirectory (e.g., `locations/herbs/`, `locations/tree/`)
2. Use `TeleportData` to define teleport options with `core.Teleport.Category` enum
3. Use `LocationFactory.createLocation()` to create `Location` instances

### Package Structure

- **`locations.*`**: Data layer - defines location data (teleports, coordinates, etc.)
- **`ItemsAndLocations.*`**: Business logic layer - calculates item requirements per run type
- **`core.*`**: Core models - `Teleport` and `Location` (if applicable) canonical models
- **`overlays.*`**: UI layer - rendering and highlighting logic

## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests.

## License

This project is licensed under the BSD 2-Clause "Simplified" License - see the LICENSE file for details.

## Acknowledgments

- Built for the RuneLite client