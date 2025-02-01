# Position Size Calculator

**Position Size Calculator** is a comprehensive Java Swing application that helps traders both prospectively size their trades and perform post‐trade profit analysis. The tool supports multiple entry strategies and provides detailed feedback on position allocation and risk–reward metrics. The application is designed with a clean, two‑column interface that displays settings on the left and calculation results on the right, and it supports multiple tabs for running concurrent calculations.

## Features

### 1. Dual Modes
- **Position Calculator:**  
  Determine your optimal position size based on:
  - **Account Information:** Enter your account balance and risk percentage.
  - **Stop Loss:** Enter your stop loss level.
  - **Entry Strategy:** Choose from one of the following strategies:
    - **Single Entry**
    - **Equal-Sized Dollar-Cost Averaging (DCA)**
    - **Exponential Entries**
  - The tool automatically sorts your entry prices based on your stop loss (for a long position, prices are sorted from highest to lowest; for a short position, from lowest to highest) and calculates the average entry price.
  - The results panel (displayed in the right column) shows:
    - Total Position Size (USD)
    - Total Risk (USD)
    - For each entry: the allocated USD amount along with the entry price that allocation is based on.

- **Profit Calculator:**  
  Perform post‑trade analysis using:
  - **Trade Details:** Enter your account balance and risk percentage.
  - **Stop Loss:** Enter your stop loss level (moved into a dedicated panel).
  - **Entry Settings:** Mark which entry prices were actually triggered (with automatic sorting based on stop loss).
  - **Trade Actual Close Price (Optional):** Enter a global close price if you want to analyze the effect on remaining open position.
  - **Take Profit Setup:** For each TP segment, specify:
    - The planned TP price.
    - The percentage of the original position to close.
    - Mark whether that TP level was triggered.
  - The application calculates:
    - Profit from each triggered TP.
    - If a global close price is provided, the profit for the remaining open portion.
    - A final **Risk–Reward Ratio** (total profit divided by total risk).

### 2. Multi-Tab Interface
- Open multiple calculator tabs in either mode using the "+" button.
- Each tab includes a custom header with a close ("X") button so that you can easily remove tabs.

### 3. Clean, Two-Column Layout
- Both calculators display all input settings (account info, stop loss, entries, TP levels, etc.) in a compact left-hand column.
- Calculation results are presented in a dedicated right-hand column for clear, side-by-side comparison.

### 4. Built-In Help and Disclaimer
- A **Help** menu is available from the top toolbar, opening a separate window with detailed usage instructions.
- A disclaimer is displayed at the bottom of the main window stating that the tool may contain bugs and that you use it at your own risk.
- © SBJ is displayed as the copyright.

## Download

Download the latest release from the [Releases](https://github.com/sbjohansen/PositionSizeCalculator/releases) page. Once downloaded, install the application—it will create a shortcut on your desktop.

Direct link: [PositionSizeCalculator.exe](https://github.com/sbjohansen/PositionSizeCalculator/releases/download/v1.0.27/PositionSizeCalculator.exe)

```bash
java -jar PositionSizeCalculator.jar
```
### Building from Source
If you prefer to build the application from source:

1. Clone the repository:

```bash
git clone https://github.com/sbjohansen/PositionSizeCalculator.git
cd PositionSizeCalculator
```

2. Compile the Java source code:

```bash
javac *.java
```

4. Package into a JAR file:

```bash

jar cfe PositionSizeCalculator.jar PositionSizeCalculator *.class
```

5. Run the JAR file:

```bash
java -jar PositionSizeCalculator.jar
```

### Disclaimer
This tool is provided "as is" without any warranty. It might contain bugs, and you are solely responsible for its use. Use it at your own risk.

© 2023 SBJ


