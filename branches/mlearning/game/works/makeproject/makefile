# echo $SHELL
SHELL = /bin/bash

CURRENTPATH=$(shell /bin/pwd)/..

JAR_PKG = game.jar

RES_DIR = yes

ENTRY_POINT = main.game

SOURCE_FILES = \
$(CURRENTPATH)/source/utils/BetState.java \
$(CURRENTPATH)/source/utils/Color.java \
$(CURRENTPATH)/source/utils/Poker.java \
$(CURRENTPATH)/source/utils/CardType.java \
$(CURRENTPATH)/source/utils/PokerComparator.java \
$(CURRENTPATH)/source/utils/CardGroup.java \
$(CURRENTPATH)/source/utils/CardGroupComparator.java \
$(CURRENTPATH)/source/utils/MaxCardComputer.java \
$(CURRENTPATH)/source/utils/Constants.java \
$(CURRENTPATH)/source/utils/InitState.java \
$(CURRENTPATH)/source/utils/MlCardType.java \
$(CURRENTPATH)/source/utils/MlHoldCardType.java \
$(CURRENTPATH)/source/utils/BetRecord.java \
$(CURRENTPATH)/source/betPredict/Pair.java \
$(CURRENTPATH)/source/betPredict/UserUnit.java \
$(CURRENTPATH)/source/betPredict/BetPredict.java \
$(CURRENTPATH)/source/utils/SuperAI.java \
$(CURRENTPATH)/source/simpleAI/SimpleAI.java \
$(CURRENTPATH)/source/AdvancedAI/AdvancedAI.java \
$(CURRENTPATH)/source/AdvancedAI/MoreAI.java \
$(CURRENTPATH)/source/AdvancedAI/LessAI.java \
$(CURRENTPATH)/source/AdvancedAI/SelectAI.java \
$(CURRENTPATH)/source/client/GameClient.java \
$(CURRENTPATH)/source/main/game.java


JAVAC = javac

JFLAGS = -encoding UTF-8

vpath %.class $(CURRENTPATH)/bin
vpath %.java $(CURRENTPATH)/source

# Default:
#	@echo $(CURRENTPATH)
#	@echo "make new: new project, create src, bin, res dirs."
#	@echo "make build: build project."
#	@echo "make clean: clear classes generated."
#	@echo "make rebuild: rebuild project."
#	@echo "make run: run your app."
#	@echo "make jar: package your project into a executable jar."

Default: jar


build: $(SOURCE_FILES:.java=.class)

%.class: %.java
	$(JAVAC) -cp $(CURRENTPATH)/bin -d $(CURRENTPATH)/bin $(JFLAGS) $<

rebuild: clean build

.PHONY: new clean run jar

new:
ifeq ($(RES_DIR),yes)
	mkdir -pv $(CURRENTPATH)/source $(CURRENTPATH)/bin
else
	mkdir -pv $(CURRENTPATH)/source $(CURRENTPATH)/bin
endif

clean:
	rm -rf $(CURRENTPATH)/bin
	mkdir $(CURRENTPATH)/bin
	rm -frv $(CURRENTPATH)/bin/*

run:
	java -cp $(CURRENTPATH)/bin $(ENTRY_POINT)

jar: rebuild

ifeq ($(RES_DIR),yes)
	jar cvfe $(JAR_PKG) $(ENTRY_POINT)  -C $(CURRENTPATH)/bin .
else
	jar cvfe $(JAR_PKG) $(ENTRY_POINT) -C $(CURRENTPATH)/bin .
endif
	mv $(JAR_PKG) $(CURRENTPATH)/target
