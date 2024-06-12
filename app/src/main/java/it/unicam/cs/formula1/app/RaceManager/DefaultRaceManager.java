/*
 * Copyright (c) 2024 Mattia Giaccaglia
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package it.unicam.cs.formula1.app.RaceManager;

import it.unicam.cs.formula1.Bot.Bot;
import it.unicam.cs.formula1.GameEngine.GameEngine;
import it.unicam.cs.formula1.app.RaceDisplay.RaceDisplay;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Default implementation of the {@link RaceManager} interface.
 * Manages the race by starting the race, checking for race completion, and determining if the race is finished.
 */
public class DefaultRaceManager implements RaceManager {

    private final GameEngine gameEngine;
    private final RaceDisplay raceDisplay;
    private AnimationTimer timer;
    private final Pane root;

    /**
     * Constructs a new DefaultRaceManager with the specified game engine, race display, and pane.
     *
     * @param gameEngine the game engine managing the game logic
     * @param raceDisplay the display for visualizing the race
     * @param root the pane on which to display the race
     */
    public DefaultRaceManager(GameEngine gameEngine, RaceDisplay raceDisplay, Pane root) {
        this.gameEngine = gameEngine;
        this.raceDisplay = raceDisplay;
        this.root = root;
    }

    /**
     * Starts the race and manages race updates using an {@link AnimationTimer}.
     */
    @Override
    public void startRace() {
        if (timer != null)
            timer.stop(); // Ferma il timer se è già in esecuzione
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameEngine.updateRace();
                raceDisplay.updateBotPositions(root);
                if (isRaceFinished()) {
                    stop();
                    checkRaceCompletion(gameEngine.getBots());
                }
            }
        };
        timer.start();
    }

    @Override
    public void checkRaceCompletion(List<Bot> bots) {
        for (Bot bot : bots)
            if (gameEngine.getTrack().getEndPositions().contains(bot.getCurrentPosition())) {
                Platform.runLater(() -> showRaceFinishedDialog(bot));
                break;
            }
    }

    @Override
    public boolean isRaceFinished() {
        return gameEngine.getBots().stream()
                .anyMatch(defaultBot -> gameEngine.getTrack().getEndPositions().contains(defaultBot.getCurrentPosition()));
    }

    /**
     * Displays a dialog indicating which bot has won the race.
     *
     * @param defaultBot the bot that won the race
     */
    private void showRaceFinishedDialog(Bot defaultBot) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Race finished!");
        alert.setHeaderText(null);
        alert.setContentText("Bot " + defaultBot.getName() + " won the race!");
        alert.showAndWait();
    }
}
