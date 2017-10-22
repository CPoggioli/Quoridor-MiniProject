//package lookandfeel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
//import javax.swing.plaf.metal;

/**
*121 Mini Project - Quoridor
*
*Java game designed to funciton similarly to the game Quoridor
*Main aspects of the program
*Moving Player Pieces to opposite side of the board
*Placing 2 part wall segmants to block the way
*Code to prevent players from clicking spaces that are blocked by walls
*preventing players from clciking on spaces occcupied by other players
*Keeping track of walls placed and limiting walls placed
*Having a scoreboard that updates when players win the game
*Allowing for undo, exit, and new game
*Reading and writing into files to allow players to save or open game
*
*Section - 01
*Course Number - ISTE 121
*Instructor - Michael Floeser
*@author Catherine Poggioli
*@version 1.0
*/

public class Quoridor extends JFrame{

   //File Used to Store and Call Game
   File gridFile = new File("Quoridor_Grid.bin");
   File formatFile = new File("Quoridor_Format_Info.bin");
   
   //stores the player names
   String pName1 = "Player 1";
   String pName2 = "Player 2";
   
   //Images for tokens
   ImageIcon p1Token = new ImageIcon("GreenStar.png");
   ImageIcon p2Token = new ImageIcon("SpadeToken.png");
   
   //Whose turn it is
   String turn = pName1;
   
   //Menu Item initialized
   JMenuItem   mExit;    
   JMenuItem   mNew;     
   JMenuItem   mUndo;
   JMenuItem   mPlace;    
   JMenuItem   mRules;   
   JMenuItem   mCredits;
   JMenuItem   mSave;
   JMenuItem   mOpen; 
   
   //Label for ScoreBoard 
   JLabel jlScore;
   
   //Record wins if players are playing multiple games
   int p1Wins = 0;
   int p2Wins = 0;
   
   //passing for if walls or players spots are enabled or disabled
   boolean clickable = true;
   
   //Labels of walls
   JLabel wallAmount1;
   JLabel wallAmount2;
   
   //# of walls allowed to place / wall counter
   int wallNum1 = 7;
   int wallNum2 = 7;
   
   //used for resetting p1 and p2 positions
   final int p1RowStart = 0;
   final int p1ColStart = 8;
   final int p2RowStart = 14;
   final int p2ColStart = 6;   
   
   //Changing p1 and p2 positions
   int p1Row = 0;
   int p1Col = 8;
   
   int p2Row = 14;
   int p2Col = 6;
   
   //location of button clicked
   int x;
   int y;
   
   //Allow for two wall placements   
   int count = 0;
   
   //used to undo wall placement
   int unconfRow;
   int unconfCol;
   
   //grid that is used to determine if wall or button space is occupied
   int[][] gridArray = new int[][]
   {
      { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }
                                                      };
   
   //Array of buttons that are used for the main board
   JButton[][] buttonArray = new JButton[15][15];
   
   //Wall buttons
   JButton horzWall = new JButton();
   JButton vertWall = new JButton();
   //Player Space Button
   JButton pSpace = new JButton();
   //Space holder or format button
   JButton unUsed = new JButton();
   
   /**
   *Main JFrame Layout
   *Adding panels from seperate classes to GUI
   *setting up title, packing, and making visible
   */
   public Quoridor(){
      
      setLayout(new BorderLayout(0,0));
      //Make JMenuBar on JFrame
      MenuGui mBar = new MenuGui();
      setJMenuBar(mBar);
      
      //SCORE BOARD panel
      add(new ScoreBoard(), BorderLayout.NORTH);
      
      //MAIN BOARD Panel
      add(new ButtonDisplay(), BorderLayout.CENTER);
      
      //Create Wall Info Panel
      add(new WallTracker(), BorderLayout.SOUTH);
      
      //Set up Title
      setTitle("Quoridor Mini Project"); 
      
      pack();
      setLocationRelativeTo(null);
      setDefaultCloseOperation( EXIT_ON_CLOSE );
      setVisible(true);   
   }
   /**
   *Initial Creation of buttons
   *Uses loops and if statements to fill an Array with four different kinds of buttons
   *Adds color, function, size, and action command to each
   *Uses panels to organize buttons into desired layout
   *Is only called with inital running of the game
   */
   class ButtonDisplay extends JPanel implements ActionListener{
   
      //Array list so there can be flowLayouts of different sized buttons
      ArrayList<JPanel> jpList = new ArrayList<JPanel>();
   
      ButtonDisplay(){
         
         setLayout(new GridLayout(0,1,0,0));    
         
         //Generate Four Different types of Buttons
         //For loop and if statements used to 
         //create two alternating rows of alternating buttons
         for(int i = 0; i < 15; i++){
            jpList.add(new JPanel(new FlowLayout(FlowLayout.CENTER, 15,15)));      
            if(i % 2 == 0){
               for(int j = 0; j < 15; j++){
                  //Customize Button Information Above
                  if(j % 2 == 0){ //Player Space Button Creation
                     buttonArray[i][j] = new JButton();
                     buttonArray[i][j].setBackground(Color.BLUE);
                     buttonArray[i][j].setPreferredSize(new Dimension(40, 40));
                     buttonArray[i][j].setActionCommand(i + "-" + j);
                     buttonArray[i][j].addActionListener(this);       
                  }
                  else{ //Wall segmant Creation
                     buttonArray[i][j] = new JButton();
                     buttonArray[i][j].setBackground(Color.WHITE);
                     buttonArray[i][j].setPreferredSize(new Dimension(20, 40));
                     buttonArray[i][j].setActionCommand(i + "-" + j);
                     buttonArray[i][j].addActionListener(this);
                     
                  }
                  jpList.get(i).add(buttonArray[i][j]);
                  buttonArray[i][j].setEnabled(false);
               }
               
            }
            else{ 
               for(int j = 0; j < 15; j++){
                  if(j % 2 == 0){ //Wall segmant button creation
                     buttonArray[i][j] = new JButton();
                     buttonArray[i][j].setBackground(Color.WHITE);
                     buttonArray[i][j].setPreferredSize(new Dimension(40, 20));
                     buttonArray[i][j].setActionCommand(i + "-" + j); 
                     buttonArray[i][j].addActionListener(this);
           
                  }
                  else{ //unused or filler button
                     buttonArray[i][j] = new JButton();
                     buttonArray[i][j].setBackground(Color.WHITE);
                     buttonArray[i][j].setPreferredSize(new Dimension(20, 20));
                     
                  }
                  jpList.get(i).add(buttonArray[i][j]);
                  buttonArray[i][j].setEnabled(false);
               }
            }
            add(jpList.get(i));
         }
         
      }
      public void	actionPerformed(ActionEvent ae){
      
         //Get location of button clicked
         String command = ae.getActionCommand();
         
         String[] split = command.split("-");
         x = Integer.parseInt(split[0]);
         y = Integer.parseInt(split[1]);
                 
         clickable = false;
         
         //Have the Token change to new location clicked if PLAYER SPACE CLICKED
         if(x % 2 == 0 && y % 2 == 0){
            if(turn == pName1){
               new MoveableSpace(p1Row,p1Col,clickable); // disable player spaces
               buttonArray[x][y].setIcon(p1Token); //Change Token location
               buttonArray[p1Row][p1Col].setIcon(null); //Change Visual
               gridArray[x][y] = 1; //Show Location as full
               gridArray[p1Row][p1Col] = 0;
               p1Row = x;
               p1Col = y;
               
               //CHECK FOR WIN CONDITION
               if(p1Row == 14){
                  JOptionPane.showMessageDialog( null, "CONGRATS \n" + turn + " has reached the opposite side and wins.");
                  turn = "none";
                  //ASK to play again
                  new PlayAgain();
                  //STORE HIGH SCORES FUNCTION HERE
                  ++p1Wins;
                  jlScore.setText(pName1 + " : " + p1Wins + "   " + pName2 + " : " + p2Wins);
                  jlScore.repaint();
                  
               }
               else{
                  //Change Turns
                  turn = pName2;
                  new PlayGame();
               }
            }
            else{//PLAYER SPACES
               new MoveableSpace(p2Row,p2Col,clickable); // disable player spaces
               buttonArray[x][y].setIcon(p2Token); //Change Token location
               buttonArray[p2Row][p2Col].setIcon(null); //Change Visual
               gridArray[x][y] = 1; //Show Location as full
               gridArray[p2Row][p2Col] = 0;  // show previous location as empty
               p2Row = x;
               p2Col = y;  
               
               //CHECK FOR WIN CONDITION
               if(p2Row == 0){
                  JOptionPane.showMessageDialog( null, "CONGRATS \n" + turn + " has reached the opposite side and wins.");
                  turn = "none";
                  //ASK to play again
                  new PlayAgain();
                  //STORE HIGH SCORES FUNCTION HERE
                  ++p2Wins;
                  jlScore.setText(pName1 + " : " + p1Wins + "   " + pName2 + " : " + p2Wins);
                  jlScore.repaint();
               }
               else{
                  //Change Turns
                  turn = pName1;
                  new PlayGame();
               }
            }
            //Allow player to alternatively use JMenu to select next move
            mPlace.setEnabled(true);
         }
         else{//WALL
               if(gridArray[x][y] == 0){
               buttonArray[x][y].setBackground(Color.BLACK);
               
               //clickable currently false except one chosen... button still enabled black but no listener 
               new EnableWallPlacement(clickable);
               gridArray[x][y] = 1;
               buttonArray[x][y].revalidate();
               buttonArray[x][y].repaint();
               //used to insure two segmants of the wall are placed
               ++count;
               //stores incase of undo
               unconfRow = x;
               unconfCol = y;
               mPlace.setEnabled(false);
                  
               if(count == 1){
                  clickable = true;
                  //Allow for second wall placement
                  mUndo.setEnabled(true);
                  new EnableCloseWall(clickable);
                  
                  //Once seg is clicked... stop mPlace from reg as true
                  //Change Player Turns
                  if(turn == pName1){
                     --wallNum1;
                     wallAmount1.setText(pName1 + "'s Walls: " + wallNum1);
                     wallAmount1.repaint();
                  }
                  else{
                     --wallNum2;
                     wallAmount2.setText(pName2 + "'s Walls: " + wallNum2);
                     wallAmount2.repaint();
                  }
               }
               if(count == 2){
                  count = 3; //resets wall seg count 
                  mUndo.setEnabled(false);
                  //if and else written sperately to insure change of turns only occurs after chance of undo passes
                  if(turn == pName1){
                     turn = pName2;
                  }
                  else{
                     turn = pName1;
                  }
                  //Allow Place Piece Menu to get clicked
                  mPlace.setEnabled(true);
               }
               if(count == 3){
                  count = 0;
                  new PlayGame();
               }
            }             
         }
      }
   }
   
   /**
   *Used to Create Inital ScoreBoard
   *Adds JLabels to panel that is called in the inital running of the program
   */
   class ScoreBoard extends JPanel{
   
      ScoreBoard(){
         setLayout(new GridLayout(0,1));
         
         JLabel scoreTitle = new JLabel("Score Board", JLabel.CENTER);
         jlScore = new JLabel(pName1 + " : " + p1Wins + "   " + pName2 + " : " + p2Wins, JLabel.CENTER);
         scoreTitle.setFont(new Font("Arial", Font.BOLD, 18));
         jlScore.setFont(new Font("Arial", Font.PLAIN, 18));
         add(scoreTitle);
         add(jlScore);
            
      }
   }
   /**
   *Creates Initial Wall Tally on lower right side
   *Used to display the amount of walls placed by each player
   *Formatted to agree with othe components of the program
   */    
   class WallTracker extends JPanel{
   
      WallTracker(){
      
         setLayout(new GridLayout(0,1));
         
         JLabel diagramExp  = new JLabel("# of Walls Available", JLabel.RIGHT);
         wallAmount1 = new JLabel(pName1 + "'s Walls: " + wallNum1, JLabel.RIGHT); 
         wallAmount2 = new JLabel(pName2 + "'s Walls: " + wallNum2, JLabel.RIGHT);
         diagramExp.setFont(new Font("Arial", Font.BOLD, 18));
         wallAmount1.setFont(new Font("Arial", Font.PLAIN, 18));
         wallAmount2.setFont(new Font("Arial", Font.PLAIN, 18));
         add(diagramExp);
         add(wallAmount1);
         add(wallAmount2);
         setBorder( new EmptyBorder( 10, 30, 15, 15 ) );
      }
   }     
   
   /**
   *Used to create the JMenuBar
   *Contains 3 main menus, file, piecemove, about
   *File provides a way to exit, start a new game, open a old game, and save the game
   *PieceMove provides a way to reOpen turn selection and undo a move
   *About provides a way to see the rules and credits of the game
   *Uses action listeners to call multiple different functions required for each menu option
   */
   class MenuGui extends JMenuBar{
      //Creates the Menu Choices and Menu Items

      MenuGui(){
      
         //Wrriten Rules for Quoridor
         String ruleString = "Each player's pawn start in the center of the board. \n" +
                      "The goal of the game is to reach the other side. \n" +
                      "A player may choose to either move their pawn or place a wall on their turn. \n" +
                      "Player pieces may move up, down, left, or right. Two pieces may not occupy the same spot. \n" +
                      "Walls are placed horizontally or vertically and take up two connecting spots. \n" +
                      "Walls can't be moved and can never completely wall in the opponent. There must always be a path. \n";
      
         //Create Menu Options
         JMenu      mFile     = new JMenu("File");
         JMenu      mPieceMove= new JMenu("Piece Movements");
         JMenu      mAbout    = new JMenu("About");
          
         //Create Menu Items
         mExit    = new JMenuItem("Exit");
         mNew     = new JMenuItem("New Game");
         mUndo    = new JMenuItem("Undo Wall Placement");
         mPlace   = new JMenuItem("Place a wall or Piece");
         mSave    = new JMenuItem("Save Game");
         mOpen    = new JMenuItem("Open Old Game");
         
         //Menu Starts as false as default
         mPlace.setEnabled(false);
         mRules   = new JMenuItem("Rules");
         mCredits = new JMenuItem("Credits");
         //Menu Starts as false as default
         mUndo.setEnabled(false);
         
         //Change Font for Menu
         mFile.setFont(new Font("Arial", Font.BOLD, 18));
         mAbout.setFont(new Font("Arial", Font.BOLD, 18));
         mPieceMove.setFont(new Font("Arial", Font.BOLD, 18));
         
         mExit.setFont(new Font("Arial", Font.PLAIN, 18));
         mNew.setFont(new Font("Arial", Font.PLAIN, 18));    
         mUndo.setFont(new Font("Arial", Font.PLAIN, 18));
         mPlace.setFont(new Font("Arial", Font.PLAIN, 18));   
         mRules.setFont(new Font("Arial", Font.PLAIN, 18));  
         mCredits.setFont(new Font("Arial", Font.PLAIN, 18));
         mSave.setFont(new Font("Arial", Font.PLAIN, 18));  
         mOpen.setFont(new Font("Arial", Font.PLAIN, 18));
          
         //Add Menu Item to Menu and Menu to bar
         add(mFile);
         add(mAbout);
         add(mPieceMove);
          
         mFile.add(mExit);
         mFile.add(mNew);
         mFile.add(mSave);
         mFile.add(mOpen);

         
         mPieceMove.add(mUndo);
         mPieceMove.add(mPlace);

         mAbout.add(mRules); 
         mAbout.add(mCredits);
         
         mCredits.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mCredits, "Quoridor Mini Project \n by: Catherine Poggioli & Zain Alam");
            }
         });
         
         mRules.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mRules, ruleString);
            }
         });
         
         mExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mExit, "Thank-you for playing.");
               System.exit(0);
            }
         });
         
         mNew.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mNew, "New Game");
               
               //Enables alternative menu option to choose placing wall or piece
               new NewPlayers();
               new ResetScoreBoard();
               new Reset();
               new PlayGame();
               mPlace.setEnabled(true);
            }
         });
         mUndo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mCredits, "Undo Wall Segmant Placement");
               --count; //sets the count of segmants place back
               gridArray[unconfRow][unconfCol] = 0; //Changes array to remove wall segmant
               buttonArray[unconfRow][unconfCol].setBackground(Color.WHITE); // visually make the button appear unclicked
               buttonArray[unconfRow][unconfCol].repaint(); // repaints the button to white
               new EnableWallPlacement(true);              
               mUndo.setEnabled(false);
            }
         });
         mPlace.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               new PlayGame(); //incase option for moves is closed before option is chosen... alternative way to get prompted
            }
        });
        
        mOpen.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mOpen, "Open");
               new ReadGame();
            }
        });
        mSave.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
               JOptionPane.showMessageDialog(mNew, "Save");
               new WriteGame();
            }
        });
        

      }
   }
   
   /**
   *Used to Save the current Game
   *Takes all necessary information for playing
   *Writes GridArray to a file
   *Writes addition info needed to keep record of the game to a file
   *@exception IOException to check if IO Errors when writing info
   *@exception Exception to catch alternative issues
   *@see IOException
   *@see Exception
   */
   class WriteGame{
      WriteGame(){
         try{
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(gridFile)));
            DataOutputStream dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(formatFile)));
            
            //copy current gridArray into the gridFile to store data
            for(int i = 0; i < 15; i++){
               for(int j = 0; j < 15; j++){
                  dos.writeInt(gridArray[i][j]);
               }
            }
            //reading format related info into file to store
            dos2.writeUTF(turn);
            dos2.writeUTF(pName1);
            dos2.writeUTF(pName2);
            dos2.writeInt(p1Wins);
            dos2.writeInt(p2Wins);
            dos2.writeInt(p1Row);
            dos2.writeInt(p1Col);
            dos2.writeInt(p2Row);
            dos2.writeInt(p2Col);
            dos2.writeInt(count);
            dos2.writeInt(wallNum1);
            dos2.writeInt(wallNum2);
            dos2.writeInt(x);
            dos2.writeInt(y);
            
            
            //closing open files & flushing
            dos.flush();
            dos.close();
            dos2.close(); 
         }
         catch(IOException e){e.printStackTrace();}
         catch(Exception e){e.printStackTrace();}
      }
   }
   
   /**
   *Used to read in the previous Game
   *Resets the current game's display and information
   *Disables buttons to insure that proper turn and piece placement is maintained
   *Reads in integers from file to gridArray
   *Uses loop to change the color of walls if gridArray shows a wall is located there
   *Reads other information needed for game play into their assigned variables
   *Updates GUI with new Info
   *Displays tokens at proper locations
   *@exception FileNotFoundException creates a dialog pop up that lets the user know there isn't a saved file
   *@exception IOException to check if IO Errors when reading info
   *@exception Exception to catch alternative issues
   *@see FileNotFoundException
   *@see IOException
   *@see Exception
   */
   class ReadGame{
      ReadGame(){
         try{
            //clear previous data and conflicting colors
            new Reset();
            
            //Makes sure walls and Players spaces are unclickable
            new EnableWallPlacement(false); //disable walls
            new MoveableSpace(p1Row,p1Col,false); // disable player spaces
            new MoveableSpace(p2Row,p2Col,false); // disable player spaces
            
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(gridFile)));
            DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(formatFile)));
            
            //copy gridFile into gridArray
            for(int i = 0; i < 15; i++){
               for(int j = 0; j < 15; j++){
                  gridArray[i][j] = dis.readInt();
                  if(i % 2 == 0){
                     if(j % 2 == 1 && gridArray[i][j] == 1){
                        buttonArray[i][j].setBackground(Color.BLACK);
                        buttonArray[i][j].repaint();
                     }                    
                  }
                  else{
                     if(j % 2 == 0 && gridArray[i][j] == 1){
                        buttonArray[i][j].setBackground(Color.BLACK);
                        buttonArray[i][j].repaint();
                     }
                  }
               }
            }
            //reading format related info into variables
            turn  =  dis2.readUTF();
            pName1=  dis2.readUTF();
            pName2=  dis2.readUTF();
            p1Wins=  dis2.readInt();
            p2Wins=  dis2.readInt();
            p1Row =  dis2.readInt();
            p1Col =  dis2.readInt();
            p2Row =  dis2.readInt();
            p2Col =  dis2.readInt();
            count =  dis2.readInt();
            wallNum1 = dis2.readInt();
            wallNum2 = dis2.readInt();
            x = dis2.readInt();
            y = dis2.readInt();
            
            jlScore.setText(pName1 + " : " + p1Wins + "   " + pName2 + " : " + p2Wins);
            jlScore.repaint();
            
                
            
            buttonArray[p1RowStart][p1ColStart].setIcon(null); //Change Token location
            buttonArray[p1Row][p1Col].setIcon(p1Token); //Change Visual
            
            wallAmount1.setText(pName1 + "'s Walls: " + wallNum1);
            wallAmount1.repaint();
            wallAmount2.setText(pName2 + "'s Walls: " + wallNum2);
            wallAmount2.repaint();

            buttonArray[p2RowStart][p2ColStart].setIcon(null); //Change Token location
            buttonArray[p2Row][p2Col].setIcon(p2Token); //Change Visual                               
            
            //closing file
            dis.close();
            dis2.close();   
         }
         catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(mExit, "No Save File Found.");   
         }
         catch(IOException e){e.printStackTrace();}
         catch(Exception e){e.printStackTrace();}      
      }
   }
   /**
   *Prompt to ask players for their Names when starting a new game
   */
   class NewPlayers{
   
      NewPlayers(){
         pName1 = JOptionPane.showInputDialog("Player 1, what would you like to be called?");
         if(pName1 == null || pName1.isEmpty()) {
            pName1 = "Player 1";
         }
         
         pName2 = JOptionPane.showInputDialog("What about you, Player 2?");
         if(pName2 == null || pName2.isEmpty()) {
            pName2 = "Player 2";
         }
      }
   }
   /**
   *Used when winner is found
   *Asks if there are the same players next game
   *If yes, it resets and plays keeping track of current win
   *If no, it resets the game and all information stored back to default
   */
   class PlayAgain{
  
      //Set up for next game
      int choice = 0;
      PlayAgain(){
      
      //Prompt for the new game
         Object[] paOptions = {"Yes", "No"};
         String paMessage = "Do you want to play again with the same players?";  
         choice = JOptionPane.showOptionDialog(null, paMessage, "Continue Playing?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, paOptions, paOptions[0]); 
         if(choice == JOptionPane.YES_OPTION){
            new Reset(); //resets the board
            new PlayGame(); // begins the game
         }
         else{
            new NewPlayers(); // ask players for their names
            new ResetScoreBoard(); // change the scoreboards back to 0
            new Reset(); //reset the board
            new PlayGame(); //Begins the game
         }
      }
   }
   /**
   *Used to reset the scoreboard and chang the text on the scoreboard
   */
   class ResetScoreBoard{
      ResetScoreBoard(){
      //reset Scoreboard wins
      p1Wins = 0;
      p2Wins = 0;
         
      //Change the scoreboard label
      jlScore.setText(pName1 + " : " + p1Wins + "   " + pName2 + " : " + p2Wins);
      jlScore.repaint();     
      }
   }
   /**
   *Main way of reseting the game
   *Resets token location, and information stored in grid
   *Changes turn order back to 1st player
   *Resets wall count and the display
   *Repaints walls as white
   */
   class Reset{
   
      Reset(){
      
         //Reset Token positions to start
         buttonArray[p1Row][p1Col].setIcon(null);
         buttonArray[p1RowStart][p1ColStart].setIcon(p1Token);
         buttonArray[p2Row][p2Col].setIcon(null);
         buttonArray[p2RowStart][p2ColStart].setIcon(p2Token);
         
         //Reset Values in Grid Array
         gridArray[p1Row][p1Col] = 0;
         gridArray[p2Row][p2Col] = 0;
         gridArray[p1RowStart][p1ColStart] = 1;
         gridArray[p2RowStart][p2ColStart] = 1;
         
         //Reset Values of Token Position Tracker
         p1Row = p1RowStart;
         p1Col = p1ColStart;
         p2Row = p2RowStart;               
         p2Col = p2ColStart;
      
         //reset player turn to 1
         turn = pName1;
         
         //reset wall count
         wallNum1 = 7;
         wallNum2 = 7;
         
         //Change wall label
         wallAmount1.setText(pName1 + "'s Walls: " + wallNum1);
         wallAmount1.repaint();
         wallAmount2.setText(pName2 + "'s Walls: " + wallNum2);
         wallAmount2.repaint();
         
         for(int i = 0; i < 15; i++){
            for(int j = 0; j < 15; j++){
               if(i % 2 == 0 && j % 2 == 1 && gridArray[i][j] == 1){
                  gridArray[i][j] = 0;
                  buttonArray[i][j].setBackground(Color.WHITE);
                  buttonArray[i][j].repaint();
               }
               if(i % 2 == 1 && j % 2 == 0 && gridArray[i][j] == 1){
                  gridArray[i][j] = 0;
                  buttonArray[i][j].setBackground(Color.WHITE);
                  buttonArray[i][j].repaint();
               }   
            }
         }
             
      }
   }
   /**
   *Prompts the player to place a wall or move a piece
   *uses an if else to change the prompt depending of if walls can or cannot be placed
   *Allows for selected option to be clickable (allows for game to progress)
   */
   class PlayGame{
      
      int choice = 0;
   
      PlayGame(){
         //while statement to check if win condition
         String stoppedMidwall = turn + "'s turn \n Please continnue placing your wall segmant.";
         String normalMessage = turn + "'s turn. \n Do you want to place a wall or move your piece?";
         String altMessage = turn + "'s turn. \n You're out of walls! Click to move your piece.";
         
         Object[] options = {"Place a Wall", "Move my Piece"};
         Object[] stoppedOptions = {"Place a Wall"};
         Object[] altOptions = {"Move my Piece"};
         
         if(count > 0){
            choice = JOptionPane.showOptionDialog(null, stoppedMidwall, "Continue Placing Wall Segmants", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stoppedOptions, stoppedOptions[0]); 
         }
         //codes for wall can't be placed
         else if((turn == pName1 && wallNum1 <= 0) || (turn == pName2 && wallNum2 <= 0)){
            choice = JOptionPane.showOptionDialog(null, altMessage, "Choose Your Move", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, altOptions, altOptions[0]);
         }
         //else if(turn == pName2 && wallNum2 <= 0){
            //choice = JOptionPane.showOptionDialog(null, altMessage, "Choose Your Move", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, altOptions, altOptions[0]);
         //}
         //Codes for if wall can get placed
         else{       
            choice = JOptionPane.showOptionDialog(null, normalMessage,"Choose Your Move", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
         }
         
         // Code for walls getting placed
         if(choice == JOptionPane.YES_OPTION){
         
            if(count > 0){
               new EnableCloseWall(true); //incase game was saved and opened mid wall placement
            }
            else{
               clickable = true;
               new EnableWallPlacement(clickable); //all other occasions this runs
            }
            //Disable Jmenu option since one is already selected this turn
            mPlace.setEnabled(false);           
         }
         else if( choice == JOptionPane.NO_OPTION){
            if(turn == pName1){
               clickable = true;
               new MoveableSpace(p1Row, p1Col, clickable);               
            }
            else{
               clickable = true;
               new MoveableSpace(p2Row, p2Col, clickable);    
            }
            //Disable JMenuItem option since movement choice already chosen
            mPlace.setEnabled(false);
         }     
      }
   }
   /**Allow for wall placement
   *loop to make walls that are not already clicked (stored in grid array as 0) clickable or unclickable
   *@param _clickable boolean that is used to enable or disable buttonArray
   */
   class EnableWallPlacement{
   
      EnableWallPlacement(boolean _clickable){
      
         boolean clickable = _clickable;
         
         for(int i = 0; i < 15; i++){       
            for(int j = 0; j < 15; j++){
               //Vertically Long
               if(i % 2 == 0 && j % 2 == 1 && gridArray[i][j] == 0){
                  buttonArray[i][j].setEnabled(clickable);
               }
               //Horizontally Long Buttons
               if(i % 2 == 1 && j % 2 == 0 && gridArray[i][j] == 0){
                  buttonArray[i][j].setEnabled(clickable);
               }
            }
         }   
      }
   }
   /**used for second wall pick insuring it is directly next to the 1st wall
   *usses first wall location to only make the 4 walls horz vertically next to it clickable
   *checks that these spaces don't contain walls before making them clickable
   *@param _clickable boolean that is used to enable or disable walls
   */
   class EnableCloseWall{
      
      EnableCloseWall(boolean _clickable){
         boolean clickable = _clickable;
         
         if(x % 2 == 0 && y % 2 == 1){
         
            try{
               if(gridArray[x - 2][y] == 0){
                  buttonArray[x - 2][y].setEnabled(clickable);
               }
            }
            catch(ArrayIndexOutOfBoundsException ae){}
            
            try{
               if(gridArray[x + 2][y] == 0){
                  buttonArray[x + 2][y].setEnabled(clickable);
               }               
            }
            catch(ArrayIndexOutOfBoundsException ae){}
         }    
         if(x % 2 == 1 && y % 2 == 0){
         
            try{
               if(gridArray[x][y - 2] == 0){
                  buttonArray[x][y - 2].setEnabled(clickable);
               }
            }
            catch(ArrayIndexOutOfBoundsException ae){}
            
            try{
               if(gridArray[x][y + 2] == 0){
                  buttonArray[x][y + 2].setEnabled(clickable);
               }
            }
            catch(ArrayIndexOutOfBoundsException ae){}
         }                
      }                   
   }
   /**
   *Uses the current location of the player token for row and col
   *Tries to make player spaces not currently occupied right,left, above, and below clickable
   *@param _row used to pass in the row location of the player's token
   *@param _col used to pass in the col location of the player's token
   *@param _clickable used to enable and disable player space buttons
   *@exception ArrayIndexOutOfBounds used in each incase the index checked is out of bounds
   *@see ArrayIndexOutOfBounds
   */  
   class MoveableSpace{
      
      MoveableSpace(int _row, int _col, boolean _clickable){
         
         int row = _row;
         int col = _col;
         boolean clickable = _clickable;
         
         try{
            //Check North wall and Player Space
            if(gridArray[row - 1][col] == 0 && gridArray[row - 2][col] == 0){
               buttonArray[row-2][col].setEnabled(clickable);
            }
         }
         catch(ArrayIndexOutOfBoundsException ae){}
         
         try{
            //Check South wall and Player Space
            if(gridArray[row + 1][col] == 0 && gridArray[row + 2][col] == 0){
               buttonArray[row+2][col].setEnabled(clickable);
            }
         }
         catch(ArrayIndexOutOfBoundsException ae){}
         
         try{
            //Check West wall and Player Space
            if(gridArray[row][col - 1] == 0 && gridArray[row][col - 2] == 0){
               buttonArray[row][col-2].setEnabled(clickable);
            }
         }
         catch(ArrayIndexOutOfBoundsException ae){}
         
         try{
            //Check East wall and Player Space
            if(gridArray[row][col + 1] == 0 && gridArray[row][col + 2] == 0){
               buttonArray[row][col+2].setEnabled(clickable);
            }
         }
         catch(ArrayIndexOutOfBoundsException ae){}
      }
   }
   /**
   *Main Function
   *set UI manager look and Feel
   *runs Quoridor() the main part of the game
   *
   *@exception UnsupportedLookAndFeelException incase the look or feel isn't supported 
   *@exception ClassNotFoundException
   *@exception InstantiationException
   *@exception IllegalAccessException
   *@see UnsupportedLookAndFeelException
   *@see ClassNotFoundException
   *@see InstantiationException
   *@see IllegalAcessException
   */
   public static void main(String[] args){
   
      try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
      } 
       catch (UnsupportedLookAndFeelException e) {
       // handle exception
      }
      catch (ClassNotFoundException e) {
       // handle exception
      }
      catch (InstantiationException e) {
       // handle exception
      }
      catch (IllegalAccessException e) {
       // handle exception
      }
      new Quoridor();
   }
}
