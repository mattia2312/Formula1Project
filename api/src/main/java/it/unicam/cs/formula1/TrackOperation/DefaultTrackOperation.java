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

package it.unicam.cs.formula1.TrackOperation;

import it.unicam.cs.formula1.Bot.Bot;
import it.unicam.cs.formula1.Position.Position;
import it.unicam.cs.formula1.Track.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a record-based implementation of the {@link TrackOperation} interface.
 * Provide concrete details on how to check and manipulate positions on a {@link Track}.
 *
 * @param track The track on which operations are performed.
 */
public record DefaultTrackOperation(Track track) implements TrackOperation {
    private static int[][] trackLayout;

    /**
     * Constructs a new DefaultTrackOperation with the specified track.
     * Initializes the track layout from the provided track.
     *
     * @param track the track on which operations are performed
     */
    public DefaultTrackOperation(Track track) {
        this.track = track;
        trackLayout = track.getTrackLayout();
    }

    @Override
    public boolean isValidAndPassable(Position mainPoint, Position mainPoint1, Position mainPoint2) {
        return isValidPosition(mainPoint) && isValidPosition(mainPoint1) &&
                checkPassableTrack(mainPoint, mainPoint1) &&
                checkPassableTrack(mainPoint1, mainPoint2);
    }

    @Override
    public boolean checkPassableTrack(Position start, Position arrive) {
        int dx = Math.abs(arrive.getX() - start.getX());
        int dy = -Math.abs(arrive.getY() - start.getY());
        int sx = Integer.signum(arrive.getX() - start.getX());
        int sy = Integer.signum(arrive.getY() - start.getY());
        int err = dx + dy;
        int x = start.getX();
        int y = start.getY();
        while (true) {
            if (!isValidPosition(new Position(x, y)))
                return false;
            if (x == arrive.getX() && y == arrive.getY())
                return true;
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y += sy;
            }
        }
    }

    @Override
    public List<Position> calculateNearbyMoves(Position position) {
        List<Position> nearbyMoves = new ArrayList<>(8);
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++){
                    Position newPoint = new Position(position.getX() + dx, position.getY() + dy);
                    if (isValidPosition(newPoint))
                        nearbyMoves.add(newPoint);
                }
        nearbyMoves.remove(position);
        return nearbyMoves;
    }

    @Override
    public boolean isValidPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && y >= 0 && x < trackLayout.length && y < trackLayout[x].length && trackLayout[x][y] != 0;
    }

    @Override
    public void executeNearbyMove(Bot bot) {
        Position currentPosition = bot.getCurrentPosition();
        Position mainPoint = bot.getMovement().calculateMainPoint(currentPosition, bot.getPreviousMove());
        List<Position> validMoves = calculateNearbyMoves(mainPoint).stream()
                .filter(move -> !move.equals(currentPosition) && calculateNearbyMoves(currentPosition).contains(move))
                .toList();
        if (validMoves.isEmpty())
            bot.isEliminated(true);
        else {
            bot.getMovement().decelerate(mainPoint, bot.getPreviousMove());
            bot.updatePosition(validMoves.get(new Random().nextInt(validMoves.size())));
        }
    }
}
