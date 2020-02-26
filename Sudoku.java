import java.util.Scanner;

/**
 * Hung Dao
 * @author hunggiadao
 */

public class Sudoku {
	/* bigRow, bigCol refer to the relative column and row of a block, from 0-2*/
	/* board[][] is a 2D array used to store numbers to be displayed in a Sudoku board, 1st dimension is row, 2nd
	 * dimension column, stores 0-9*/
	/* currentBlock is number address for the block that a square is in, left to right, top to bottom, 1-9*/
	/* boardArray[][][] is a 3D array that stores possible answers to a square, 1st dimension is row, 2nd
	 * dimension column, 3rd dimension is all potential answers*/
	/* numOfPos[][] is a 2D array that keeps track of number of remaining possible answers for a square, used to find
	 * square with fewest possibilities*/
	/* emptySquares is a counter that keeps track of # of empty squares remaining, goal is to get emptySquares to 0*/
	/* didSth is a flag that checks if 1 iteration of the program "did something"*/
	/* doneWithSquare is a flag that confirms if a square has been solved and data is refreshed for other squares in
	 * the same block, row, and column. Ex: if answer to a square is 7, all other empty squares in the same block, row
	 * or column with a possibility of 7 have to be updated and numOfPos and emptySquares reduced by 1 every time*/
	/* appearOnce[] is is an array that stores possibilities of a square that only appear once in a block, row, or
	 * column. If there's one, that must be the answer*/
	/* ranNum stores numbers to be guessed if cannot solve, if ranNum == "", that means no possible guesses can
	 * be made, there's been an error*/
	/* lines[] is an array of strings that saves the user input to be parsed, 9 lines*/
	/* smallestEmpty is a tracker that saves the lowest # of empty squares of failed attempts, used to see how
	 * far the program gets to before a successful solution*/
	
	// declared outside main method to be used in other methods as well
	static int bigRow, bigCol = 0;
	static int[][] board = new int[9][9];
	static int currentBlock;
	static int[][][] boardArray = new int[9][9][9];
	static int[][] numOfPos = new int[9][9];
	static int emptySquares = 0;
	static boolean didSth = false;
	static boolean doneWithSquare = false;
	static int[] appearOnce = new int[9];
	static boolean firstTime = true;
	static String ranNum = "";
	static String[] lines = new String[9];
	static int smallestEmpty = 100;
	
	public static void main(String[] args) {
		/* First prompt user to input sudoku puzzle as lines of numbers, un-spaced, blank squares as 0*/
		/* cycleThroughAll(lines): initiate data in board[][], boardArray[][][], numOfPos[][], emptySquares, etc.*/
		/* run while loop until emptySquares == 0*/
		/* solveSudoku1Square(row, col): try to solve square with row and column indicated. If appearOnce[] has
		 * a number, that's the answer. If has 0 or >1 answer, pass because of uncertainty*/
		/* After a while-loop iteration, check didSth. If true, move on to next iteration. if false (didn't do
		 * anything), force guessing*/
		/* solveSudokuStale(): if didSth == false, look for square with fewest possibilities and randomly guess
		 * any number in boardArray[][][]*/
		/* If guessing, choose a random index in ranNum to be the answer. ranNum changes according to the current
		 * square every time didSth == false*/
		/* changeValueOfTo(row, col, value): set the square at this row and column to the value parametre, after
		 * that eliminates all possibilities with the same value in same block, row, or column and reduce their
		 * numOfPos[][] by 1, also emptySquares by 1*/
		/* If there's a conflict that a number appears twice in a block, row or column (StringIndexOutOfBoundsException
		 * for ranNum because ranNum == ""), reset to the position before running any solveSudokuStale() and guess
		 * again*/
		/* iterate until emptySquares == 0, then stop while loop and print out user input as well as solution in
		 * a grid*/
		
		// long gmt1 = System.currentTimeMillis();
		Scanner inputLine = new Scanner(System.in);
		
		// reset boardArray and numOfPos
		for (int row = 0; row < 9 /*boardArray.length*/; row++) {
			for (int col = 0; col < 9 /*boardArray[row].length*/; col++) {
				// fill boardArray[][][] with possibilities from 1 to 9 as if nothing has been solved
				resetArray(boardArray[row][col]);
				numOfPos[row][col] = 9;
			}
		}
		
		// ask for input
		System.out.println("Input 9x9 Sudoku grid line by line, no spaces, blank squares as 0:");
		lines[0] = inputLine.nextLine();
		lines[1] = inputLine.nextLine();
		lines[2] = inputLine.nextLine();
		lines[3] = inputLine.nextLine();
		lines[4] = inputLine.nextLine();
		lines[5] = inputLine.nextLine();
		lines[6] = inputLine.nextLine();
		lines[7] = inputLine.nextLine();
		lines[8] = inputLine.nextLine();
		
		// initiate Sudoku data
		cycleThroughAll(lines);
		
		// solve Sudoku
		// keep solving until there are no more empty squares
		while (emptySquares > 0) {
			didSth = false;
			
			// cycle through all empty squares and attempt to solve them logically
			for (int row = 0; row < 9 /*board.length*/; row++) {
				for (int col = 0; col < 9 /*board[row].length*/; col++) {
					/* if this method successfully calls changeValueOfTo(row, col, value), didSth will be set to true
					 * and emptySquares reduced by 1*/
					solveSudoku1Square(row, col);
				}
			}
			// done with 1 cycle
			
			// if did not do anything after the cycle
			if (!didSth && firstTime) {
				// renew lines and save to lines[], also sets firstTime to false forever
				firstTime = false;
				
				/* re-initiate the board based on this position. The reason I don't reset the whole thing back to
				 * the very beginning is to save the program a few steps that I know will always take place before
				 * having to guess anything. If the program only executes solveSudoku1Square() before guessing, it
				 * will go through everything to get to this stale mate, so I start from here instead*/
				for (int row = 0; row < 9 /*board.length*/; row++) {
					lines[row] = "";
					for (int col = 0; col < 9 /*board[row].length*/; col++) {
						// copy to lines[] the most current version of the Sudoku, not the original
						lines[row] += board[row][col];
						resetArray(boardArray[row][col]);
						numOfPos[row][col] = 9;
					}
				}
				emptySquares = 0;
				
				// re-initiate board
				cycleThroughAll(lines);
				
				// force guessing at least 1 square
				solveSudokuStale();
				// done with 1 cycle
				
				// indicate that program has guessed so that user knows this step is uncertain
				System.out.println("solveSudokuStale()");
			}
			
			/* If not the first time the program fails, simply go back to lines[] without having to
			 * renew lines[]*/
			else if (!didSth) {
				// same, force guessing at least 1 square
				solveSudokuStale();
				System.out.println("solveSudokuStale()");
			}
			
			/* In the end of every iteration, update smallestEmpty to keep track of how close it is to a successful
			 * solution*/
			if (emptySquares < smallestEmpty) {
				smallestEmpty = emptySquares;
			}
			
			// done with 1 iteration
		}
		// done with the entire board and exit while loop
		
		// print out original user input
		for (int row = 0; row < 9 /*lines.length()*/; row++) {
			System.out.println(lines[row]);
		}
		
		// print out final solution in neat grid
		for (int row = 0; row < 9 /*board.length*/; row++) {
			if (row == 0) {
				System.out.println("-------------------------");
			}
			for (int col = 0; col < 9 /*board[row].length*/; col++) {
				if (col == 0 || col == 3 || col == 6) {
					System.out.print("| " + board[row][col] + " ");
				} else {
					System.out.print(board[row][col] + " ");
				}
				if (col == 8) {
					System.out.print("|");
				}
			}
			System.out.println();
			if (row == 2 || row == 5 || row == 8) {
				System.out.println("-------------------------");
			}
		}
		
		/*
		long gmt2 = System.currentTimeMillis();
		// calculate
		long gmt = gmt2 - gmt1;
		long hour = (gmt % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
		long min = (gmt % (60 * 60 * 1000)) / (60 * 1000);
		long sec = (gmt % (60 * 1000)) / 1000;
		long mili = gmt % 1000;
		System.out.println("Time elapsed: " + hour + ":" + min + ":" + sec + "." + mili);
		*/
		
		inputLine.close();
	}
	
	/* This method refills all possibilities in an array to restart, as if nothing has been solved*/
	public static void resetArray(int[] array) {
		// set all possible numbers in array from 1 to 9
		for (int i = 0; i < array.length; i++) {
			array[i] = i + 1;
		}
	}
	
	/* This method returns the current block location of a square to see if it's in block 1 to 9. Blocks are
	 * ordered from 1 to 9 from left to right, top to bottom*/
	public static int returnBlockNum(int row, int col) {
		// set bigRow
		switch (row) {
			case 0:
			case 1:
			case 2:
				bigRow = 0; break;
			case 3:
			case 4:
			case 5:
				bigRow = 1; break;
			default:
				bigRow = 2;
		}
		
		// set bigCol
		switch (col) {
			case 0:
			case 1:
			case 2:
				bigCol = 0; break;
			case 3:
			case 4:
			case 5:
				bigCol = 1; break;
			default:
				bigCol = 2;
		}
		
		// return blockNum
		if (bigRow == 0 && bigCol == 0) {return 1;}
		else if (bigRow == 0 && bigCol == 1) {return 2;}
		else if (bigRow == 0 && bigCol == 2) {return 3;}
		else if (bigRow == 1 && bigCol == 0) {return 4;}
		else if (bigRow == 1 && bigCol == 1) {return 5;}
		else if (bigRow == 1 && bigCol == 2) {return 6;}
		else if (bigRow == 2 && bigCol == 0) {return 7;}
		else if (bigRow == 2 && bigCol == 1) {return 8;}
		else {return 9;}
	}
	
	/* This method changes value of an empty square (0) to a given value in the according row and column. After
	 * that, it eliminates any possibility of the same value in empty squares in the same block, row, and column.
	 * Finally, change didSth to true and decrement numOfPos[][] and emptySquares*/
	public static void changeValueOfTo(int row, int col, int value) {
		board[row][col] = value;
		
		// reduce numOfPos by block
		currentBlock = returnBlockNum(row, col);
		for (int smallRow = 0; smallRow < 3; smallRow++) {
			for (int smallCol = 0; smallCol < 3; smallCol++) {
				// if there's another empty square
				if (board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] == 0) {
					// delete valueth possibility if that empty square has same possible value
					if (boardArray[bigRow * 3 + smallRow][bigCol * 3 + smallCol][value - 1] != 0) {
						boardArray[bigRow * 3 + smallRow][bigCol * 3 + smallCol][value - 1] = 0;
						numOfPos[bigRow * 3 + smallRow][bigCol * 3 + smallCol]--;
					}
				}
			}
		}
		
		// reduce numOfPos by row
		for (int currentCol = 0; currentCol < 9 /*board[row].length*/; currentCol++) {
			// if there's another empty square
			if (board[row][currentCol] == 0) {
				// delete valueth possibility if that empty square has same possible value
				if (boardArray[row][currentCol][value - 1] != 0) {
					boardArray[row][currentCol][value - 1] = 0;
					numOfPos[row][currentCol]--;
				}
			}
		}
		
		// reduce numOfPos by column
		for (int currentRow = 0; currentRow < 9; currentRow++) {
			// if there's another empty square
			if (board[currentRow][col] == 0) {
				// delete valueth possibility if that empty square has same possible value
				if (boardArray[currentRow][col][value - 1] != 0) {
					boardArray[currentRow][col][value - 1] = 0;
					numOfPos[currentRow][col]--;
				}
			}
		}
		
		emptySquares--;
		doneWithSquare = true;
		didSth = true;
		// done with this square
	}
	
	/* This method initiates everything according to lines[]. Basically, it sets up board[][], boardArray[][][],
	 * numOfPos[][], emptySquares before doing any solving*/
	public static void cycleThroughAll(String[] lines) {
		// convert strings in lines[] to numbers and save to board
		for (int row = 0; row < 9 /*board.length*/; row++) {
			for (int col = 0; col < 9 /*board[row].length*/; col++) {
				board[row][col] = Integer.parseInt(lines[row].charAt(col) + "");
			}
		}
		
		// cycle through all squares to check for empty squares
		for (int row = 0; row < 9 /*board.length*/; row++) {
			for (int col = 0; col < 9 /*board[row].length*/; col++) {
				// if found an empty square
				if (board[row][col] == 0) {
					emptySquares++;
					
					/* set any possibility to 0 if there's already a filled square with that number by block.
					 * Any possibility of 0 is disregarded*/
					currentBlock = returnBlockNum(row, col);
					for (int smallRow = 0; smallRow < 3; smallRow++) {
						for (int smallCol = 0; smallCol < 3; smallCol++) {
							if (board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] != 0) {
								if (boardArray[row][col][board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] - 1] != 0) {
									boardArray[row][col][board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] - 1] = 0;
									numOfPos[row][col]--;
								}
							}
						}
					}
					
					/* set any possibility to 0 if there's already a filled square with that number by row.
					 * Any possibility of 0 is disregarded*/
					for (int currentCol = 0; currentCol < 9 /*board[row].length*/; currentCol++) {
						if (board[row][currentCol] != 0) {
							if (boardArray[row][col][board[row][currentCol] - 1] != 0) {
								boardArray[row][col][board[row][currentCol] - 1] = 0;
								numOfPos[row][col]--;
							}
						}
					}
					
					/* set any possibility to 0 if there's already a filled square with that number by column.
					 * Any possibility of 0 is disregarded*/
					for (int currentRow = 0; currentRow < 9; currentRow++) {
						if (board[currentRow][col] != 0) {
							if (boardArray[row][col][board[currentRow][col] - 1] != 0) {
								boardArray[row][col][board[currentRow][col] - 1] = 0;
								numOfPos[row][col]--;
							}
						}
					}
					
					// done with block, row, and column
				}
				// done with 1 square
			}
			// done cycling through all squares
		}
		
		// indicate # of empty squares before solving
		System.out.println("emptySquares " + emptySquares);
		
		// done cycleThroughAll(lines)
	}
	
	/* This method logically solves an assigned square. If a possibility only appears once in a block, row, or
	 * column, it must be the only solution to that square and execute changeValueOfTo(). It can fail to solve any
	 * square (fail to execute changeValueOfTo()) if there's no certainty, in which case didSth is still false*/
	public static void solveSudoku1Square(int row, int col) {
		doneWithSquare = false;
		
		// if the square is empty
		if (board[row][col] == 0) {
			// check if there is only 1 possibility, in which case that must be the answer
			if (numOfPos[row][col] == 1) {
				for (int value = 0; value < 9 /*boardArray[row][col].length*/; value++) {
					// find number that isn't 0 in boardArray[][][] (a valid possibility) and set it as value
					if (boardArray[row][col][value] != 0) {
						changeValueOfTo(row, col, value + 1);
						System.out.println("emptySquares " + emptySquares);
					}
				}
			}
			
			// try other methods if there are more than 1 possibility
			
			// find appearOnce by block
			if (!doneWithSquare) {
				resetArray(appearOnce);
				currentBlock = returnBlockNum(row, col);
				// find another empty square
				for (int smallRow = 0; smallRow < 3; smallRow++) {
					for (int smallCol = 0; smallCol < 3; smallCol++) {
						// if found another empty square and that square has the same possibility
						if (board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] == 0) {
							for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
								if (boardArray[row][col][ind] != 0) {
									/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
									 * numbers that only "appear once"*/
									if (boardArray[bigRow * 3 + smallRow][bigCol * 3 + smallCol][ind] != 0) {
										appearOnce[ind] = 0;
									}
								} else {appearOnce[ind] = 0;}
							}
						}
						// done finding another empty square
					}
				}
				// done with block
				// check if there's a number appearing once
				int onceNum = 0;
				int onceCount = 0;
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					// if different from 0
					if (appearOnce[ind] != 0) {
						onceNum = appearOnce[ind];
						onceCount++;
					}
				}
				
				/* if appearOnce[] has only 1 non-zero, that must be the answer. If more than 1, there are more
				 * than 1 number that appear once, uncertain*/
				if (onceCount == 1) {
					changeValueOfTo(row, col, onceNum);
					System.out.println("emptySquares " + emptySquares);
				}
			}
			
			// find appearOnce by row
			if (!doneWithSquare) {
				resetArray(appearOnce);
				for (int currentCol = 0; currentCol < 9 /*board[row].length*/; currentCol++) {
					// if found another empty square and that square has the same possibility
					if (board[row][currentCol] == 0) {
						for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
							if (boardArray[row][col][ind] != 0) {
								/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
								 * numbers that only "appear once"*/
								if (boardArray[row][currentCol][ind] != 0) {
									appearOnce[ind] = 0;
								}
							} else {appearOnce[ind] = 0;}
						}
					}
					// done with 1 empty square
				}
				// check if there's a number appearing once
				int onceNum = 0;
				int onceCount = 0;
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					// if different from 0
					if (appearOnce[ind] != 0) {
						onceNum = appearOnce[ind];
						onceCount++;
					}
				}
				
				/* if appearOnce[] has only 1 non-zero, that must be the answer. If more than 1, there are more
				 * than 1 number that appear once, uncertain*/
				if (onceCount == 1) {
					changeValueOfTo(row, col, onceNum);
					System.out.println("emptySquares " + emptySquares);
				}
			}
			
			// find appearOnce by column
			if (!doneWithSquare) {
				resetArray(appearOnce);
				for (int currentRow = 0; currentRow < 9; currentRow++) {
					// if found another empty square and that square has the same possibility
					if (board[currentRow][col] == 0) {
						for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
							if (boardArray[row][col][ind] != 0) {
								/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
								 * numbers that only "appear once"*/
								if (boardArray[currentRow][col][ind] != 0) {
									appearOnce[ind] = 0;
								}
							} else {appearOnce[ind] = 0;}
						}
					}
					// done with 1 empty square
				}
				// check if there's a number appearing once
				int onceNum = 0;
				int onceCount = 0;
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					// if different from 0
					if (appearOnce[ind] != 0) {
						onceNum = appearOnce[ind];
						onceCount++;
					}
				}
				
				/* if appearOnce[] has only 1 non-zero, that must be the answer. If more than 1, there are more
				 * than 1 number that appear once, uncertain*/
				if (onceCount == 1) {
					changeValueOfTo(row, col, onceNum);
					System.out.println("emptySquares " + emptySquares);
				}
			}
		}
		// done with this empty square
	}
	
	/* This method forces a guess on the square with fewest possibilities if no squares have been solved
	 * after 1 iteration. After this method is executed, at least 1 square must have been filled, no matter
	 * right or wrong. This is uncertain and could lead to errors, that's why it tests to see if ranNum == ""
	 * (StringIndexOutOfBoundsException for ranNum) in the end. If there's an error, it resets the board
	 * to position before any guesses and starts guessing again*/
	public static void solveSudokuStale() {
		doneWithSquare = false;
		int minNumOfPos = 10;
		int minRow = 0, minCol = 0; // row and col of interested square with fewest possibilities
		
		/* find location of square with fewest possibilities to maximise chance of guessing right in a
		 * shorter time*/
		for (int row = 0; row < 9 /*board.length*/; row++) {
			for (int col = 0; col < 9 /*board[row].length*/; col++) {
				// if found an empty square
				if (board[row][col] == 0) {
					if (numOfPos[row][col] < minNumOfPos) {
						minNumOfPos = numOfPos[row][col];
						minRow = row;
						minCol = col;
					}
				}
			}
		}
		
		// find appearOnce by block
		if (!doneWithSquare) {
			resetArray(appearOnce);
			currentBlock = returnBlockNum(minRow, minCol);
			// find another empty square
			for (int smallRow = 0; smallRow < 3; smallRow++) {
				for (int smallCol = 0; smallCol < 3; smallCol++) {
					// if found another empty square and that square has the same possibility
					if (board[bigRow * 3 + smallRow][bigCol * 3 + smallCol] == 0) {
						for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
							if (boardArray[minRow][minCol][ind] != 0) {
								/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
								 * numbers that only "appear once"*/
								if (boardArray[bigRow * 3 + smallRow][bigCol * 3 + smallCol][ind] != 0) {
									appearOnce[ind] = 0;
								}
							} else {appearOnce[ind] = 0;}
						}
					}
					// done finding another empty square
				}
			}
			// done with block
			// check if there's a number appearing once
			int onceNum = 0;
			int onceCount = 0;
			for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
				// if different from 0
				if (appearOnce[ind] != 0) {
					onceNum = appearOnce[ind];
					onceCount++;
				}
			}
			
			/* if appearOnce[] has >1 non-zeros, uncertain, guess any of them*/
			if (onceCount > 1) {
				ranNum = "";
				// find all appear once numbers and save to ranNum
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					if (appearOnce[ind] != 0) {
						ranNum += appearOnce[ind];
					}
				}
				// change value to random possibility
				changeValueOfTo(minRow, minCol, Integer.parseInt(ranNum.charAt((int)Math.round(Math.random() * (ranNum.length() - 1))) + ""));
				System.out.println("emptySquares " + emptySquares);
			}
		}
		
		// find appearOnce by row
		if (!doneWithSquare) {
			resetArray(appearOnce);
			for (int currentCol = 0; currentCol < 9 /*board[row].length*/; currentCol++) {
				// if found another empty square and that square has the same possibility
				if (board[minRow][currentCol] == 0) {
					for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
						if (boardArray[minRow][minCol][ind] != 0) {
							/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
							 * numbers that only "appear once"*/
							if (boardArray[minRow][currentCol][ind] != 0) {
								appearOnce[ind] = 0;
							}
						} else {appearOnce[ind] = 0;}
					}
				}
				// done with 1 empty square
			}
			// check if there's a number appearing once
			int onceNum = 0;
			int onceCount = 0;
			for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
				// if different from 0
				if (appearOnce[ind] != 0) {
					onceNum = appearOnce[ind];
					onceCount++;
				}
			}
			
			/* if appearOnce[] has >1 non-zeros, uncertain, guess any of them*/
			if (onceCount > 1) {
				ranNum = "";
				// find all appear once numbers and save to ranNum
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					if (appearOnce[ind] != 0) {
						ranNum += appearOnce[ind];
					}
				}
				// change value to random possibility
				changeValueOfTo(minRow, minCol, Integer.parseInt(ranNum.charAt((int)Math.round(Math.random() * (ranNum.length() - 1))) + ""));
				System.out.println("emptySquares " + emptySquares);
			}
		}
		
		// find appearOnce by column
		if (!doneWithSquare) {
			resetArray(appearOnce);
			for (int currentRow = 0; currentRow < 9; currentRow++) {
				// if found another empty square and that square has the same possibility
				if (board[currentRow][minCol] == 0) {
					for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
						if (boardArray[minRow][minCol][ind] != 0) {
							/* set to 0 if not 0 already. So in the end, non-zeros in appearOnce are
							 * numbers that only "appear once"*/
							if (boardArray[currentRow][minCol][ind] != 0) {
								appearOnce[ind] = 0;
							}
						} else {appearOnce[ind] = 0;}
					}
				}
				// done with 1 empty square
			}
			// check if there's a number appearing once
			int onceNum = 0;
			int onceCount = 0;
			for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
				// if different from 0
				if (appearOnce[ind] != 0) {
					onceNum = appearOnce[ind];
					onceCount++;
				}
			}
			
			/* if appearOnce[] has >1 non-zeros, uncertain, guess any of them*/
			if (onceCount > 1) {
				ranNum = "";
				// find all appear once numbers and save to ranNum
				for (int ind = 0; ind < 9 /*appearOnce.length*/; ind++) {
					if (appearOnce[ind] != 0) {
						ranNum += appearOnce[ind];
					}
				}
				// change value to random possibility
				changeValueOfTo(minRow, minCol, Integer.parseInt(ranNum.charAt((int)Math.round(Math.random() * (ranNum.length() - 1))) + ""));
				System.out.println("emptySquares " + emptySquares);
			}
		}
		
		/* If still not doneWithSquare even after doing by block, row, and column, ranNum must have been "".
		 * In which case, test for StringIndexOutOfBoundsException in ranNum. If it throws an exception, then
		 * reset to position before any guesses and starts guessing again (done when there's an error)*/
		if (!doneWithSquare) {
			// if onceCount = 0
			ranNum = "";
			// find all possibilities and save to ranNum
			for (int ind = 0; ind < 9 /*boardArray[row][col].length*/; ind++) {
				if (boardArray[minRow][minCol][ind] != 0) {
					ranNum += boardArray[minRow][minCol][ind];
				}
			}
			
			// change value to random possibility
			try {
				changeValueOfTo(minRow, minCol, Integer.parseInt(ranNum.charAt((int)Math.round(Math.random() * (ranNum.length() - 1))) + ""));
				System.out.println("emptySquares " + emptySquares);
			} catch (StringIndexOutOfBoundsException sioobe) {
				// if out of bound, reset back
				for (int thisrow = 0; thisrow < 9 /*boardArray.length*/; thisrow++) {
					for (int thiscol = 0; thiscol < 9 /*boardArray[thisrow].length*/; thiscol++) {
						resetArray(boardArray[thisrow][thiscol]);
						numOfPos[thisrow][thiscol] = 9;
					}
				}
				emptySquares = 0;
				
				/* print last failed solution before doing again so user knows that's the point the program
				 * cannot guess any more*/
				for (int thisrow = 0; thisrow < 9 /*board.length*/; thisrow++) {
					for (int thiscol = 0; thiscol < 9 /*board[thisrow].length*/; thiscol++) {
						System.out.print(" " + board[thisrow][thiscol]);
					}
					System.out.println();
				}
				System.out.println("-----------------------------------");
				System.out.println("smallestEmpty " + smallestEmpty + "\n");
				
				// reset back to before any guesses
				cycleThroughAll(lines);
			}
		}
		// done with this empty square
	}
}

