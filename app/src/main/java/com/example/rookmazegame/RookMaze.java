package com.example.rookmazegame;

import java.util.Random;

public class RookMaze {
    // Random class
    Random rand;

    // Maze variables
    int mazeRows;
    int mazeColumns;
    int solutionLength;
    int[][] rookMaze;

    // Player movement variables
    int playerMoveCounter;
    boolean playerMazeSolved;
    int currentPlayerPositionY;
    int currentPlayerPositionX;

    int solutionPositionX;
    int solutionPositionY;


    public RookMaze() {
        rand = new Random();
        mazeRows = 5;
        mazeColumns = 5;
        solutionLength = 4;
        rookMaze = new int[mazeRows][mazeColumns];

        playerMoveCounter = 0;
        playerMazeSolved = false;
        currentPlayerPositionY = 0;
        currentPlayerPositionX = 0;
    }

    public void setDifficulty(int difficulty) {
        if (difficulty == 1) {
            mazeRows = 5;
            mazeColumns = 5;
            solutionLength = 4;
            rookMaze = new int[mazeRows][mazeColumns];
        } else if (difficulty == 2) {
            mazeRows = 7;
            mazeColumns = 7;
            solutionLength = 5;
            rookMaze = new int[mazeRows][mazeColumns];
        } else if (difficulty == 3) {
            mazeRows = 8;
            mazeColumns = 8;
            solutionLength = 6;
            rookMaze = new int[mazeRows][mazeColumns];
        } else {
            throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
    }

    // Generate the maze by generating a solution, then populating empty spaces
    public void generateMaze() {
        int stepCounter = 0;
        int direction = 0;
        int currentYPosition = 0;
        int currentXPosition = 0;
        int currentValue = 0;

        while (stepCounter <= solutionLength) {
            // Pick a random number that corresponds to a direction
            direction = setRandomDirectionNumber();

            // Set the current positions number
            rookMaze[currentYPosition][currentXPosition] = setRandomMazeNumber();
            // Get the current number to check how far we can move
            currentValue = rookMaze[currentYPosition][currentXPosition];

            if (direction == 0) {
                if (isValid(currentYPosition + currentValue, currentXPosition)) {
                    currentYPosition += currentValue;
                    stepCounter += 1;
                }

            } else if (direction == 1) {
                if (isValid(currentYPosition, currentXPosition + currentValue)) {
                    currentXPosition += currentValue;
                    stepCounter += 1;
                }

            } else if (direction == 2) {
                if (isValid(currentYPosition - currentValue, currentXPosition)) {
                    currentYPosition -= currentValue;
                    stepCounter += 1;
                }

            } else if (direction == 3) {
                if (isValid(currentYPosition, currentXPosition - currentValue)) {
                    currentXPosition -= currentValue;
                    stepCounter += 1;
                }
            }
        }

        // Set the goal
        solutionPositionX = currentXPosition;
        solutionPositionY = currentYPosition;
        rookMaze[currentYPosition][currentXPosition] = setGoal();
        // Populate the empty spaces in the maze (0's)
        populateMaze();
    }

    public int getSolutionPositionX() {
        return solutionPositionX;
    }

    public int getSolutionPositionY() {
        return solutionPositionY;
    }

    // Populate empty spaces in the maze with a random number
    // Should be called after generateMaze, within the generateMaze() method
    public void populateMaze() {
        for (int i = 0; i < getMazeRows(); i++) {
            for (int j = 0; j < getMazeColumns(); j ++) {
                if (rookMaze[i][j] == 0) {
                    rookMaze[i][j] = setRandomMazeNumber();
                }
            }
        }
    }

    // Checks the move that is about to be made is valid
    public boolean isValid(int y, int x) {
        if (y < 0 || y >= rookMaze.length || x < 0 || x >= rookMaze[y].length) {
            return false;
        } else {
            return true;
        }
    }

    // Generates a random number for the direction
    private int setRandomDirectionNumber() {
        return rand.nextInt(4);
    }

    // Generates a random number that is used to populate empty spaces in the maze
    // The +1 prevents a 0 from being generated
    private int setRandomMazeNumber() {
        return (rand.nextInt(4) + 1);
    }

    // Sets the goal on the rookMaze
    private int setGoal() {
        return 9;
    }

    // Get method for the rows variable
    public int getMazeRows() {
        return mazeRows;
    }

    // Get method for the columns variable
    public int getMazeColumns() {
        return mazeColumns;
    }
}
