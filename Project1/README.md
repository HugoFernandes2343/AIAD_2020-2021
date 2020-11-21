# AIAD2020
mkdir bin

javac -d bin -cp libs/jade.jar -sourcepath src src/game/MonopolyMain.java src/game/Player.java src/behaviors/PlayListeningBehaviour.java

cd bin

java -cp .;../libs/jade.jar game/MonopolyMain


existe um video de execução num .7z que pode descomprimir se a janela do computador que esta a usar nao tiver 1600x1080 de tamanho, porque se nao tiver esse tamanho nao conseguira ver toda a UI