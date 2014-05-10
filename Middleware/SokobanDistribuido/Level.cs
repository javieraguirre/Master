using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace SokobanURJC
{
    
    /// <summary>
    /// Enum for the possible directions of Sokoban
    /// </summary>
    public enum MoveDirection
    {
        Right,
        Up,
        Down,
        Left
    }


	/// <summary>
	/// Level class. Keeps the level information and draws a level on screen.
	/// An 'item' in a level can be a wall, a floor, a box or sokoban. The
	/// width and height of a level are measured in items, not in pixels. The
	/// width and height of a level when drawed on the screen can be calculated
	/// by multiplying the width and height by the size of the item.
	/// </summary>
    /// 
    [Serializable]
    public class Level : System.MarshalByRefObject
	{
	    private string name = string.Empty; // Name of the level
	    private ItemType[,] levelMap;       // Level layout of items
	    private int nrOfGoals = 0;          // A box must be placed on a goal
	    private int levelNr = 0;
	    private int width = 0;              // Level width in items
	    private int height = 0;             // Level height in items
	    
	    // The name of the level set that the level belongs to
	    private string levelSetName = string.Empty;
	    
	    private int moves = 0;              // Sokoban number of moves
	    private int pushes = 0;             // Pushes (when a box is moved)
	    
	    private int sokoPosX;               // X position of Sokoban
	    private int sokoPosY;               // Y position of Sokoban
	    
	    private bool isUndoable = false;    // Indicates if we can do an undo
	    
	    // Default direction Sokoban is facing when starting a level.
	    private MoveDirection sokoDirection = MoveDirection.Right;
	    
        // ITEM_SIZE is the size of an item in the level
        // TO DO: Let user change it, and save the size in the savegame.xml ???
	    public const int ITEM_SIZE = 30;
	    
	    // changedItems is updated every time Sokoban moves or pushes a box.
	    // Max. 3 items can be changed each push, 2 for each move. We keep
	    // track of these change so we don't have to redraw the whole level
	    // after each move/push.
	    private Item item1, item2, item3;
	    
	    // Here are 3 items to keep the undo information
	    private Item item1U, item2U, item3U;
	    private int movesBeforeUndo = 0;    // Number of moves before an undo
	    private int pushesBeforeUndo = 0;   // Number of pushes before an undo
	    
	    // For drawing the level on screen
        public Bitmap img;
        private Graphics g;

        public String ImageToString(Bitmap img)
        {
            byte[] byteArray = new byte[0];
            using (MemoryStream stream = new MemoryStream())
            {
                img.Save(stream, System.Drawing.Imaging.ImageFormat.Png);
                stream.Close();
                byteArray = stream.ToArray();
            }
            return Convert.ToBase64String(byteArray);
        }


		#region Properties

        public string Name
        {
            get { return name; }
        }

        public int LevelNr
        {
            get { return levelNr; }
        }

        public int Width
        {
            get { return width; }
        }

        public int Height
        {
            get { return height; }
        }

        public string LevelSetName
        {
            get { return levelSetName; }
        }

        public int Moves
        {
            get { return moves; }
        }

        public int Pushes
        {
            get { return pushes; }
        }

        public bool IsUndoable
        {
            get { return isUndoable; }
        }
        
        #endregion
        
		
	    /// <summary>
	    /// Constructor.
	    /// </summary>
	    /// <param name="aName">Level name</param>
	    /// <param name="aLevelMap">Level map</param>
	    /// <param name="aWidth">Level width</param>
	    /// <param name="aHeight">Level height</param>
	    /// <param name="aNrOfGoals">Number of goals</param>
	    /// <param name="aLevelNr">Level number</param>
	    /// <param name="aLevelSetName">Set name that level belongs to</param>
        public Level(string aName, ItemType[,] aLevelMap, int aWidth,
		    int aHeight, int aNrOfGoals, int aLevelNr, string aLevelSetName)
		{
		    name = aName;
			width = aWidth;
			height = aHeight;
			levelMap = aLevelMap;
			nrOfGoals = aNrOfGoals;
			levelNr = aLevelNr;
			levelSetName = aLevelSetName;
		}
		

        /// <summary>
        /// This method draws the level on screen. Around the level there are
        /// extra rows and columns to make it look better. The first 3 for-
        /// statements draw this border. Then we load the level map and step
        /// through it line by line, and character by character. Depending on
        /// the ItemType in the level map, we draw the corresponding image.
        /// </summary>
        /// <returns>The 'level' image that will be drawn to screen</returns>

        public String DrawLevel() // img como String.
        // public Bitmap DrawLevel() // img como Bitmap.
		{
            int levelWidth = (width + 2) * Level.ITEM_SIZE;
            int levelHeight = (height + 2) * Level.ITEM_SIZE;
            
            img = new Bitmap(levelWidth, levelHeight);
            g = Graphics.FromImage(img);
            
		    Font statusText = new Font("Tahoma", 10, FontStyle.Bold);
		    
            g.Clear(Color.FromArgb(27, 33, 61));
		 
            // Draw the border around the level
            for (int i = 0; i < width + 2; i++)
            {
                g.DrawImage(ImgSpace, ITEM_SIZE * i, 0,
                    ITEM_SIZE, ITEM_SIZE);
                g.DrawImage(ImgSpace, ITEM_SIZE * i,
                    (height + 1) * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            }
            for (int i = 1; i < height + 1; i++)
                g.DrawImage(ImgSpace, 0, ITEM_SIZE * i,
                    ITEM_SIZE, ITEM_SIZE);
            for (int i = 1; i < height + 1; i++)
                g.DrawImage(ImgSpace, (width + 1) * ITEM_SIZE,
                    ITEM_SIZE * i, ITEM_SIZE, ITEM_SIZE);

            // Draw the level
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    Image image = GetLevelImage(levelMap[i, j], sokoDirection);

                    g.DrawImage(image, ITEM_SIZE + i * ITEM_SIZE,
                        ITEM_SIZE + j * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                    
                    // Set Sokoban's position
                    if (levelMap[i, j] == ItemType.Sokoban ||
                        levelMap[i, j] == ItemType.SokobanOnGoal)
                    {
                        sokoPosX = i;
                        sokoPosY = j;
                    }
                }
            }
            // return img; // img como Bitmap
            return ImageToString(img); // img como String
		}
		
		
		/// <summary>
		/// When Sokoban moves or pushes we only draws these changes instead of
		/// redrawing the whole level again. Great performance improvement.
		/// </summary>
		/// <returns>The 'level' image that will be drawn to screen</returns>

        public String DrawChanges()
        // public Bitmap DrawChanges()
		{
            Image image1 = GetLevelImage(item1.ItemType, sokoDirection);
            g.DrawImage(image1, ITEM_SIZE + item1.XPos * ITEM_SIZE,
                ITEM_SIZE + item1.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
		    
            Image image2 = GetLevelImage(item2.ItemType, sokoDirection);
            g.DrawImage(image2, ITEM_SIZE + item2.XPos * ITEM_SIZE,
                ITEM_SIZE + item2.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            
            if (item3 != null)
            {
                Image image3 = GetLevelImage(item3.ItemType, sokoDirection);
                g.DrawImage(image3, ITEM_SIZE + item3.XPos * ITEM_SIZE,
                    ITEM_SIZE + item3.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            }
            
            return ImageToString(img); // img como String.
            // return img; // img como Bitmap.
		}
		
		
		/// <summary>
		/// Checks if a level is finished/solved. A level is solved when all
		/// boxes has been places on all goals. So all we have to do is count
		/// the number of boxes placed on a goal and compare this with the
		/// total number of goals defined for a level.
		/// </summary>
		/// <returns>True if level is solved, otherwise false</returns>
        public bool IsFinished()
		{
		    int nrOfPackagesOnGoal = 0;
		    
		    for (int i = 0; i < width; i++)
		        for (int j = 0; j < height; j++)
		            if (levelMap[i, j] == ItemType.PackageOnGoal)
		                nrOfPackagesOnGoal++;
		            
		    return nrOfPackagesOnGoal == nrOfGoals ? true : false;
		}
		
		
		/// <summary>
		/// Undo the last push. First we draw the images as they were before
		/// the push. These images and their positions were stored just before
		/// we pushed a box. We also update the level map with the stored item
		/// types. The second thing we do is remove Sokoban from his current
		/// position, since we've already put him where he was when he pushed
		/// the box before our undo operation.
		/// </summary>
		/// <returns>The 'level' image that will be drawn to screen</returns>

        public Bitmap Undo() // img como Bitmap.
        // public String Undo() // img como String
		{
		    // item1U, item2U and item3U contains the ItemTypes and their
		    // positions at the time of just before the last push.
            Image image1 = GetLevelImage(item1U.ItemType, sokoDirection);
            g.DrawImage(image1, ITEM_SIZE + item1U.XPos * ITEM_SIZE,
                ITEM_SIZE + item1U.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            levelMap[item1U.XPos, item1U.YPos] = item1U.ItemType;
		
            Image image2 = GetLevelImage(item2U.ItemType, sokoDirection);
            g.DrawImage(image2, ITEM_SIZE + item2U.XPos * ITEM_SIZE,
                ITEM_SIZE + item2U.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            levelMap[item2U.XPos, item2U.YPos] = item2U.ItemType;

            Image image3 = GetLevelImage(item3U.ItemType, sokoDirection);
            g.DrawImage(image3, ITEM_SIZE + item3U.XPos * ITEM_SIZE,
                ITEM_SIZE + item3U.YPos * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
            levelMap[item3U.XPos, item3U.YPos] = item3U.ItemType;
            
            // Here we remove Sokoban from his current position and replace it
            // with a floor or goal (depending on where he was standing on).
            // If Sokoban was already standing on the same place as he was just
            // before the last push, we can skip this step.
            if (!(sokoPosX == item1U.XPos && sokoPosY == item1U.YPos))
            {
                if (levelMap[sokoPosX, sokoPosY] == ItemType.Sokoban)
                {
                    levelMap[sokoPosX, sokoPosY] = ItemType.Floor;
                    g.DrawImage(GetLevelImage(ItemType.Floor, MoveDirection.Up),
                        ITEM_SIZE + sokoPosX * ITEM_SIZE, ITEM_SIZE +
                        sokoPosY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                }
                else if (levelMap[sokoPosX, sokoPosY] == ItemType.SokobanOnGoal)
                {
                    levelMap[sokoPosX, sokoPosY] = ItemType.Goal;
                    g.DrawImage(GetLevelImage(ItemType.Goal, MoveDirection.Up),
                        ITEM_SIZE + sokoPosX * ITEM_SIZE, ITEM_SIZE +
                        sokoPosY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                }
            }
            
            // Update Sokoban's position
            sokoPosX = item1U.XPos;
            sokoPosY = item1U.YPos;
            
            // Restore the number of moves and pushes
            moves = movesBeforeUndo;
            pushes = pushesBeforeUndo;
            
            isUndoable = false;
		    
		    return img; // img como Bitmap.
            // return ImageToString(img); // img como String
		}
		
		
		#region Moving Sokoban
		
        /// <summary>
        /// Check in what direction we want to move and call the corresponding
        /// method.
        /// </summary>
        /// <param name="direction">Direction to move in</param>
        public void MoveSokoban(MoveDirection direction)
        {
            sokoDirection = direction;
		    
            switch (direction)
            {
                case MoveDirection.Up:
                    MoveUp();
                    break;
                case MoveDirection.Down:
                    MoveDown();
                    break;
                case MoveDirection.Right:
                    MoveRight();
                    break;
                case MoveDirection.Left:
                    MoveLeft();
                    break;
            }
        }
        
        
        // Here's what happens in the 4 move methods:
        // If the item in front of Sokoban is a box, we first have to check if
        // the item next to the box is a free space (floor or goal). If so, we
        // can move the box. Else, nothing will happen. Then we update the
        // levelmap. Note that the box as well as Sokoban can be standing on a
        // free floor or on a goal. Before moving Sokoban we also set the
        // current position of Sokoban, the box, and the free space thereafter
        // so we're able to undo the move.
        // If there's a free space in front of Sokoban, we can simply move him
        // one step. If Sokoban can't move in the desired direction (there's a
        // wall or no free space behind a box), nothing happens here.
        // At most, we have 3 items in the level that have changed. We put them
        // in 3 Item objects and redraw these after moving Sokoban. This way,
        // we don't have to redraw the whole level after each move, which
        // results in a huge performance improvement.
        // Lastly, we update the number of moves and pushes. Before this, we
        // set the movesBeforeUndo and pushesBeforeUndo to the current number
        // moves and pushes so we can restore these values when we undo a move.
        
        /// <summary>
        /// Move up
        /// </summary>
        private void MoveUp()
		{
		    if ((levelMap[sokoPosX, sokoPosY - 1] == ItemType.Package ||
		        levelMap[sokoPosX, sokoPosY - 1] == ItemType.PackageOnGoal) &&
		        (levelMap[sokoPosX, sokoPosY - 2] == ItemType.Floor ||
		        levelMap[sokoPosX, sokoPosY - 2] == ItemType.Goal))
		    {
		        item3U = new Item(levelMap[sokoPosX, sokoPosY - 2], sokoPosX, sokoPosY - 2);
		        item2U = new Item(levelMap[sokoPosX, sokoPosY - 1], sokoPosX, sokoPosY - 1);
		        item1U = new Item(levelMap[sokoPosX, sokoPosY], sokoPosX, sokoPosY);
		        
		        if (levelMap[sokoPosX, sokoPosY - 2] == ItemType.Floor)
		        {
		            levelMap[sokoPosX, sokoPosY - 2] = ItemType.Package;
		            item3 = new Item(ItemType.Package, sokoPosX, sokoPosY - 2);
		        }
		        else if (levelMap[sokoPosX, sokoPosY - 2] == ItemType.Goal)
		        {
		            levelMap[sokoPosX, sokoPosY - 2] = ItemType.PackageOnGoal;
		            item3 = new Item(ItemType.PackageOnGoal, sokoPosX, sokoPosY - 2);
		        }
                if (levelMap[sokoPosX, sokoPosY - 1] == ItemType.Package)
                {
                    levelMap[sokoPosX, sokoPosY - 1] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX, sokoPosY - 1);
                }
                else if (levelMap[sokoPosX, sokoPosY - 1] == ItemType.PackageOnGoal)
                {
                    levelMap[sokoPosX, sokoPosY - 1] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX, sokoPosY - 1);
                }
                
                isUndoable = true;
                UpdateCurrentSokobanPosition();
                movesBeforeUndo = moves;
                pushesBeforeUndo = pushes;
                moves++;
                pushes++;
                sokoPosY--;
		    }
		    else if (levelMap[sokoPosX, sokoPosY - 1] == ItemType.Floor ||
		        levelMap[sokoPosX, sokoPosY - 1] == ItemType.Goal)
		    {
                if (levelMap[sokoPosX, sokoPosY - 1] == ItemType.Floor)
                {
                    levelMap[sokoPosX, sokoPosY - 1] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX, sokoPosY - 1);
                }
                else if (levelMap[sokoPosX, sokoPosY - 1] == ItemType.Goal)
                {
                    levelMap[sokoPosX, sokoPosY - 1] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX, sokoPosY - 1);
                }
                
                item3 = null;
                UpdateCurrentSokobanPosition();
                moves++;
                sokoPosY--;
		    }
		}
		
		
		/// <summary>
		/// Move down
		/// </summary>
        private void MoveDown()
		{
            if ((levelMap[sokoPosX, sokoPosY + 1] == ItemType.Package ||
                levelMap[sokoPosX, sokoPosY + 1] == ItemType.PackageOnGoal) &&
                (levelMap[sokoPosX, sokoPosY + 2] == ItemType.Floor ||
                levelMap[sokoPosX, sokoPosY + 2] == ItemType.Goal))
            {
                item3U = new Item(levelMap[sokoPosX, sokoPosY + 2], sokoPosX, sokoPosY + 2);
                item2U = new Item(levelMap[sokoPosX, sokoPosY + 1], sokoPosX, sokoPosY + 1);
                item1U = new Item(levelMap[sokoPosX, sokoPosY], sokoPosX, sokoPosY);
                
                if (levelMap[sokoPosX, sokoPosY + 2] == ItemType.Floor)
                {
                    levelMap[sokoPosX, sokoPosY + 2] = ItemType.Package;
                    item3 = new Item(ItemType.Package, sokoPosX, sokoPosY + 2);
                }
                else if (levelMap[sokoPosX, sokoPosY + 2] == ItemType.Goal)
                {
                    levelMap[sokoPosX, sokoPosY + 2] = ItemType.PackageOnGoal;
                    item3 = new Item(ItemType.PackageOnGoal, sokoPosX, sokoPosY + 2);
                }
		            
                if (levelMap[sokoPosX, sokoPosY + 1] == ItemType.Package)
                {
                    levelMap[sokoPosX, sokoPosY + 1] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX, sokoPosY + 1);
                }
                else if (levelMap[sokoPosX, sokoPosY + 1] == ItemType.PackageOnGoal)
                {
                    levelMap[sokoPosX, sokoPosY + 1] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX, sokoPosY + 1);
                }
                
                isUndoable = true;
                UpdateCurrentSokobanPosition();
                movesBeforeUndo = moves;
                pushesBeforeUndo = pushes;
                moves++;
                pushes++;
                sokoPosY++;
            }
            else if (levelMap[sokoPosX, sokoPosY + 1] == ItemType.Floor ||
                levelMap[sokoPosX, sokoPosY + 1] == ItemType.Goal)
            {
                if (levelMap[sokoPosX, sokoPosY + 1] == ItemType.Floor)
                {
                    levelMap[sokoPosX, sokoPosY + 1] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX, sokoPosY + 1);
                }
                else if (levelMap[sokoPosX, sokoPosY + 1] == ItemType.Goal)
                {
                    levelMap[sokoPosX, sokoPosY + 1] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX, sokoPosY + 1);
                }
                
                item3 = null;
                UpdateCurrentSokobanPosition();
                moves++;
                sokoPosY++;
            }
        }
        
        
        /// <summary>
        /// Move right
        /// </summary>
        private void MoveRight()
        {
            if ((levelMap[sokoPosX + 1, sokoPosY] == ItemType.Package ||
                levelMap[sokoPosX + 1, sokoPosY] == ItemType.PackageOnGoal) &&
                (levelMap[sokoPosX + 2, sokoPosY] == ItemType.Floor ||
                levelMap[sokoPosX + 2, sokoPosY] == ItemType.Goal))
            {
                item3U = new Item(levelMap[sokoPosX + 2, sokoPosY], sokoPosX + 2, sokoPosY);
                item2U = new Item(levelMap[sokoPosX + 1, sokoPosY], sokoPosX + 1, sokoPosY);
                item1U = new Item(levelMap[sokoPosX, sokoPosY], sokoPosX, sokoPosY);
                
                if (levelMap[sokoPosX + 2, sokoPosY] == ItemType.Floor)
                {
                    levelMap[sokoPosX + 2, sokoPosY] = ItemType.Package;
                    item3 = new Item(ItemType.Package, sokoPosX + 2, sokoPosY);
                }
                else if (levelMap[sokoPosX + 2, sokoPosY] == ItemType.Goal)
                {
                    levelMap[sokoPosX + 2, sokoPosY] = ItemType.PackageOnGoal;
                    item3 = new Item(ItemType.PackageOnGoal, sokoPosX + 2, sokoPosY);
		        }    
                if (levelMap[sokoPosX + 1, sokoPosY] == ItemType.Package)
                {
                    levelMap[sokoPosX + 1, sokoPosY] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX + 1, sokoPosY);
                }
                else if (levelMap[sokoPosX + 1, sokoPosY] == ItemType.PackageOnGoal)
                {
                    levelMap[sokoPosX + 1, sokoPosY] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX + 1, sokoPosY);
                }
                
                isUndoable = true;
                UpdateCurrentSokobanPosition();
                movesBeforeUndo = moves;
                pushesBeforeUndo = pushes;
                moves++;
                pushes++;
                sokoPosX++;
            }
            else if (levelMap[sokoPosX + 1, sokoPosY] == ItemType.Floor ||
                levelMap[sokoPosX + 1, sokoPosY] == ItemType.Goal)
            {
                if (levelMap[sokoPosX + 1, sokoPosY] == ItemType.Floor)
                {
                    levelMap[sokoPosX + 1, sokoPosY] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX + 1, sokoPosY);
                }
                else if (levelMap[sokoPosX + 1, sokoPosY] == ItemType.Goal)
                {
                    levelMap[sokoPosX + 1, sokoPosY] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX + 1, sokoPosY);
                }
                
                item3 = null;
                UpdateCurrentSokobanPosition();
                moves++;
                sokoPosX++;
            }
        }
        
        
        /// <summary>
        /// Move left
        /// </summary>
        private void MoveLeft()
        {
            if ((levelMap[sokoPosX - 1, sokoPosY] == ItemType.Package ||
                levelMap[sokoPosX - 1, sokoPosY] == ItemType.PackageOnGoal) &&
                (levelMap[sokoPosX - 2, sokoPosY] == ItemType.Floor ||
                levelMap[sokoPosX - 2, sokoPosY] == ItemType.Goal))
            {
                item3U = new Item(levelMap[sokoPosX - 2, sokoPosY], sokoPosX - 2, sokoPosY);
                item2U = new Item(levelMap[sokoPosX - 1, sokoPosY], sokoPosX - 1, sokoPosY);
                item1U = new Item(levelMap[sokoPosX, sokoPosY], sokoPosX, sokoPosY);
                
                if (levelMap[sokoPosX - 2, sokoPosY] == ItemType.Floor)
                {
                    levelMap[sokoPosX - 2, sokoPosY] = ItemType.Package;
                    item3 = new Item(ItemType.Package, sokoPosX - 2, sokoPosY);
                }
                else if (levelMap[sokoPosX - 2, sokoPosY] == ItemType.Goal)
                {
                    levelMap[sokoPosX - 2, sokoPosY] = ItemType.PackageOnGoal;
                    item3 = new Item(ItemType.PackageOnGoal, sokoPosX - 2, sokoPosY);
		        }    
                if (levelMap[sokoPosX - 1, sokoPosY] == ItemType.Package)
                {
                    levelMap[sokoPosX - 1, sokoPosY] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX - 1, sokoPosY);
                }
                else if (levelMap[sokoPosX - 1, sokoPosY] == ItemType.PackageOnGoal)
                {
                    levelMap[sokoPosX - 1, sokoPosY] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX - 1, sokoPosY);
                }
                
                isUndoable = true;
                UpdateCurrentSokobanPosition();
                movesBeforeUndo = moves;
                pushesBeforeUndo = pushes;
                moves++;
                pushes++;
                sokoPosX--;
            }
            else if (levelMap[sokoPosX - 1, sokoPosY] == ItemType.Floor ||
                levelMap[sokoPosX - 1, sokoPosY] == ItemType.Goal)
            {
                if (levelMap[sokoPosX - 1, sokoPosY] == ItemType.Floor)
                {
                    levelMap[sokoPosX - 1, sokoPosY] = ItemType.Sokoban;
                    item2 = new Item(ItemType.Sokoban, sokoPosX - 1, sokoPosY);
                }
                else if (levelMap[sokoPosX - 1, sokoPosY] == ItemType.Goal)
                {
                    levelMap[sokoPosX - 1, sokoPosY] = ItemType.SokobanOnGoal;
                    item2 = new Item(ItemType.SokobanOnGoal, sokoPosX - 1, sokoPosY);
                }
                
                item3 = null;
                UpdateCurrentSokobanPosition();  
                moves++;              
                sokoPosX--;
            }
        }
        
        
        /// <summary>
        /// Updates Sokoban's position. This code is used in all the MoveXX
        /// methods, so I put it in a separate method.
        /// </summary>
        private void UpdateCurrentSokobanPosition()
        {
            if (levelMap[sokoPosX, sokoPosY] == ItemType.Sokoban)
            {
                levelMap[sokoPosX, sokoPosY] = ItemType.Floor;
                item1 = new Item(ItemType.Floor, sokoPosX, sokoPosY);
            }
            else if (levelMap[sokoPosX, sokoPosY] == ItemType.SokobanOnGoal)
            {
                levelMap[sokoPosX, sokoPosY] = ItemType.Goal;
                item1 = new Item(ItemType.Goal, sokoPosX, sokoPosY);
            }
        }
		
		#endregion
		
		#region GetLevelImage
		
		/// <summary>
		/// Depending on the 'item character' in the XML for the level set we
		/// need to display an image on the screen. This is what happens here.
		/// We also take into account the direction Sokoban is moving in,
		/// because we want him to face to the left when he is moving left.
		/// </summary>
		/// <param name="itemType">Level item</param>
		/// <param name="direction">Sokoban direction</param>
		/// <returns>The image to be displayed on screen</returns>
        // public String GetLevelImage(ItemType itemType, MoveDirection direction) 
        public Image GetLevelImage(ItemType itemType, MoveDirection direction)
		{
		    Image image;
		    
            if (itemType == ItemType.Wall)
                image = ImgWall;
            else if (itemType == ItemType.Floor)
                image = ImgFloor;
            else if (itemType == ItemType.Package)
                image = ImgPackage;
            else if (itemType == ItemType.Goal)
                image = ImgGoal;
            else if (itemType == ItemType.Sokoban)
            {
                if (direction == MoveDirection.Up)
                    image = ImgSokoUp;
                else if (direction == MoveDirection.Down)
                    image = ImgSokoDown;
                else if (direction == MoveDirection.Right)
                    image = ImgSokoRight;
                else
                    image = ImgSokoLeft;
            }
            else if (itemType == ItemType.PackageOnGoal)
                image = ImgPackageGoal;
            else if (itemType == ItemType.SokobanOnGoal)
            {
                if (direction == MoveDirection.Up)
                    image = ImgSokoUpGoal;
                else if (direction == MoveDirection.Down)
                    image = ImgSokoDownGoal;
                else if (direction == MoveDirection.Right)
                    image = ImgSokoRightGoal;
                else
                    image = ImgSokoLeftGoal;
            }
            else
                image = ImgSpace;

            Bitmap img = new Bitmap(image); // image como String
            
            return image;
            // return ImageToString(img); // img como String
		}
		
        
        // These are the proprties for the images of all possible items within
        // the game. These are hard coded. If we want to ad support for skins,
        // than we should put these values inside a skin XML file.

        public Image ImgWall
        {
            get { return
                Image.FromFile("graphics/original/wall.bmp"); }
        }

        public Image ImgFloor
        {
            get { return
                Image.FromFile("graphics/original/floor.bmp"); }
        }

        public Image ImgPackage
        {
            get { return
                Image.FromFile("graphics/original/package.bmp"); }
        }

        public Image ImgPackageGoal
        {
            get { return
                Image.FromFile("graphics/original/package_goal.bmp"); }
        }

        public Image ImgGoal
        {
            get { return
                Image.FromFile("graphics/original/goal.bmp"); }
        }

        public Image ImgSokoUp
        {
            get { return
                Image.FromFile("graphics/original/soko_up.bmp"); }
        }

        public Image ImgSokoDown
        {
            get { return
                Image.FromFile("graphics/original/soko_down.bmp"); }
        }

        public Image ImgSokoRight
        {
            get { return
                Image.FromFile("graphics/original/soko_right.bmp"); }
        }

        public Image ImgSokoLeft
        {
            get { return
                Image.FromFile("graphics/original/soko_left.bmp"); }
        }

        public Image ImgSokoUpGoal
        {
            get { return
                Image.FromFile("graphics/original/soko_goal_up.bmp"); }
        }

        public Image ImgSokoDownGoal
        {
            get { return
                Image.FromFile("graphics/original/soko_goal_down.bmp"); }
        }

        public Image ImgSokoRightGoal
        {
            get { return
                Image.FromFile("graphics/original/soko_goal_right.bmp"); }
        }

        public Image ImgSokoLeftGoal
        {
            get { return
                Image.FromFile("graphics/original/soko_goal_left.bmp"); }
        }

        public Image ImgSpace
        {
            get { return
                Image.FromFile("graphics/original/space.bmp"); }
        }
        
        #endregion
	}
}
