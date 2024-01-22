package HW1;

// This is the starting version of the UPC-A scanner
//   that needs to be filled in for the homework

public class UPC {
	
	//--------------------------------------------
	// Scan in the bit pattern from the image
	// Takes the filename of the image
	// Returns an int array of the 95 scanned bits
	//--------------------------------------------
	public static int[] scanImage(String filename) {
		
		//initialize variables
		int[] fin = new int[95];
		DUImage barCodeImage = new DUImage(filename);
		
		//assign each line in the barcode to a value in the array
		for(int i = 0; i < 95; i++)
			fin[i] = 1 - barCodeImage.getRed(5+(2*i), 50) / 255;
		
		return fin;
	}
	
	//--------------------------------------------
	// Finds the matching digit for the given pattern
	// This is a helper method for decodeScan
	// Takes the full 95 scanned pattern as well as
	//   a starting location in that pattern where we
	//   want to look
	// Also takes in a boolean to indicate if this is a
	//   left or right pattern
	// Returns an int indicating which digit matches
	//   Any pattern that doesn't match anything will be -1
	//--------------------------------------------
	public static int matchPattern(int[] scanPattern, int startIndex, boolean left) {
		
		int[][] digitPat = {{0,0,0,1,1,0,1},
				            {0,0,1,1,0,0,1},	
				            {0,0,1,0,0,1,1},
				            {0,1,1,1,1,0,1},
				            {0,1,0,0,0,1,1},
				            {0,1,1,0,0,0,1},
				            {0,1,0,1,1,1,1},
				            {0,1,1,1,0,1,1},
				            {0,1,1,0,1,1,1},
				            {0,0,0,1,0,1,1}};
		
		//if the pattern is right, flip the digits
		int[] temp = new int[7];
		for(int i = 0; i < 7; i++)
			if(left)
				temp[i] = scanPattern[startIndex+i];
			else
				temp[i] = 1-scanPattern[startIndex+i];

		//for each entry in digitPat, check its values against those of the scanPattern
		//if a digit does not match, skip the rest of the entry
		//if the entire entry is checked without a single divergent digit, return the index of the entry
		for(int i = 0; i < 10; i++)
			for(int k = 0; k < 7; k++) {
				if(temp[k] != digitPat[i][k])
					k = 7;
				if(k == 6)
					return i;
			}
		
		//if no entry in digitPat matches the scanPattern, return -1
		return -1;
		
	}
	
	//--------------------------------------------
	// Performs a full scan decode that turns all 95 bits
	//   into 12 digits
	// Takes the full 95 bit scanned pattern
	// Returns an int array of 12 digits
	//   If any digit scanned incorrectly it is returned as a -1
	// If the start, middle, or end patterns are incorrect
	//   it provides an error and exits
	//--------------------------------------------
	public static int[] decodeScan(int[] scanPattern) {
		
		//initialize variables, including the correct start, end, and middle patterns, and the final array
		int[] startEndPat = {1, 0, 1};
		int[] midPat = {0, 1, 0, 1, 0};
		int[] fin = new int[12];
		
		for(int i = 0; i < 95;) {
			
			//for the first three digits, if the start pattern is incorrect, send an error
			//otherwise, move to the next digit
			if(i < 3) {
				if(scanPattern[i] != startEndPat[i]) {
					System.out.println("Start pattern is incorrect");
					System.exit(1); }
				i++;
			}
			//between the start and middle patterns, send the next 7 digits to the matchPattern method, and mark them as a left pattern
			else if(i < 45) {
				fin[(i-3)/7] = matchPattern(scanPattern, i, true);
				i += 7;
			}
			//for the middle 5 digits, if the middle pattern is incorrect, send an error
			//otherwise, move to the next digit
			else if(i < 50) {
				if(scanPattern[i] != midPat[i-45]) {
					System.out.println("Middle pattern is incorrect");
					System.exit(1); }
				i++;
			}
			//between the middle and end patterns, send the next 7 digits to the matchPattern method, and mark them as a right pattern
			else if(i < 92) {
				fin[(i-8)/7] = matchPattern(scanPattern, i, false);
				i += 7;
			}
			//for the last three digits, if the end pattern is incorrect, send an error
			//otherwise, move to the next digit
			else {
				if(scanPattern[i] != startEndPat[94-i]) {
					System.out.println("End pattern is incorrect");
					System.exit(1); }
				i++;
			}
		}
		return fin;

	}
	
	//--------------------------------------------
	// Do the checksum of the digits here
	// All digits are assumed to be in range 0..9
	// Returns true if check digit is correct and false otherwise
	//--------------------------------------------
	public static boolean verifyCode(int[] digits) {
		
		//In the UPC-A system, the check digit is calculated as follows:
		//	1.Add the digits in the even-numbered positions (zeroth, second, fourth, sixth, etc.) together and multiply by three.
		//	2.Add the digits in the odd-numbered positions (first, third, fifth, etc.) to the result.
		//	3.Find the result modulo 10 (i.e. the remainder when divided by 10.. 10 goes into 58 5 times with 8 leftover).
		//	4.If the result is not zero, subtract the result from ten.

		// Note that what the UPC standard calls 'odd' are our evens since we are zero based and they are one based
		
		//initialize the even and odd sums
		int evens = 0;
		int odds = 0;
		
		//add each value at an even index to the even sum, and each value at an odd index to the odd sum
		for(int i = 0; i < 11; i++)
			if(i % 2 == 0)
				evens += digits[i];
			else
				odds += digits[i];
		
		//set the check digit to the even sum multiplied by 3, plus the odd sum, modulo 10
		int checkDig = ((evens*3)+odds)%10;
		
		//if the check digit is not 0, set it to itself subtracted from 10
		if(checkDig != 0)
			checkDig = 10 - checkDig;
		
		return digits[11] == checkDig;
	}
	
	//--------------------------------------------
	// The main method scans the image, decodes it,
	//   and then validates it
	//--------------------------------------------	
	public static void main(String[] args) {
	        // file name to process.
	        // Note: change this to other files for testing
	        String barcodeFileName = "barcode1.png";

	        // optionally get file name from command-line args
	        if(args.length == 1){
		    barcodeFileName = args[0];
		}
		
		// scanPattern is an array of 95 ints (0..1)
		int[] scanPattern = scanImage(barcodeFileName);

		// Display the bit pattern scanned from the image
		System.out.println("Original scan");
		for (int i=0; i<scanPattern.length; i++) {
			System.out.print(scanPattern[i]);
		}
		System.out.println(""); // the \n
				
		
		// digits is an array of 12 ints (0..9)
		int[] digits = decodeScan(scanPattern);
		
		//if the first digit returns an error, fix the function to work upside-down
		if(digits[0] == -1) {
			//initialize a new array for the updated scanPattern
			int[] newPattern = new int[95];
			//for each index in the new array, set it to the value at the opposite index in the old array
			for(int i = 0; i < 95; i++)
				newPattern[i] = scanPattern[94-i];
			//re-calculate the digits array with the updated pattern
			digits = decodeScan(newPattern);
		}
		
		
		// Display the digits and check for scan errors
		boolean scanError = false;
		System.out.println("Digits");
		for (int i=0; i<12; i++) {
			System.out.print(digits[i] + " ");
			if (digits[i] == -1) {
				scanError = true;
			}
		}
		System.out.println("");
				
		if (scanError) {
			System.out.println("Scan error");
			
		} else { // Scanned in correctly - look at checksum
		
			if (verifyCode(digits)) {
				System.out.println("Passed Checksum");
			} else {
				System.out.println("Failed Checksum");
			}
		}
	}
}

