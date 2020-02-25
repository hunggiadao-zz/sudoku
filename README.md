# sudoku
This repository contains the Java source code for a program that solves any Sudoku puzzle.

Usage instructions:
- User is prompted to input a Sudoku puzzle for the program to solve
- Input numbers without spaces, blank squares represented by 0
- Enter 9 lines of numbers, 9 numbers from 0-9 each line
- Program solves for Sudoku

Program procedures (also contained in source code):
- First prompt user to input sudoku puzzle as lines of numbers, un-spaced, blank squares as 0
- cycleThroughAll(lines): initiate data in board[][], boardArray[][][], numOfPos[][], emptySquares, etc.
run while loop until emptySquares == 0
- solveSudoku1Square(row, col): try to solve square with row and column indicated. If appearOnce[] has
a number, that's the answer. If has 0 or >1 answer, pass because of uncertainty
- After a while-loop iteration, check didSth. If true, move on to next iteration. if false (didn't do
anything), force guessing
- solveSudokuStale(): if didSth == false, look for square with fewest possibilities and randomly guess
any number in boardArray[][][]
- If guessing, choose a random index in ranNum to be the answer. ranNum changes according to the current
square every time didSth == false
- changeValueOfTo(row, col, value): set the square at this row and column to the value parametre, after
that eliminates all possibilities with the same value in same block, row, or column and reduce their
- numOfPos[][] by 1, also emptySquares by 1
- If there's a conflict that a number appears twice in a block, row or column (StringIndexOutOfBoundsException
for ranNum because ranNum == ""), reset to the position before running any solveSudokuStale() and guess
again
- iterate until emptySquares == 0, then stop while loop and print out user input as well as solution in
a grid
