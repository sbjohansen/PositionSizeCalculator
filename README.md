# Position Size Calculator

Position Size Calculator is a user-friendly Java Swing application designed to help traders determine their optimal position sizes. Based on your account balance, risk tolerance, stop loss, and selected entry strategy, the tool calculates the appropriate position size for long or short trades. It supports multiple entry strategies including:

- **Single Entry**
- **Equal-Sized Dollar-Cost Averaging (DCA)**
- **Exponential Entries**
- 
## Download

Download the latest release from the [Releases](https://github.com/sbjohansen/PositionSizeCalculator/releases) page. Once downloaded, you can install application and it will create shortcut on your desktop.

Direct link [PositionSizeCalculator](https://github.com/sbjohansen/PositionSizeCalculator/releases/download/v1.0.21/PositionSizeCalculator.exe)

```bash
java -jar PositionSizeCalculator.jar
``` 

Building from Source
If you prefer to build the application from source:

Clone the repository:


```bash
git clone https://github.com/yourusername/PositionSizeCalculator.git
cd PositionSizeCalculator
``` 

Compile the Java source code:

```bash
javac PositionSizeCalculator.java
``` 

Package into a JAR file:

```bash
jar cfe PositionSizeCalculator.jar PositionSizeCalculator *.class
``` 

Run the JAR file:

```bash
java -jar PositionSizeCalculator.jar
``` 
