# AIAD2020
mkdir bin

javac -d bin -cp libs/jade.jar -sourcepath src src/game/MonopolyMain.java src/game/Player.java src/behaviors/PlayListeningBehaviour.java

cd bin

java -cp .;../libs/jade.jar game/MonopolyMain