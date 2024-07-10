
import java.util.*;

public class LempelZiv {
    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */
    public static String compress(String input) {

        // TODO fill this in.
        int lookahead = 8;

        StringBuilder output = new StringBuilder();
        int cursor = 0;
        int windowSize = 100;
        while(cursor < input.length()){

            int length = 1;
            int preMatch = 0;
            while(true){
                int textStart = (cursor<windowSize)?0:cursor-windowSize;
                int match = -1;
                if(cursor + length <= input.length() && length <= lookahead) {

                    match = KMP.search(
                            input.substring(cursor, Math.min(cursor + length, cursor + lookahead)),
                            input.substring(textStart, cursor));
                }
                if(match != -1){
                    preMatch = match;
                    length++;
                }
                else{
                    String string = "";
                    if(input.length() > cursor + length - 1) {
                        string = Character.toString(input.charAt(cursor + length - 1));
                    }
                        int offset = cursor - preMatch;
                        if(cursor > windowSize){
                            offset = cursor - (cursor - windowSize + preMatch);
                        }
                        if (length == 1) {
                            offset = 0;
                        }
                        String add = "[" + (offset) + "|" + (length - 1)
                                + "|" + string + "]";
                        output.append(add);


                    cursor += length;
                    break;
                }
            }
        }
        return output.toString();
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        // TODO fill this in.
        StringBuilder output = new StringBuilder();
        int cursor = 0;
        List<String> tuples = tupleTable(compressed);
        for(String tuple: tuples){
            char character = tuple.charAt(tuple.length() - 2);

            if (tuple.charAt(1) != 0) {
                int bracket1 = tuple.indexOf('[');
                int bar1 = tuple.indexOf('|', bracket1);
                int bar2 = tuple.indexOf('|', bar1 + 1);
                // Parses the string argument as a signed decimal integer. The characters in the string must all be decimal digits
                int offset = Integer.parseInt(tuple.substring(bracket1 + 1, bar1));
                int length = Integer.parseInt(tuple.substring(bar1 + 1, bar2));

                for (int j = 0; j < length; j++) {

                    output.append(output.charAt(cursor - offset));

                    cursor++;
                }
            }
            if(character != '|'){
                output.append(character);
            }

            cursor++;
        }

        return output.toString();
    }

    public static List<String> tupleTable(String compressed){
        List<String> tuples = new ArrayList<>();
        int i = 0;
        StringBuilder eachTuple = new StringBuilder();
        while(i < compressed.length()){
            eachTuple.append(compressed.charAt(i));
            if(compressed.charAt(i) == ']'){
                tuples.add(eachTuple.toString());
                eachTuple = new StringBuilder();
            }
            i++;
        }

        return tuples;

    }



    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
