# Optical Barcode Readers and Writers

## Understand the Problem

Let's look at a Datamatrix. The solid black on the left and bottom is called the Closed Limitation Line. Look a little
more closely and you'll notice that the right and top consist of an alternating black and white pattern, so that the odd
numbered pixels on the far right (and top) are black, while the even numbered pixels are white. This is called the Open
Borderline.

The Closed Limitation Line and Open Borderline help the algorithms because they:

- Help situate the code in a standard position.
- Determine the minimum size of each pixel.
- Determine the height and width of the Datamatrix (which as you can see from the examples can vary).

An example of our datamatrix is this:

```
Open border line even '*'         ⬇️
* * * * * * * * * * * * * * * * * *⬅️    First line = 84 = 'T' in ASCII
*                                 * -------------- 128s   =    
***** ** * **** ****** ** **** **   -------------- 64s    = * 
* **************      ************* -------------- 32s    =   
**  *  *        *  *   *        *   -------------- 16s    = * 
* **  *     **    * *   * ****   ** -------------- Eights =   
**         ****   * ** ** ***   **  -------------- Fours  = * 
*   *  *   ***  *       *  ***   ** -------------- Twos   =   
*  ** ** * ***  ***  *  *  *** *    -------------- Ones   =   
***********************************⬅️         
⬆️Closed limitation line all '*'
```

- Use the solid Closed Limitation Line and the Open Borderline simply to identify the size and extent of the code.
- We'll look at (or print if we are creating the label) each column from left-to-right, converting the ASCII codes into
  a sequence of 8 characters

It turns out, the structure of the classes, objects and algorithms we'll need to write will be adequate as a framework
for the more complex and real Datamatrix, even though we will only program a faint suggestion of the real deal.

Here is a complete text and the code to go with it, for you to use as you write your program:

```
* * * * * * * * * * * * * * * * * *
*                                 *
***** ** * **** ****** ** **** **  
* **************      *************
**  *  *        *  *   *        *  
* **  *     **    * *   * ****   **
**         ****   * ** ** ***   ** 
*   *  *   ***  *       *  ***   **
*  ** ** * ***  ***  *  *  *** *   
***********************************
This is a good SAMPLE to look at.
```

#### class BarcodeImage implements Cloneable

- BarcodeImage class will be one of the main member-objects of the class
- BarcodeImage will describe the 2D dot-matrix pattern, or "image"
- It will contain methods for storing, modifying and retrieving the data in a 2D image.
- The interpretation of the data is not part of this class. Its job is only to manage the optical data. It will
  implement Cloneable interface because it contains deep data.

#### interface BarcodeIO

An Interface (which you define) called BarcodeIO that defines the I/O and basic methods of any barcode class which might
implement it.

#### class DataMatrix implements BarcodeIO

- BarcodeImage member object
- Text String member that represents the message encoded
- This is not a true Datamatrix because, for one thing, there is no Reed-Solomon error correction.

## Phase 1: BarcodeImage

This class will realize all the essential data and methods associated with a 2D pattern, thought of conceptually as an
image of a square or rectangular bar code.Here are the essential ingredients. This class has very little "smarts" in it,
except for the parameterized constructor. It mostly just stores and retrieves 2D data.

```java
public class BarcodeImage implements Cloneable {
   //The exact internal dimensions of 2D data. 
   public static final int MAX_WIDTH = 65;
   public static final int MAX_HEIGHT = 30;
   //This is where to store your image. If the incoming data is smaller than the max,
   //instantiate memory anyway, but leave it blank (white). This data will be false 
   //for elements that are white, and true for elements that are black.
   private boolean[][] imageData;
   //Methods:
   public BarcodeImage() { imageData = new boolean[MAX_HEIGHT][MAX_WIDTH]; }
   // Converts 1D array of Strings to the internal 2D array of booleans.
   // HINT: Image can be different size. The DataMatrix class will make sure 
   // that there is no extra space below or left of the image so this constructor
   // can put it into the lower-left corner of the array.
   public BarcodeImage(String[] strData) { }
   // Accessor and mutator for each bit in the image:
   public boolean getPixel(int row, int col) { } 
   public boolean setPixel(int row, int col, boolean value) { }
   // OPTIONAL:
   // Checking the incoming data for size or null error.Bigger or null is not ok
   public void checkSize(String[] data) {}
   public void displayToConsole() {}
   // method that overrides the method of that name in Cloneable interface.
   clone() throws CloneNotSupportedException {
      
      throw new CloneNotSupportedException();
   }
}
```

## Phase 2: BarcodeIO

BarcodeIO is expected to store some version of an image and some version of the text associated with that image.

```java
interface BarcodeIO {
   // Accepts a BarcodeImage object and stores a copy of this image.
   // Stored image might be an exact or a refined clone of the parameter
   // No translation. Text string is not touched, updated or defined
   public boolean scan(BarcodeImage bc);
   // Accepts a text string to be eventually encoded in an image.
   // No translation, BarcodeImage is not touched, updated or defined
   public boolean readText(String text);
   // Produces an image (in whatever format the implementing class using) using
   // Internal text stored in the implementing class
   public boolean generateImageFromText();
   // Opposite of generateImageFromTExt() above
   public boolean translateImageToText();
   // prints out the text string to the console.
   public void displayTextToConsole();
   // prints out the image to the console.
   public void displayImageToConsole();
}
```

## DataMatrix

Have the 2D array format and a left and bottom closed limitation line, top and right open borderline.

```java
public class DataMatrix implements BarcodeIO {
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   // A single internal copy of any image
   private BarcodeImage image;
   // A single internal copy of any text
   private String text;
   // Two ints that are less than barcode's MAX_WIDTH and MAX_HEIGHT
   private int actualWidth;
   private int actualHeight;

   // constructs an empty(non-null) image and text value. Image should be all 
   // white chars, actualWidth and actualHeight should start at 0
   // The text can be set to blank, "", or something like "undefined".
   public DataMatrix() {
      this.image = new BarcodeImage();
      actualHeight = actualWidth = 0;
      text = "";
   }

   // Sets the image but leaves the text at its default value.
   public DataMatrix(BarcodeImage image) {
      this.image = image;
      //Call scan() and avoid duplication of code here.
   }

   // Sets the text but leaves the image at its default value. 
   public DataMatrix(String text) {
      this.text = text;
      // Call readText() and avoid duplication of code here.
   }

   // A mutator for text. It is called by the constructor.
   public readText(String text) {   }

   // A mutator for image. It is called by the constructor.
   // Calls the clone(), cleanImage() and then set the actualWidth and actualHeight.
   // Don't attempt to hand-off the exception using a "throws" clause in the 
   // function header since that will not be compatible with the underlying BarcodeIO interface.
   public scan(BarcodeImage image) {
      try { clone(); } 
      catch (CloneNotSupportedException exception){ /* Leave empty */}
   }

   public int getActualWidth() {
      return this.getActualWidth;
   }

   public int getActualHeight() {
      return this.getActualHeight();
   }

   private int computeSignalWidth() {
   }

   // Using left and bottom BLACK to determine the actual size.
   private int computeSignalHeight() {   }

   // Implementation of all BarcodeIO methods.
   
   // Incoming BarcodeImage may not be lower-left justified. Called in scan() 
   // and would move the signal to the lower-left of the larger 2D array.
   private void cleanImage() {   }

   private void moveImageToLowerLeft() {   }

   private void shiftImageDown(int offset) {   }

   private void shiftImageLeft(int offset) {   }
}
```

Here's an example of the placement of such an un-standardized image:

```
--------------------------------------------------------------
|                                                            |
|                                                            |
|  * * * * * * * * * * * * * * * * * * *                     |
|  *                                    *                    |
|  **** *** **   ***** ****   *********                      |
|  * ************ ************ **********                    |
|  ** *      *    *  * * *         * *                       |
|  ***   *  *           * **    *      **                    |
|  * ** * *  *   * * * **  *   ***   ***                     |
|  * *           **    *****  *   **   **                    |
|  ****  *  * *  * **  ** *   ** *  * *                      |
|  **************************************                    |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
--------------------------------------------------------------
```

### After cleanImage():

```
-------------------------------------------------------------- 
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|                                                            |
|* * * * * * * * * * * * * * * * * * *                       |
|*                                    *                      |
|**** *** **   ***** ****   *********                        |
|* ************ ************ **********                      |
|** *      *    *  * * *         * *                         |
|***   *  *           * **    *      **                      |
|* ** * *  *   * * * **  *   ***   ***                       |
|* *           **    *****  *   **   **                      |
|****  *  * *  * **  ** *   ** *  * *                        |
|**************************************                      |
--------------------------------------------------------------
```

### Other considerations for DataMatrix

```java

displayImageToConsole() {} 
//should display only the relevant portion of the image,
// clipping the excess blank/white from the top and right.
// Also, show a border as in:
------------------------------------
|* * * * * * * * * * * * * * * * * |
|*                                *|
|****   * ***** **** **** ******** |
|*   *** ***************** ********|
|*  * **  *   *   *  *    * **     |
|* *       * *  **    * * *    ****|
|*     *   *    ** * *  *  *  ** * |
|** * *** *****  **     * *      **|
|****  *   **** ** *   *   *  * *  |
|**********************************|
```

## Recommendation

The methods generateImageFromText() and translateImageToText(), are the tricky parts, and it will help if you have some
methods like the following to break up the work:  private char readCharFromCol(int col) and private boolean
WriteCharToCol(int col, int code). While you don't have to use these exact methods, you must not turn in huge methods
generateImageFromText() and translateImageToText() that are not broken down to smaller ones. Optional - public void
displayRawImage() can be implemented to show the full image data including the blank top and right. It is a useful
debugging tool. Optional - private void clearImage() - a nice utility that sets the image to white = false.

You may need to digest what you are doing and why you are doing it at each juncture. If you just focus on each
individual method, writing and testing as you go, you will be fine. You and your team may need to spend time over
multiple sittings to do this. I am here for questions, as usual.

Here is a sample main() to run. You can add to it, but include these bar codes for decoding:

```
   public static void main(String[] args)
   {
      String[] sImageIn =
      {
         "                                               ",
         "                                               ",
         "                                               ",
         "     * * * * * * * * * * * * * * * * * * * * * ",
         "     *                                       * ",
         "     ****** **** ****** ******* ** *** *****   ",
         "     *     *    ****************************** ",
         "     * **    * *        **  *    * * *   *     ",
         "     *   *    *  *****    *   * *   *  **  *** ",
         "     *  **     * *** **   **  *    **  ***  *  ",
         "     ***  * **   **  *   ****    *  *  ** * ** ",
         "     *****  ***  *  * *   ** ** **  *   * *    ",
         "     ***************************************** ",  
         "                                               ",
         "                                               ",
         "                                               "

      };      
            
         
      
      String[] sImageIn_2 =
      {
            "                                          ",
            "                                          ",
            "* * * * * * * * * * * * * * * * * * *     ",
            "*                                    *    ",
            "**** *** **   ***** ****   *********      ",
            "* ************ ************ **********    ",
            "** *      *    *  * * *         * *       ",
            "***   *  *           * **    *      **    ",
            "* ** * *  *   * * * **  *   ***   ***     ",
            "* *           **    *****  *   **   **    ",
            "****  *  * *  * **  ** *   ** *  * *      ",
            "**************************************    ",
            "                                          ",
            "                                          ",
            "                                          ",
            "                                          "

      };
     
      BarcodeImage bc = new BarcodeImage(sImageIn);
      DataMatrix dm = new DataMatrix(bc);
     
      // First secret message
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      dm.translateImageToText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
      
      // create your own message
      dm.readText("What a great resume builder this is!");
      dm.generateImageFromText();
      dm.displayTextToConsole();
      dm.displayImageToConsole();
   }
```

## Phase 3: Draw the UML diagram

Use the tool, Gliffy, to build a UML diagram. Use this link for Gliffy (Links to an external site.) . Click on "sign up"
in the top right and then use your Google CSUMB account to create an account. Make sure to click the link in the "
Welcome To Gliffy!" email to validate your email account. Just creating an account with an EDU email is not enough. It
will add a bunch of time to your trial and unlock various premium features (including JPG export). Export a .jpg file to
use for submission.

Submission

Turn in one .txt file with your code from phases 1- 2 including output. UML diagram file.