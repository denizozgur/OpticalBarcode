/**
 * @author Deniz Erisgen ©
 **/

import java.util.BitSet;

interface BarcodeIO {
   /**
    * Accepts a BarcodeImage object and stores a copy of this image.
    * Stored image might be an exact or a refined clone of the parameter
    * No translation. Text string is not touched, updated or defined
    */

   boolean scan(BarcodeImage bc);

   /**
    *  Accepts a text string to be eventually encoded in an image
    *  No translation, BarcodeImage is not touched, updated or defined
    * @param text
    * @return
    */
   boolean readText(String text);

   // Produces image using internal stored text in the implementing class
   boolean generateImageFromText();

   // Produces text using internal stored image in the implementing class
   boolean translateImageToText();

   // prints out the text string to the console.
   void displayTextToConsole();

   // prints out the image to the console.
   void displayImageToConsole();
}

class OpticalBarcodeReader {
   public static void main(String[] args) {
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
      if (dm.translateImageToText()) dm.displayImageToConsole();
      dm.displayTextToConsole();

      // second secret message
      bc = new BarcodeImage(sImageIn_2);
      dm.scan(bc);
      if (dm.translateImageToText()) dm.displayImageToConsole();
      dm.displayTextToConsole();

      // create your own message
      dm.readText("What a great resume builder this is!");
      if (dm.generateImageFromText()) dm.displayImageToConsole();
      dm.displayTextToConsole();

      // create your own message
      dm.readText("This was an amazing assignment!");
      if (dm.generateImageFromText()) dm.displayImageToConsole();
      dm.displayTextToConsole();
   }
}

class BarcodeImage implements Cloneable {
   //The exact internal dimensions of 2D data.
   public static final int MAX_WIDTH = 65;
   public static final int MAX_HEIGHT = 30;
   private boolean[][] imageData;// This is where to store your image.

   public BarcodeImage() {
      // default init value for bool is already false
      imageData = new boolean[BarcodeImage.MAX_HEIGHT][BarcodeImage.MAX_WIDTH];
   }

   // Converts 1D array of Strings to the internal 2D array of booleans.
   public BarcodeImage(String[] strData) {
      if (checkSize(strData)) imageData = new boolean[BarcodeImage.MAX_HEIGHT][BarcodeImage.MAX_WIDTH];
      int lines = BarcodeImage.MAX_HEIGHT - 1; // marker for actual bottom row
      for (int i = strData.length - 1; i >= 0; i--) {
         // skipping blank lines
         while (strData[i].isBlank()) {
            i--;
            if (i == 0) break;
         }
         // all leading and trailing space removed from line
         String cleanLine = strData[i].trim();
         for (int j = 0; j < cleanLine.length(); j++) {
            if (cleanLine.charAt(j) != ' ') setPixel(lines, j, true);
         }
         lines--;
      }
   }

   // Accessor and mutator for each bit in the image:
   public boolean getPixel(int row, int col) {
      if (imageData != null && row <= BarcodeImage.MAX_HEIGHT && col <= BarcodeImage.MAX_WIDTH) {
         return imageData[row][col];
      } else return false;
   }

   public boolean setPixel(int row, int col, boolean value) {
      if (imageData != null && row <= BarcodeImage.MAX_HEIGHT && col <= BarcodeImage.MAX_WIDTH) {
         imageData[row][col] = value;
         return true;
      } else return false;
   }

   // Checking incoming data if bigger or null
   private boolean checkSize(String[] data) {
      if (data == null || data.length == 0) return false;
      return (data.length <= BarcodeImage.MAX_HEIGHT && data[0].length() <= BarcodeImage.MAX_WIDTH);
   }

   // barcode debug print
   public void displayToConsole() {
      for (boolean[] imageLines : imageData) {
         for (boolean pixel : imageLines) System.out.print(pixel ? '*' : ' ');
         System.out.print('\n');
      }
   }

   public BarcodeImage clone() {
      try {
         BarcodeImage clone = (BarcodeImage) super.clone();
         clone.imageData = new boolean[BarcodeImage.MAX_HEIGHT][BarcodeImage.MAX_WIDTH];
         for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++) {
            System.arraycopy(imageData[i], 0, clone.imageData[i],
                  0, imageData[i].length);
         }
         return clone;
      } catch (CloneNotSupportedException e) {
         return null;
      }
   }
}

class DataMatrix implements BarcodeIO {
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   // Member Data
   private BarcodeImage image;
   private String text;
   private int actualWidth, actualHeight;

   // constructs an empty image and text. actualWidth and actualHeight is 0
   public DataMatrix() {
      image = new BarcodeImage();
      actualHeight = actualWidth = 0;
      text = "";
   }

   // Sets the image but leaves the text at its default value.
   public DataMatrix(BarcodeImage image) {
      if (!(scan(image))) System.exit(1);
      text = " ";
   }

   // Sets the text but leaves the image at its default value.
   public DataMatrix(String text) {
      if (!(readText(text))) System.exit(2);
   }

   // Calls the clone(), cleanImage() and then set the actualWidth,actualHeight
   public boolean scan(BarcodeImage image) {
      this.image = image.clone();
      if (this.image == null) return false;
      cleanImage();
      actualWidth = computeSignalWidth();
      actualHeight = computeSignalHeight();
      return true;
   }

   // Accessor for width
   public int getActualWidth() {
      return actualWidth;
   }

   // Accessor for height
   public int getActualHeight() {
      return actualHeight;
   }

   // Using left '*' computing height
   private int computeSignalHeight() {
      int rows = 0;
      int line = BarcodeImage.MAX_HEIGHT - 1;
      while (image.getPixel(line--, 0)) rows++;
      return rows;
   }

   // Using bottom '*' computing width
   private int computeSignalWidth() {
      int width = 0;
      while (image.getPixel(BarcodeImage.MAX_HEIGHT - 1, width++)) ;
      return --width; // for first '*'
   }

   private void cleanImage() {
      // Clean Image == Bottom left corner is true
      if (!(image.getPixel(BarcodeImage.MAX_HEIGHT - 1, 0)))
         moveImageToLowerLeft();
   }

   //Move the signal to the lower-left of the larger 2D array.
   private void moveImageToLowerLeft() {
      BarcodeImage tempImage = new BarcodeImage();
      int startRow, startCol;
      startRow = startCol = 0;
      //find lower left Coordinates starting from bottom
      for (int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--) {
         for (int j = 0; j < BarcodeImage.MAX_WIDTH; j++) {
            if (image.getPixel(i, j)) {
               startRow = i; // Row
               startCol = j; // Column
               break;
            }
         }
      }

      for (int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--) {
         for (int j = 0; j < BarcodeImage.MAX_WIDTH - startCol; j++) {
            tempImage.setPixel(i, j, image.getPixel(startRow, startCol));
            startCol++;
         }
         startRow--;
      }
      image = tempImage;
   }

   // checks text and initializes a new image
   public boolean readText(String text) {
      if (text != null && text.length() + 2 < BarcodeImage.MAX_WIDTH) {
         this.text = text;
         image = new BarcodeImage();
         actualHeight = 10; // 8bits + frame
         actualWidth = text.length() + 2; // text length + frame
         return true;
      }
      return false;
   }

   // generates image using char BitSets
   public boolean generateImageFromText() {
      if (image == null || text == null || text.isEmpty() || text.isBlank()) {
         return false;
      }

      int letterCount = 0;
      int startRow = BarcodeImage.MAX_HEIGHT - 1;
      int topRow = (startRow - actualHeight) + 1;

      for (int i = 0; i < actualWidth; i++) {
         image.setPixel(startRow, i, true);
         image.setPixel(topRow, i, (i % 2) == 0);
      }
      startRow--;
      letterCount++;
      for (int x : text.toCharArray()) {
         BitSet set = convertCharToBitSet(x);
         for (int i = 0; i < 8; i++) {
            image.setPixel(startRow - i, 0, true);
            image.setPixel(startRow - i, letterCount, set.get(i));
            if (((startRow - i) % 2) == 1) image.setPixel((startRow - i)
                  , actualWidth - 1, true);
         }
         letterCount++;
      }
      return true;
   }

   // debug printer
   private void displayRawImage() {
      for (int i = 0; i < BarcodeImage.MAX_HEIGHT; i++) {
         for (int j = 0; j < BarcodeImage.MAX_WIDTH; j++) {
            System.out.print((image.getPixel(i, j) ? DataMatrix.BLACK_CHAR : DataMatrix.WHITE_CHAR));
         }
         System.out.print('\n');
      }
   }

   // translates each column to a char and returns the string
   public boolean translateImageToText() {
      if (image == null) return false;
      StringBuilder builder = new StringBuilder();
      int startRow = BarcodeImage.MAX_HEIGHT - 2;
      for (int i = 1; i < getActualWidth() - 1; i++) {
         BitSet readBitSet = new BitSet();
         for (int j = 0; j < 8; j++) {
            if (image.getPixel(startRow - j, i)) readBitSet.set(j);
         }
         builder.append(convertBitSetToChar(readBitSet));
      }
      text = builder.toString();
      return true;
   }

   // converts letter (int value) to a BitSet
   private BitSet convertCharToBitSet(int value) {
      BitSet bitSet = new BitSet();
      int bit = 0;
      while (value != 0) {
         bitSet.set(bit++, ((value % 2) == 1));
         value >>= 1;
      }
      return bitSet;
   }

   // converts BitSet to Char
   private char convertBitSetToChar(BitSet bSet) {
      short total = 0;
      for (int i = 0; i < 8; i++) {
         total += (bSet.get(i) ? 1 : 0) << i;
      }
      return (char) total;
   }

   public void displayTextToConsole() {
      System.out.println(text);
   }

   // prints image without empty lines
   public void displayImageToConsole() {
      int startRow = 0;
      while (!(image.getPixel(startRow, 0))) {
         startRow++;
         if (startRow == BarcodeImage.MAX_HEIGHT) break;
      }
      String bar = "-", post = "|";
      int barLength = getActualWidth() + 2;
      System.out.println(bar.repeat(barLength));
      for (int i = BarcodeImage.MAX_HEIGHT - actualHeight;
           i < BarcodeImage.MAX_HEIGHT; i++) {
         System.out.print(post);
         for (int j = 0; j < actualWidth; j++) {
            System.out.print(image.getPixel(i, j) ? DataMatrix.BLACK_CHAR : DataMatrix.WHITE_CHAR);
         }
         System.out.print(post + '\n');
      }
      System.out.println(bar.repeat(barLength));
   }
}