
public class Tile {
	private boolean covered, marked, isMine;
	private  int adjacentMines; //the value will be the number of mines nearby.

	public Tile(boolean makeAMine)
	{
		
		covered = true;
		marked = false;
		isMine = makeAMine;
		
	}
	
	public void setAdjacentMines(int newValue)
	{
		adjacentMines = newValue;
	}
	
	public int getAdjacentMines()
	{
		return adjacentMines;
	}
	public boolean getIsMine()
	{
		return isMine;
	}
	public boolean getCovered()
	{
		return covered;
	}
	public boolean getMarked()
	{
		return marked;
	}
	
	public void uncover()
	{
		marked = false;
			
		covered = false;
	}
	
	public void mark()
	{
		marked = true;
	}
	public void unMark()
	{
		marked = false;
	}
	
	//returns the right half of a square containing the symbol for the tile
	//uncovered tiles are *, marked are ^, uncovered mines are X, uncovered empties are either their number or - if they have no adjacent mines
	public String toString()
	{
	
		if (marked)
		{
			return " ^ |";
		}
		else if (covered)
		{
			return " * |";
		}
		else
		{
			if(!isMine)
			{
				if (adjacentMines != 0)
				 return  " " + adjacentMines + " |";
				else
				return " - |";
			}
			else
			{
				return " X |";
			}
	
		}
		
	}
}
