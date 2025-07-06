[![Java CI with Maven](https://github.com/TW2/SSASS/actions/workflows/maven.yml/badge.svg)](https://github.com/TW2/SSASS/actions/workflows/maven.yml)

# SSASS
### SSA ASS
This library isn't ready for production, it is the very very start and it is destined to offer an Java alternative to libass. Later I will do the render for CSASS too !
I have removed some specifics LoliSub class but this project is aimed to be the same of the internal renderer of LoliSub. There is no dependency and no test on Swing or another UI interface.
All help is welcome, you can go to my Discord TW2 channel, or propose your help simply by PR and Issue. Your are also free to clone it and use it for you respecting the terms of the GNU/GPLv3 license.
The main class just show "Hello" in the terminal. It can be a good test. But it's not interesting at all. Please only use one of the latest version of Java. By the way I use Java 24.

### How to build it :

1/ Have git, maven and the last version of Java installed and configurated in your system (this project uses Java 23).

2/ Clone this repository in a Terminal by using the command :<br>```git clone https://github.com/TW2/SSASS.git```

3/ In the folder created by the command ```git clone```, type ```mvn verify``` in your Terminal.

4/ Enter target subfolder.

5/ Launch with the command java -jar targeting the SSASS shaded version :<br>```java -jar SSASS-0.0-SNAPSHOT-shaded.jar```

### Tags taken into account :

- [x] Drawing commands p and subcommands m l b s p c and the n which is in the meantime equals to m
- [ ] Drawing baseline pbo
- [x] Font specific : b i u s fn fs r
- [x] Rescale/alter : fsc fscx fscy fsp
- [ ] Others

You can join me on Discord to speak or idle, in English or French (cause I'm a half white black Frenchy).

[![Discord](https://github.com/user-attachments/assets/99ec6536-7624-41c1-afd1-7993fc4a1e25)](https://discord.gg/ef8xvA9wsF)
