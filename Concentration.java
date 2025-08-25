
// Concentration Game
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;
import java.net.URL;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

import javax.sound.sampled.AudioInputStream;

import javax.sound.sampled.AudioSystem;

import javax.sound.sampled.Clip;

//background music for the game 
class Music1 {

  public static Clip play(String audioName) {

    Clip clip = null;

    try {

      URL link = new URL("https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav");

      AudioInputStream audio =

          AudioSystem.getAudioInputStream(link);

      clip = AudioSystem.getClip();

      clip.open(audio);

      clip.start();

    }

    catch (Exception e) {

      e.printStackTrace();

    }

    return clip;

  }

}

//class for card
class Card {
  String rank;
  String suit;
  int value;
  boolean flipped;

  // constructor
  Card(String rank, String suit, int value) {
    this.rank = rank;
    this.suit = suit;
    this.value = value;
    this.flipped = false;
  }

  // method for flipping card
  void flip() {
    this.flipped = !flipped;
  }

  // method for seeing if two cards match
  boolean sameCard(Card c) {
    return this.value == c.value;
  }

  // draws for image of card
  WorldImage draw() {
    Color textColor;

    if (!flipped) {

      WorldImage startImage = new FromFileImage("pokemon (1).png");
      return startImage;

    }
    if (this.suit.equals("♦") || this.suit.equals("♥")) {
      textColor = Color.red;
    }
    else {
      textColor = Color.black;
    }
    return new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
        new TextImage(this.suit, 20.0, FontStyle.BOLD, textColor), 0, 0,
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            new TextImage(this.rank, 15.0, FontStyle.BOLD_ITALIC, textColor), 0, 0,
            new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.BOTTOM,
                new TextImage(this.rank, 15.0, FontStyle.BOLD_ITALIC, textColor), 0, 0,
                new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
                    new RectangleImage(100, 139, OutlineMode.OUTLINE, Color.BLACK), 0, 0,
                    new RectangleImage(100, 139, OutlineMode.SOLID, Color.WHITE)))));
  }

}

// class for a deck of cards
class DeckOfCards {

  ArrayList<ArrayList<Card>> board;

  DeckOfCards() {
    board = createBoard();

  }

  // creates the board by looping through all suits creating a row of a suit with
  // all the ranks
  ArrayList<ArrayList<Card>> createBoard() {
    ArrayList<ArrayList<Card>> board = new ArrayList<ArrayList<Card>>();

    ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♣", "♦", "♥", "♠"));
    ArrayList<String> ranks = new ArrayList<String>(Arrays.asList("Ace", "2", "3", "4", "5", "6",
        "7", "8", "9", "10", "Jack", "Queen", "King"));
    // creates board of all cards
    for (int i = 0; i < suits.size(); i++) {
      ArrayList<Card> row = new ArrayList<>();
      for (int j = 0; j < ranks.size(); j++) {
        int value = j + 1; // Number cards have value equal to their number
        Card card = new Card(ranks.get(j), suits.get(i), value);
        row.add(card);
      }
      board.add(row);
    }

    return board;
  }

  // method for shuffling deck
  void shuffleDeck() {
    ArrayList<Card> spreadOut = new ArrayList<Card>();

    // Flatten the board to a single list
    for (ArrayList<Card> row : board) {
      spreadOut.addAll(row);
    }

    // Shuffle the flattened deck
    Collections.shuffle(spreadOut);

    // Update the board with the shuffled cards
    int index = 0;
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        board.get(i).set(j, spreadOut.get(index));
        index++;
      }
    }

  }

  // draws teh background of game
  void draw(WorldScene ws) {
    Color skyBlue = new Color(135, 206, 235);
    ws.placeImageXY(new RectangleImage(2000, 2000, OutlineMode.SOLID, skyBlue), 1000, 1000);

    int yOffset = 100; // Offset for the vertical position of each row

    // creates positions of cards on screen
    for (ArrayList<Card> row : board) {

      int xOffset = 60; // Offset for the horizontal position of each card

      for (Card card : row) {

        // Create a TextImage representing the card with the appropriate color
        WorldImage cardImage = card.draw();

        // Place the card image on the WorldScene at the appropriate position
        ws.placeImageXY(cardImage, xOffset, yOffset);

        xOffset += 110; // Adjust the horizontal position for the next card
      }

      yOffset += 150; // Adjust the vertical position for the next row
    }

  }

  // calculates the row/column index based on the vertical position
  Card findCardByPosition(Posn pos) {
    int row = pos.y / 150;
    int col = pos.x / 110;

    if (row >= 0 && row < board.size() && col >= 0 && col < board.get(row).size()) {
      return board.get(row).get(col);
    }
    else {
      return null;
    }
  }

  // flips the card when they match
  boolean allFlipped() {
    boolean result = true;
    for (ArrayList<Card> row : board) {
      for (Card card : row) {
        boolean cardCheck = card.flipped;

        result = result && cardCheck;

        if (!result) {
          break;
        }
      }

      if (!result) {
        break;
      }
    }
    return result;
  }

  // method for counting the cards
  public int counter(Predicate<Card> pred) {
    int count = 0;
    for (ArrayList<Card> row : board) {
      for (Card card : row) {
        if (pred.test(card)) {
          count++;
        }
      }
    }
    return count;
  }

}

//class for implementing concentration world
class ConcentrationWorld extends World {
  DeckOfCards deck;
  Card selectedCardOne;
  Card selectedCardTwo;
  int tick;
  int clickCounter;
  int score;
  int time;
  Clip backgroundMusic;

  // constructor
  ConcentrationWorld() {
    this.deck = new DeckOfCards();
    deck.shuffleDeck();
    this.selectedCardOne = null;
    this.selectedCardTwo = null;
    this.tick = 0;
    this.clickCounter = 0;
    this.score = 0;
    this.time = 0;
    if (backgroundMusic == null) {
      this.backgroundMusic = Music1.play("song.wav");

    }

  }

  // secondary constructor for testing
  ConcentrationWorld(DeckOfCards deck) {
    this.deck = deck;
    this.selectedCardOne = null;
    this.selectedCardTwo = null;
    this.tick = 0;
    this.clickCounter = 0;
    this.score = 0;
    this.time = 0;
    if (backgroundMusic == null) {
      this.backgroundMusic = Music1.play("song.wav");
    }

  }

  // creates scene of cards
  @Override
  public WorldScene makeScene() {
    WorldScene ws = new WorldScene(2000, 2000);
    this.deck.draw(ws);
    ws.placeImageXY(
        new TextImage("You Have " + String.valueOf(250 - this.clickCounter) + " Clicks Left", 30.0,
            FontStyle.BOLD, Color.red),
        725, 800);
    ws.placeImageXY(
        new TextImage("Score: " + String.valueOf(this.score), 30.0, FontStyle.BOLD, Color.red), 200,
        800);
    ws.placeImageXY(new TextImage("Time: " + String.valueOf(this.time / 69) + " Seconds", 30.0,
        FontStyle.BOLD, Color.red), 200, 700);
    return ws;
  }

  // allows user to click on a card to flip
  @Override
  public void onMouseClicked(Posn pos) {
    // Find the clicked card based on the position
    Card clickedCard = findCardByPosition(pos);

    if (clickedCard != null && !clickedCard.flipped) {
      if (selectedCardOne == null) {
        // No card is currently selected, flip the clicked card
        clickedCard.flip();
        this.clickCounter++;
        selectedCardOne = clickedCard;
      }
      else if (selectedCardTwo == null) {
        // Another card is already selected, flip the clicked card and check for a match
        clickedCard.flip();
        this.clickCounter++;
        selectedCardTwo = clickedCard;
      }
    }
  }

  // creates image after game finishes
  public WorldScene makeAFinalScene() {
    WorldScene ws = new WorldScene(2000, 2000);
    WorldImage endImage = new FromFileImage("Victory.png");
    ws.placeImageXY(endImage, 350, 350);
    return ws;
  }

  // resets the board
  @Override
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.deck = new DeckOfCards();
      this.deck.shuffleDeck();
    }

  }

  // sees if match found, keep the cards flipped
  // no match, flips back over
  @Override
  public void onTick() {
    this.time++;
    if (selectedCardOne != null && selectedCardTwo != null) {
      if (selectedCardOne.sameCard(selectedCardTwo)) {
        // Match found, keep the cards flipped
        selectedCardOne = null;
        selectedCardTwo = null;
        this.score += 50;
      }
      else {
        // No match, increment the tick counter and flip both cards back after a delay
        tick++;
        if (tick >= 30) {
          selectedCardOne.flip();
          selectedCardTwo.flip();
          selectedCardOne = null;
          selectedCardTwo = null;
          tick = 0;
        }
      }
    }
  }

  // method for finding a card based on its positions
  Card findCardByPosition(Posn pos) {
    return this.deck.findCardByPosition(pos);
  }

  // method for finishing game, ends if all cards are flipped over
  @Override
  public WorldEnd worldEnds() {
    WorldScene ws = new WorldScene(2000, 2000);
    WorldImage endImage = new FromFileImage("wasted.png");
    ws.placeImageXY(endImage, 350, 350);
    if (this.deck.allFlipped()) {
      return new WorldEnd(true, this.makeAFinalScene());
    }
    if (this.clickCounter == 250) {
      return new WorldEnd(true, ws);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

//class for examples
class ExamplesConcentration1 {
  DeckOfCards test = new DeckOfCards();

  boolean testFindCard(Tester t) {
    return t.checkExpect(test.findCardByPosition(new Posn(60, 100)), new Card("Ace", "♣", 1))
        && t.checkExpect(test.findCardByPosition(new Posn(120, 100)), new Card("2", "♣", 2))
        && t.checkExpect(test.findCardByPosition(new Posn(120, 250)), new Card("2", "♦", 2));
  }

  // tests shuffles
  void testingShuffle(Tester t) {
    DeckOfCards test = new DeckOfCards();
    test.shuffleDeck();
    t.checkExpect(test.counter(card -> card.suit.equals("♠")), 13);
    t.checkExpect(test.counter(card -> card.suit.equals("♣")), 13);
    t.checkExpect(test.counter(card -> card.suit.equals("♦")), 13);
    t.checkExpect(test.counter(card -> card.suit.equals("♥")), 13);
    t.checkExpect(test.counter(card -> true), 52);

    t.checkExpect(test.allFlipped(), false);

  }

  // testing if two cards are the same when flipped
  void testingFlipAndSameCard(Tester t) {
    Card ace = new Card("Ace", "♣", 1);
    Card ace2 = new Card("Ace", "♦", 1);
    Card two = new Card("2", "♦", 2);
    Card two2 = new Card("2", "♥", 2);

    t.checkExpect(ace.flipped, false);
    ace.flip();
    t.checkExpect(ace.flipped, true);
    ace.flip();
    t.checkExpect(ace.flipped, false);

    t.checkExpect(ace.sameCard(ace2), true);
    t.checkExpect(two.sameCard(ace), false);
    t.checkExpect(two.sameCard(two2), true);

  }

  // tests world
  void testingworld(Tester t) {
    ConcentrationWorld world = new ConcentrationWorld(new DeckOfCards());
    // test findcardbyposition
    t.checkExpect(world.findCardByPosition(new Posn(60, 100)), new Card("Ace", "♣", 1));
    t.checkExpect(world.findCardByPosition(new Posn(120, 100)), new Card("2", "♣", 2));
    t.checkExpect(world.findCardByPosition(new Posn(120, 250)), new Card("2", "♦", 2));
    t.checkExpect(world.selectedCardOne, null);
    t.checkExpect(world.selectedCardTwo, null);
    Card card = new Card("Ace", "♣", 1);
    Card card2 = new Card("2", "♣", 2);
    t.checkExpect(card.draw(), new FromFileImage("pokemon (1).png"));
    t.checkExpect(card.draw(), new FromFileImage("pokemon (1).png"));
    card.flip();
    card2.flip();
    t.checkExpect(card.draw(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage(card.suit, 20.0, FontStyle.BOLD, Color.black), 0, 0,
            new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
                new TextImage(card.rank, 15.0, FontStyle.BOLD_ITALIC, Color.black), 0, 0,
                new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.BOTTOM,
                    new TextImage(card.rank, 15.0, FontStyle.BOLD_ITALIC, Color.black), 0, 0,
                    new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
                        new RectangleImage(100, 139, OutlineMode.OUTLINE, Color.BLACK), 0, 0,
                        new RectangleImage(100, 139, OutlineMode.SOLID, Color.WHITE))))));

    t.checkExpect(card2.draw(),
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage(card2.suit, 20.0, FontStyle.BOLD, Color.black), 0, 0,
            new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
                new TextImage(card2.rank, 15.0, FontStyle.BOLD_ITALIC, Color.black), 0, 0,
                new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.BOTTOM,
                    new TextImage(card2.rank, 15.0, FontStyle.BOLD_ITALIC, Color.black), 0, 0,
                    new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
                        new RectangleImage(100, 139, OutlineMode.OUTLINE, Color.BLACK), 0, 0,
                        new RectangleImage(100, 139, OutlineMode.SOLID, Color.WHITE))))));
    world.onMouseClicked(new Posn(60, 100));
    world.onMouseClicked(new Posn(120, 100));
    // test onmouseclicked
    t.checkExpect(world.selectedCardOne, card);
    t.checkExpect(world.selectedCardTwo, card2);
    // on tick 30 times to reset
    for (int i = 0; i < 30; i++) {
      world.onTick();
    }
    // check if ontick reset
    t.checkExpect(world.selectedCardOne, null);
    t.checkExpect(world.selectedCardTwo, null);
    // reset whole board, ace club 1 by random chance should not be at that position
    // unlucky if test fails :(
    world.onKeyEvent("r");
    world.onKeyEvent("r");
    t.checkExpect(world.findCardByPosition(new Posn(60, 100)).sameCard(new Card("Ace", "♣", 1)),
        false);

    // Create the expected WorldScene based on the provided code snippet
    WorldScene expectedScene = new WorldScene(2000, 2000);
    world.deck.draw(expectedScene);
    expectedScene.placeImageXY(
        new TextImage("You Have " + String.valueOf(250 - world.clickCounter) + " Clicks Left", 30.0,
            FontStyle.BOLD, Color.red),
        725, 800);
    expectedScene.placeImageXY(
        new TextImage("Score: " + String.valueOf(world.score), 30.0, FontStyle.BOLD, Color.red),
        200, 800);
    expectedScene
        .placeImageXY(new TextImage("Time: " + String.valueOf(world.time / 69) + " Seconds", 30.0,
            FontStyle.BOLD, Color.red), 200, 700);

    // Use 'checkExpect' to compare the actual and expected scenes
    t.checkExpect(world.makeScene(), expectedScene);

    // Modify the necessary properties and state of the 'world' object for testing
    world.clickCounter = 200;
    // Set the 'flipped' property of each card in the deck to true
    for (ArrayList<Card> row : world.deck.board) {
      for (Card cardlol : row) {
        cardlol.flip();
      }
    }

    // Create the expected WorldScene based on the provided code snippet and the
    // state of the 'world' object
    WorldScene expectedEndScene = new WorldScene(2000, 2000);
    WorldImage endImage = new FromFileImage("wasted.png");
    expectedEndScene.placeImageXY(endImage, 350, 350);

    // Create the expected WorldEnd object for the "allFlipped" condition
    WorldEnd expectedAllFlippedEnd = new WorldEnd(true, world.makeAFinalScene());

    // Use 'checkExpect' to compare the actual and expected WorldEnd objects
    t.checkExpect(world.worldEnds(), expectedAllFlippedEnd);

    // reset board
    world.onKeyEvent("r");

    // Continue playing
    t.checkExpect(world.worldEnds(), new WorldEnd(false, world.makeScene()));

    // Modify the 'clickCounter' property in the 'world' object to match the
    // "clickCounter > 250" condition
    world.clickCounter = 300;

    // Use 'checkExpect' to compare the actual and expected WorldEnd objects
    t.checkExpect(world.worldEnds(), new WorldEnd(false, world.makeScene()));

    // Modify the 'flipped' property of each card in the deck to false
    for (ArrayList<Card> row : world.deck.board) {
      for (Card cardlol : row) {
        cardlol.flip();
      }
    }

  }

  // tests big bang

  void testBigBang(Tester t) {

    ConcentrationWorld world = new ConcentrationWorld();

    int worldWidth = 2000;

    int worldHeight = 2000;

    double tickRate = 0.01;

    world.bigBang(worldWidth, worldHeight, tickRate);

  }

}
