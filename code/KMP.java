import java.util.ArrayList;
import java.util.List;

/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {

	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */




	public static int search(String pattern, String text) {
		// TODO fill this in.
		if(pattern.isEmpty()){
			return -1;
		}
		return performKMP(pattern, text, computeTable(pattern));


	}

	public static int performKMP(String pattern, String text, List<Integer> table){
		// the index of the element in the text that we want to try to match from the pattern
		// 0 means the beginning
		int matchInTextIndex = 0;
		// the index of teh element in the pattern that we want to mathch with the element in the text
		// 0 means the first element in the pattern
		int currentCharacterIndex = 0;

		// if the addition of them are within the length of the text,
		// indicating that there are enough elements in the text that we can match.
		// if there is not enough elements in the text we can match, impossible have any matching
		while(matchInTextIndex + currentCharacterIndex < text.length()){
			/*if the character in the pattern match the character in the text
			at the corresponding index, move the index of the character in the pattern to one index right
			the index of the character in the text will also move one
			index right as matchInTextIndex + currentCharacterIndex
			 */

			if(pattern.charAt(currentCharacterIndex) == text.charAt(matchInTextIndex + currentCharacterIndex)){
				currentCharacterIndex++;
				/*
				if the index of the character I want to check is equal to the size of the partial table
				I finished checking all the index, so we find the start point of the match
				 */
				if(currentCharacterIndex == table.size()){
					return matchInTextIndex;
				}
			}
			/* if the two characters are not matched and that is the first character in the text
				we directly go to the next character as no "prefix == suffix"
			 */
			else if(table.get(currentCharacterIndex) == -1){
				// move the character of the text one step forward
				// ? why also add currentCharacterIndex
				matchInTextIndex = matchInTextIndex + currentCharacterIndex + 1;
				// reset the index of the pattern to the start to rematch from the start
				currentCharacterIndex = 0;
			}
			/* if not match, and it is not failed at the beginning, there might be "prefix == suffix"
			so there is a jump to backward
			 */
			else {
				matchInTextIndex = matchInTextIndex + currentCharacterIndex - table.get(currentCharacterIndex);
				currentCharacterIndex = table.get(currentCharacterIndex);
			}

		}
		return -1;

	}

	public static List<Integer> computeTable(String string){

		List<Integer> table = new ArrayList<>();

		for(int i = 0; i < string.length(); i++){
			table.add(-1);
		}
		table.set(0, -1);
		if(string.length() <= 1){
			return table;
		}
		table.set(1, 0);

		int prefixMatchLength = 0;
		// the index of the character we want to check if there is any prefix
		int posTableIndex = 2;
		// we continue check if there is any remain index 
		while(posTableIndex < string.length()){
			/*
			if the last character before the current position is the same as the first
			character in the string, we found prefix == suffix
			 */
			if(string.charAt(posTableIndex - 1) == string.charAt(prefixMatchLength)){
				table.set(posTableIndex, prefixMatchLength + 1);
				posTableIndex++;
				prefixMatchLength++;
			}
			else if(prefixMatchLength > 0){
				prefixMatchLength = table.get(prefixMatchLength);
			}
			else{ // prefixMatchLength = 0
				table.set(posTableIndex, 0);
				posTableIndex++;
			}
		}
		return table;
	}
}
