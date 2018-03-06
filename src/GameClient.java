import java.util.*;
//import java.io.InputStream;
import java.io.*;
//import sun.audio.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;
public class GameClient {
	private static int rows, columns;
	private static boolean firstMove = true;
	public static void main(String[] args)
	{
		
		Scanner input = new Scanner(System.in);
		getDimensions();
		GameBoard board = new GameBoard(rows, columns);
		
		while(firstMove)
		{
			System.out.print(board);
			board = processMove(getMove()); //getMove() gets a move, and it sends the board for processMove() to edit
		}
		long startTime = System.currentTimeMillis(); //for the timer
		while (!board.getPlayerHasLost() && !board.checkForWin())
		{
			System.out.print(board);
			System.out.println("Time so far: " + (int) ((System.currentTimeMillis() - startTime) *.001) + " seconds.");
			board = processMove(getMove(),board); //getMove() gets a move, and it sends the board for processMove() to edit
			
		}
		if (board.getPlayerHasLost())
		{
			playSound("Audio/MineTriggered.wav");
			System.out.print(board);
			System.out.println("You lose! This was the final board.");
			System.out.println("That game lasted " + (int) ((System.currentTimeMillis() - startTime) *.001) + " seconds.");
			System.out.println("Enter anything to exit.");
			input.nextLine();//allows the sound to finish playing
		}
		else
		{
			playSound("Audio/victory.wav"); 
			System.out.println(board);
			System.out.println("Congratulations! You win!");
			System.out.println("That game lasted " + (int) ((System.currentTimeMillis() - startTime) *.001) + " seconds.");
			System.out.println("Enter anything to exit.");
			input.nextLine(); //allows the sound to finish playing
		}
		
	}
	//prompts the user for dimensions of the game, and makes sure the dimensions are valid
	public static void getDimensions()
	{
		Scanner input = new Scanner(System.in);
		do
		{
			System.out.println("The game will have anywhere from 4 to 20 rows.");
			System.out.print("How many rows do you want? ");
			try
			{
				rows = input.nextInt();
				if (rows < 4 || rows > 20)
					System.out.println("Make sure you are entering a valid number.");
			}
			catch (Exception e)
			{
				System.out.println("You must enter a number.");
				input.nextLine();
			}
		}
		while (rows < 4 || rows > 20);
		do
		{
			System.out.println("The game will have anywhere from 4 to 20 columns.");
			System.out.print("How many columns do you want? ");
			
			
			try
			{
				columns = input.nextInt();
				if (columns < 4 || columns > 20)
					System.out.println("Make sure you are entering a valid number.");
			}
			catch (Exception e)
			{
				System.out.println("You must enter a number.");
				input.nextLine();
			}
		}
		while (columns < 4 || columns > 20);
	}
	
	//gets the move from the user, and makes sure it is a valid move. The only acceptable moves are
	//uncover and mark, and then a valid coordinate based on the dimensions of the array. For example,
	//"uncover A8" and "mark a7" are valid moves on a 9x9 board, but not on a 5x5
	public static String getMove()
	{
		Scanner input = new Scanner(System.in);
		String moveString = "";
		boolean isValid = true;
		System.out.print("Enter a move(example moves: \"uncover A8\", \"mark G15\"):");
		moveString = input.nextLine();
		do
		{
			int l = moveString.length();
			isValid = (l == 7 || l == 8 || l == 10 || l == 11);
			if (l == 7) //then it is a mark statement with 1 digit number
			{
				isValid = moveString.substring(0,4).toLowerCase().equals("mark") && isValid; //it must begin with "mark"
				isValid = (Character.toUpperCase(moveString.charAt(5)) >= 'A') && isValid;	 //the character must be 'A' or higher
				isValid = (Character.toUpperCase(moveString.charAt(5)) <= rows - 1 + 'A') && isValid;//the character must be less than the highest row letter
				isValid = (moveString.charAt(6) >= 1) && isValid;							 //the number given must be at least one
				isValid = ((int) moveString.charAt(6) - 48 <= columns) && isValid;			//and the number given must be less than the number of columns
			}																				//The same checks apply for the next 3 else if statements, the only differences being
																							//between a mark and an uncover command, and between one that requires a 1 digit number
																							//and one that requires a 2 digit number
			else if (l == 8) //then it is a mark statement with 2 digit number
			{
				isValid = moveString.substring(0,4).toLowerCase().equals("mark") && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(5)) >= 'A') && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(5)) <= rows - 1 + 'A') && isValid;
				
				isValid = ((int)(moveString.charAt(6)) - 48 == 1 || ((int)moveString.charAt(6) - 48 == 2 && columns == 20)) && isValid;
				isValid = (((int)moveString.charAt(6) - 48) * 10 + (int)moveString.charAt(7) - 48
							<= columns) && isValid; 
			}
			else if (l == 10) //then it is an uncover statement with 1 digit number
			{
				isValid = moveString.substring(0,7).toLowerCase().equals("uncover") && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(8)) >= 'A') && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(8)) <= rows - 1 + 'A') && isValid;
				isValid = (moveString.charAt(9) >= 1) && isValid;
				isValid = ((int)moveString.charAt(9) - 48 <= columns) && isValid;
			}
			else if (l == 11) //then it is an uncover statement with 2 digit number
			{
				isValid = moveString.substring(0,7).toLowerCase().equals("uncover") && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(8)) >= 'A') && isValid;
				isValid = (Character.toUpperCase(moveString.charAt(8)) <= rows - 1 + 'A') && isValid;
				isValid = (((int)moveString.charAt(9)) - 48 == 1 || ((int)moveString.charAt(9) - 48 == 2 && columns == 20)) && isValid;
				isValid = (((int)moveString.charAt(9) - 48) * 10 + ((int)moveString.charAt(10) - 48)
							<= columns) && isValid; 
			}
			
			
			if (!isValid)
			{
				System.out.print("That is an invalid command. try again:");
				moveString = input.nextLine();
			}
			
		}while (!isValid);
		return moveString;
	}
	
	//takes a valid move and processes it, and then performs the move
	//returns the edited board
	//all moves are one of 4 variations: "uncover a1, uncover a11, mark a1, mark a11", 
	//either a mark or an uncover and then with a one digit or 2 digit number. all also
	//have already been checked for the letters and numbers being on the board
	public static GameBoard processMove(String moveString, GameBoard board)
	{
		
		int row, column;
		moveString = moveString.toUpperCase();
		if (moveString.charAt(0) == 'U') //then it's an uncover
		{
			row = (int)moveString.charAt(8) - 65;
			column = Integer.parseInt(moveString.substring(9))-1;
			board.uncover(row, column);
			if (!board.getPlayerHasLost())
			playSound("Audio/uncover.wav");
			
		}
		else //then it's a mark
		{
			
			row = (int)moveString.charAt(5) - 65;
			column = Integer.parseInt(moveString.substring(6))-1;            
			if (board.isCovered(row, column))                                                                                            
			board.toggleMark(row,column);
			if (!board.checkForWin())
			playSound("Audio/mark.wav"); 
			
			
		}                                                                    
		return board;                                                                                                                       
	}
	
	//only runs if it is the first move. does nothing if a mark command is passed,
	//but if an uncover command is passed, it creates the board, uncovers the first move,
	//and returns the board.
	public static GameBoard processMove(String moveString)
	{
		int row, column;
		moveString = moveString.toUpperCase();
		if (moveString.charAt(0) == 'M')
		{
			System.out.println("Try uncovering for the first move.");
			return new GameBoard(rows, columns);
		}
		else
		{
			
			row = (int)moveString.charAt(8) - 65;
			column = Integer.parseInt(moveString.substring(9))-1;
			GameBoard board = new GameBoard(rows, columns, row, column);
			playSound("Audio/uncover.wav");
			firstMove = false;
			return board;
		}
	}
	//plays a sound file
	//only seems to work if the class files from the bin folder are run from CMD prompt, or if the whole project folder is in Eclipse
	public static void playSound(String fileName) {
		
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName).getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        //ex.printStackTrace();
	    }
	}
	
	

}
