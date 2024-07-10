/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {
	/**
	 * This would be a good place to compute and store the tree.
	 */

	private Map<Character, String> codes;
	private Node root;
	public HuffmanCoding(String text) {

		Map<Character, Integer> frequencies = getFrequencies(text);

		Queue<Node> queue = getPriorityQueue(frequencies);

		root = getTree(queue);
		assignCode(root);



	}

	private Queue<Node> getPriorityQueue(Map<Character, Integer> frequencies){
		Queue<Node> queue = new PriorityQueue<>();
		for(char character: frequencies.keySet()){
			Node n = new Node(character, frequencies.get(character), null, null);
			queue.offer(n);
		}
		return queue;

	}

	private Node getTree(Queue<Node> queue){
		while(queue.size() > 1){
			Node left = queue.poll();
			Node right = queue.poll();
			int sumFreq = left.getFreq() + right.getFreq();
			Node parent = new Node('\0', sumFreq, left, right);
			queue.offer(parent);
		}
		return queue.poll();
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 */
	public String encode(String text) {
		StringBuilder ans = new StringBuilder();

		for(char character: text.toCharArray()){
			ans.append(codes.get(character));
		}
		return ans.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {

		// TODO fill this in.
		StringBuilder answer = new StringBuilder();
		Node current = root;

		for(char binary: encoded.toCharArray()){

			if(binary == '0'){
				current = current.getLeft();

			}
			else{
				current = current.getRight();
			}


			if(current.getName() != '\0'){
				answer.append(current.getName());
				current = root;
			}

		}
		return answer.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}


	public Map<Character, Integer> getFrequencies(String text){
		Map<Character, Integer> frequencies = new HashMap<>();
		for(char character: text.toCharArray()){
			if(frequencies.containsKey(character)){
				frequencies.put(character, frequencies.get(character) + 1);
			}
			else{
				frequencies.put(character, 1);
			}
		}
		return frequencies;
	}

	public void assignCode(Node root){
		codes = new HashMap<>();
		assignCodeHelper(root, "", codes);


	}

	public void assignCodeHelper(Node node, String code, Map<Character, String> codes){
			if(node == null){
				return;
			}
			if(node.getName() != '\0'){
				codes.put(node.getName(), code);
			}

			assignCodeHelper(node.getLeft(), code + "0", codes);
			assignCodeHelper(node.getRight(), code + "1", codes);
	}


}

class Node implements Comparable<Node>{
	private char name;
	private int freq;
	private Node left;
	private Node right;

	Node(char name, int freq, Node left, Node right){
		this.name = name;
		this.freq = freq;
		this.left = left;
		this.right = right;
	}

	public char getName(){
		return name;
	}
	public int getFreq(){
		return freq;
	}
	public Node getLeft(){
		return left;
	}
	public Node getRight(){
		return right;
	}

	@Override
	public int compareTo(Node o) {
		int freqDifference = freq - o.freq;

		if (freqDifference < 0) {
			return -1;
		}
		if (freqDifference > 0) {
			return 1;
		}
		char oName = o.getName();
		while(oName == '\0'){
			oName = o.getLeft().getName();
		}
		char thisName = this.getName();
		while(thisName == '\0'){
			thisName = this.getLeft().getName();
		}
		// Use the default comparison for other characters
		return Character.compare(thisName, oName);
	}
	public String toString(){
		return Character.toString(this.getName());
	}
}
