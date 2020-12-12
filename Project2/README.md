# AIAD2020
mkdir bin

javac -d bin -cp libs/jade.jar;libs/SAJaS.jar;libs/asm.jar;libs/beanbowl.jar;libs/colt.jar;libs/commons-collections.jar;libs/commons-logging.jar;libs/geotools_repast.jar;libs/ibis.jar;libs/jakarta-poi.jar;libs/jep-2.24.jar;libs/jgap.jar;libs/jh.jar;libs/jmf.jar;libs/jode-1.1.2-pre1.jar;libs/joone.jar;libs/JTS.jar;libs/junit.jar;libs/log4j-1.2.8.jar;libs/OpenForecast-0.4.0.jar;libs/openmap.jar;libs/plot.jar;libs/ProActive.jar;libs/repast.jar;libs/trove.jar;libs/violinstrings-1.0.2.jar -sourcepath src src/game/RepastLauncher.java src/game/MonopolyMain.java src/game/Player.java src/behaviors/PlayListeningBehaviour.java

cd bin

java -cp .;../libs/jade.jar;../libs/SAJaS.jar;../libs/asm.jar;../libs/beanbowl.jar;../libs/colt.jar;../libs/commons-collections.jar;../libs/commons-logging.jar;../libs/geotools_repast.jar;../libs/ibis.jar;../libs/jakarta-poi.jar;../libs/jep-2.24.jar;../libs/jgap.jar;../libs/jh.jar;../libs/jmf.jar;../libs/jode-1.1.2-pre1.jar;../libs/joone.jar;../libs/JTS.jar;../libs/junit.jar;../libs/log4j-1.2.8.jar;../libs/OpenForecast-0.4.0.jar;../libs/openmap.jar;../libs/plot.jar;../libs/ProActive.jar;../libs/repast.jar;../libs/trove.jar;../libs/violinstrings-1.0.2.jar game/RepastLauncher
