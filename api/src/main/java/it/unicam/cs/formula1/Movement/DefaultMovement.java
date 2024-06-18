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

package it.unicam.cs.formula1.Movement;

import it.unicam.cs.formula1.Position.Position;

/**
 * Default implementation of the {@link Movement} interface.
 * Provides methods to accelerate, decelerate, and calculate the main point for movement.
 */
public class DefaultMovement implements Movement {
    private static final int MAX_SPEED = 4;
    private int speed;

    /**
     * Constructs a new DefaultMovement with an initial speed of 1.
     */
    public DefaultMovement(){
        this.speed = 1;
    }

    @Override
    public Position accelerate(Position actualPosition, Position previousMove) {
        Position mainPoint = calculateMainPoint(actualPosition, previousMove);
        if (speed < MAX_SPEED) {
            speed++;
            return new Position(mainPoint.x() + Integer.signum(previousMove.x()), mainPoint.y() + Integer.signum(previousMove.y()));
        }
        return mainPoint;
    }

    @Override
    public Position decelerate(Position actualPosition, Position previousMove) {
        Position mainPoint = calculateMainPoint(actualPosition, previousMove);
        if (speed > 1) {
            speed--;
            Position position = new Position(mainPoint.x() - Integer.signum(previousMove.x()), mainPoint.y() - Integer.signum(previousMove.y()));
            if(!position.equals(actualPosition)) return position;
        }
        return mainPoint;
    }

    @Override
    public Position calculateMainPoint(Position actualPosition, Position previousMove) {
        return new Position(actualPosition.getX() + previousMove.getX(), actualPosition.getY() + previousMove.getY());
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
