
public class GameBoard {
	private int rows, columns, minesCount, marksMade;
	private Tile[][] gameTiles;
	private boolean playerHasLost = false;
	private final double MINE_RATIO = .25; //change to .25 for 1/4 the squares are mines, .15625 for real ratio
	//makes the gameboard by placing mines randomly
	//covers all tiles
	//this constructor is only used to make a dummy gameboard, which has the right amount of mines and looks right.
	//the actual game board is created after the first move using the next constructor
	public GameBoard(int newRows, int newColumns)
	{
		int minesPlaced = 0;
		rows = newRows;
		columns = newColumns;
		minesCount = (int)(newRows * newColumns * MINE_RATIO); //this will end up being the amount of mines in the board
		
		boolean[][] mines = new boolean[rows][columns]; //a dummy matrix, stores true at all the same positions mines should be stored
		for(int i = 0; i < rows; i++)					//in the real gameboard
		{
			for(int j = 0; j < columns; j++)
				mines[i][j] = false;
		}
		
		while (minesPlaced < minesCount)
		{
			int i = (int)(Math.random() * rows);
			int j = (int)(Math.random() * columns);
			if(!mines[i][j])
			{
				minesPlaced ++;
				mines[i][j] = true;
				
			}
		}
		

		
		
		gameTiles = new Tile[rows][columns];
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				
				gameTiles[i][j] = new Tile(mines[i][j]); //creates a mine at all true spaces in the array mines
				
			}
		}
		
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				gameTiles[i][j].setAdjacentMines(calculateAdjacentMines(i,j));
			}
		}
		
	}
	// a constructor to be used to create a new board around the first uncovered space.
	//takes the location of the first move, and will not place a mine there.
	//creates the actual gameboard used
	public GameBoard(int newRows, int newColumns, int firstMoveRow, int firstMoveColumn)
	{
		int minesPlaced = 0;
		rows = newRows;
		columns = newColumns;
		minesCount = (int)(newRows * newColumns * MINE_RATIO);
		
		boolean[][] mines = new boolean[rows][columns];
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
				mines[i][j] = false;
		}
		
		while (minesPlaced < minesCount)
		{
			int i = (int)(Math.random() * rows);
			int j = (int)(Math.random() * columns);
			if(!mines[i][j] && (i != firstMoveRow || j != firstMoveColumn ))
			{
				minesPlaced ++;
				mines[i][j] = true;
				
			}
		}

		gameTiles = new Tile[rows][columns];
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				
				gameTiles[i][j] = new Tile(mines[i][j]);
				
			}
		}
		
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				gameTiles[i][j].setAdjacentMines(calculateAdjacentMines(i,j));
			}
		}
		uncover(firstMoveRow, firstMoveColumn);
	}
	//calculates the number of adjacent mines to the tile at the given indeces and returns it as an int
	//lots of ifs so that it doesn't try to check indeces that are out of bounds
	private int calculateAdjacentMines(int row, int column)
	{
		int result = 0;
		
		if (row > 0  && column > 0)
		{
			if (gameTiles[row-1][column-1].getIsMine())//checks the top left
				result ++;
		}
		if (column > 0)
		{
			if (gameTiles[row][column-1].getIsMine())//checks the middle left
				result ++;
		}
		if (row < rows - 1 && column > 0)
		{
			if (gameTiles[row+1][column-1].getIsMine())//checks the bottom left
				result ++;
		}
		
	
		if (column < columns - 1 && row > 0)
		{
			if (gameTiles[row-1][column+1].getIsMine())//checks the top right
			result ++;
		}
		if (column < columns - 1)
		{
			if (gameTiles[row][column+1].getIsMine())//checks the middle right
			result ++;
		}
		
		if (column < columns - 1 && row < rows - 1)
		{
			if (gameTiles[row+1][column+1].getIsMine())//checks the bottom right
			result ++;
		}
			
		
		if (row > 0)
		{
			if (gameTiles[row-1][column].getIsMine()) //checks the top middle
			result ++;
		}
		if (row < rows - 1)
		{
			if (gameTiles[row+1][column].getIsMine())//checks the bottom middle
			result ++;
		}
	
	
		
		return result;
		
	}
	
	//uncovers all tiles
	private void loseGame()
	{
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < columns; j++)
			{
				gameTiles[i][j].uncover();
			}
		}
	}
	
	//checks if a tile at the given indeces is covered or not, and returns it as a boolean
	public boolean isCovered(int i, int j)
	{
		return gameTiles[i][j].getCovered();
	}
	//uncovers the tile at the given indeces
	//if the tile has no adjacent mines, it calls uncoverAroundEmpty() at the same space
	public void uncover(int i, int j)
	{
		if(gameTiles[i][j].getCovered())
		{
			
			if (gameTiles[i][j].getMarked())
				marksMade--;
			gameTiles[i][j].uncover();
			if (gameTiles[i][j].getIsMine())
			{
				playerHasLost = true;
				loseGame();
			}
		}
		
		if (gameTiles[i][j].getAdjacentMines() == 0)
		{
			uncoverAroundEmpty(i,j);
		}
	}
	//called by uncover if an empty tile is uncovered. recursively calls itself to uncover empty space.
	private void uncoverAroundEmpty(int row,int column)
	{
		boolean wasCovered;
		
		if (row > 0  && column > 0)
		{
			wasCovered = gameTiles[row-1][column-1].getCovered(); //if the space was already uncovered, this function won't call itself again
			gameTiles[row-1][column-1].uncover();				  //stops the program from getting caught in a loop, calling itself on adjacent tiles
			if (gameTiles[row-1][column-1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row-1,column-1);
		}
		if (column > 0)
		{
			wasCovered = gameTiles[row][column-1].getCovered();
			gameTiles[row][column-1].uncover();
			if (gameTiles[row][column-1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row,column-1);
		}
		if (row < rows - 1 && column > 0)
		{
			wasCovered = gameTiles[row+1][column-1].getCovered();
			gameTiles[row+1][column-1].uncover();
			if (gameTiles[row+1][column-1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row+1,column-1);
		}
		
	
		if (column < columns - 1 && row > 0)
		{
			wasCovered = gameTiles[row-1][column+1].getCovered();
			gameTiles[row-1][column+1].uncover();
			if (gameTiles[row-1][column+1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row-1,column+1);
		}
		if (column < columns - 1)
		{
			wasCovered = gameTiles[row][column+1].getCovered();
			gameTiles[row][column+1].uncover();
			if (gameTiles[row][column+1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row,column+1);
		}
		
		if (column < columns - 1 && row < rows - 1)
		{
			wasCovered = gameTiles[row+1][column+1].getCovered();
			gameTiles[row+1][column+1].uncover();
			if (gameTiles[row+1][column+1].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row+1,column+1);
		}
			
		
		if (row > 0)
		{
			wasCovered = gameTiles[row-1][column].getCovered();
			gameTiles[row-1][column].uncover();
			if (gameTiles[row-1][column].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row-1,column);
		}
		if (row < rows - 1)
		{
			wasCovered = gameTiles[row+1][column].getCovered();
			gameTiles[row+1][column].uncover();
			if (gameTiles[row+1][column].getAdjacentMines() == 0 && wasCovered)//checks the top left
				uncoverAroundEmpty(row+1,column);
		}
	}
	//marks or unmarks the tile at the given indeces, keeping track of
	//the number of marks on the board
	public void toggleMark(int i, int j)
	{
		if (gameTiles[i][j].getCovered())
		{
			if (gameTiles[i][j].getMarked())
			{
				marksMade --;
				gameTiles[i][j].unMark();
			}
			else
			{
				marksMade ++;
				gameTiles[i][j].mark();
			}
		}
		else
			System.out.println("That tile is already uncovered.");
	}
	
	//returns whether or not all mines are marked
	public boolean checkForWin()
	{
		if(marksMade == minesCount)
		{
			int markedMines = 0;
			for(int i = 0; i < rows; i++)
			{
				for(int j = 0; j < columns; j++)
				{
					if (gameTiles[i][j].getMarked() && gameTiles[i][j].getIsMine())
						markedMines++;
				}
			}
		
			return (markedMines == minesCount);
		}
		else
		return false;
	}
	
	public boolean getPlayerHasLost()
	{
		return playerHasLost;
	}
	public String toString()
	{
		
		String result = "    mines left: " + (minesCount - marksMade) + "\n";
		
		result += "  |";
		
		for(int i = 0; i < columns; i++)
		{
			if(i < 9)
			result += " " + (i + 1) + " |";
			else
			result += " " + (i + 1) + "|";
		}
		result += "\n  ";
		
		result += "\n";
		for(int i = 0; i < rows; i++)
		{
			
			result += (char)(i + 'A'); //row labels
			result += " |";
			for(int j = 0; j < columns; j++)
			{
				result += gameTiles[i][j];
			}
			
			
			
			result += "\n";
		}
		
		result += "Key: * = covered tile, - = no adjacent mines, X = mine, ^ = check\n";
		return result;
		
	}
	
	
	
	
}
