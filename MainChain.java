import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class MainChain {
	ArrayList<Object[]> nameData = new ArrayList<Object[]>();
	HashMap<String, int[]> nextLetter = new HashMap<String, int[]>();
	HashMap<String, double[]> nextProb = new HashMap<String, double[]>();
	
	int[] firstLetters = new int[26*26];
	double[] firstProb = new double[26*26];
	
	HashMap<Character, int[]> lastLetter = new HashMap<Character, int[]>();
	HashMap<String, long[]> combined = new HashMap<String, long[]>();
	HashMap<String, double[]> combinedProb = new HashMap<String, double[]>();
	
	int[] lengths = new int[16];
	double[] lengthsProb = new double[16];
	
	private void parseData() throws NumberFormatException, IOException {
		File file = new File("allNames.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String currentLine;
		int index1, index2, index3;
		String name, gender;
		int year, births;
		while((currentLine = br.readLine()) != null) {
			index1 = currentLine.indexOf(',');
			index2 = currentLine.indexOf(',', index1+1);
			index3 = currentLine.indexOf(',', index2+1);
			
			year = Integer.parseInt(currentLine.substring(0, index1));
			name = currentLine.substring(index1+1, index2).toLowerCase();
			gender = currentLine.substring(index2+1, index3);
			births = Integer.parseInt(currentLine.substring(index3+1));
			
			nameData.add(new Object[]{year, name, gender, births});
		}
	}
	
	private int lettersToNum(char first, char second) {
		return (first-97)*26 + (second-97);
	}
	
	private boolean filter(Object[] nameRow, String gender) {
		if(gender.compareTo((String) nameRow[2]) == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	private void fillFirstLetters(String gender) {
		String name;
		char first, second;
		for(Object[] nameRow : nameData) {
			if(filter(nameRow, gender)) {
				continue;
			}
			name = (String) nameRow[1];
			first = name.charAt(0);
			second = name.charAt(1);
			firstLetters[lettersToNum(first, second)] += (int) nameRow[3];
		}
	}
	
	private void fillFirstProb() {
		int sum = 0;
	    for (int value : firstLetters) {
	        sum += value;
	    }
	    for(int i = 0; i < 26*26; i++) {
	    	firstProb[i] = (double) firstLetters[i] / (double) sum;
	    }
	}
	
	private String getFirstLetters() {
		double random = Math.random();
		double current = 0;
		
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				current += firstProb[(first-97)*26 + (second-97)];
				if(current > random) {
					return "" + first + second;
				}
			}
		}
		return "!!";
	}
	
	private void makeBaseMaps() {
		for(char first = 'a'; first <= 'z'; first++) {
			lastLetter.put(first, new int[26]);
			for(char second = 'a'; second <= 'z'; second++) {
				nextLetter.put(("" + first + second), new int[26]);
				nextProb.put(("" + first + second), new double[26]);
				combined.put(("" + first + second), new long[26]);
				combinedProb.put(("" + first + second), new double[26]);
			}
		}
	}
	
	private void createMap(String gender) {
		String name, letterPair;
		char current;
		for(Object[] nameRow : nameData) {
			if(filter(nameRow, gender)) {
				continue;
			}
			name = (String) nameRow[1];
			for(int i = 2; i < name.length()-1; i++) {
				letterPair = name.substring(i-2, i);
				current = name.charAt(i);
				nextLetter.get(letterPair)[current-97] += (int) nameRow[3];
			}
		}
	}
	
	private void fillNextProb() {
		int[] currentRow;
		double[] currentProbRow;
		int sum;
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				currentRow = nextLetter.get("" + first + second);
				currentProbRow = nextProb.get("" + first + second);
				sum = 0;
			    for (int value : currentRow) {
			        sum += value;
			    }
			    for(int i = 0; i < 26; i++) {
			    	currentProbRow[i] = (double) currentRow[i] / (double) sum;
			    }
			}
		}
	}
	
	private char getNextLetter(String prev) {
		double random = Math.random();
		double current = 0;
		
		double[] currentProbRow = nextProb.get(prev);
		for(int i = 0; i < 26; i++) {
			current += currentProbRow[i];
			if(current > random) {
				return (char)(i+97);
			}
		}
		return '!';
	}
	
	private void fillLastLetters(String gender) {
		String name;
		int length;
		char first, second;
		for(Object[] nameRow : nameData) {
			if(filter(nameRow, gender)) {
				continue;
			}
			name = (String) nameRow[1];
			length = name.length();
			first = name.charAt(length-2);
			second = name.charAt(length-1);
			lastLetter.get(first)[second-97] += (int) nameRow[3];
			lengths[length] += (int) nameRow[3];
		}
	}
	
	private void fillLengthsProb() {
		int sum = 0;
	    for (int value : lengths) {
	        sum += value;
	    }
	    for(int i = 0; i < 16; i++) {
	    	lengthsProb[i] = (double) lengths[i] / (double) sum;
	    }
	}
	
	private int getLength() {
		double random = Math.random();
		double current = 0;
		
		for(int i = 0; i < 16; i++) {
			current += lengthsProb[i];
			if(current > random) {
				return i;
			}
		}
		return 0;
	}
	
	private char getLastLetter(String prev) {
		double random = Math.random();
		double current = 0;
		double[] currentProbRow = combinedProb.get(prev);
		for(int i = 0; i < 26; i++) {
			current += currentProbRow[i];
			if(current > random) {
				return (char)(i+97);
			}
		}
		return '!';
	}
	
	private void fillCombinedLetters() {
		int[] currentRow, currentLastRow;
		long[] currentCombinedRow;
		int toAdd;
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				currentRow = nextLetter.get("" + first + second);
				currentLastRow = lastLetter.get(second);
				currentCombinedRow = combined.get("" + first + second);
				for(int i = 0; i < 26; i++) {
					currentCombinedRow[i] = (long) currentRow[i] * (long) currentLastRow[i];
				}
			}
		}
	}
	
	private void fillCombinedProb() {
		long[] currentRow;
		double[] currentProbRow;
		long sum;
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				currentRow = combined.get("" + first + second);
				currentProbRow = combinedProb.get("" + first + second);
				sum = 0;
			    for (long value : currentRow) {
			        sum += value;
			    }
			    for(int i = 0; i < 26; i++) {
			    	currentProbRow[i] = (double) currentRow[i] / (double) sum;
			    }
			}
		}
	}
	
	public void printFirstLetters() {
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				System.out.print("" + first + second);
				System.out.print(": ");
				System.out.println(firstProb[lettersToNum(first, second)]);
			}
		}
	}
		
	public void printNextLetters() {
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				System.out.print("" + first + second);
				System.out.print(": ");
				System.out.println(Arrays.toString(nextProb.get("" + first + second)));
			}
		}
	}
	
	public void printLastLetters() {
		for(char first = 'a'; first <= 'z'; first++) {
			for(char second = 'a'; second <= 'z'; second++) {
				System.out.print("" + first + second);
				System.out.print(": ");
				System.out.println(lastLetter.get(first)[second-97]);
			}
		}
	}

	public void initChain(String gender) {
		try {
			parseData();
		} catch(Exception e) { e.printStackTrace(); }
		fillFirstLetters(gender);
		fillFirstProb();
		makeBaseMaps();
		createMap(gender);
		fillNextProb();
		fillLastLetters(gender);
		fillLengthsProb();
		fillCombinedLetters();
		fillCombinedProb();
	}

	public String getName() {
		String newName = "";
		int length = getLength();
		while(length == 2) {
			length = getLength();
		}
		
		newName += getFirstLetters();
		for(int i = 2; i < length-1; i++) {
			newName += getNextLetter(newName.substring(i-2, i));
		}
		newName += getLastLetter(newName.substring(length-3, length-1));
		
		newName = newName.toUpperCase().charAt(0) + newName.substring(1);
		return newName;
	}

	
	public static void main(String[] args) {
		Scanner scnr = new Scanner(System.in);
		MainChain markov = new MainChain();
		
		int toGenerate;
		String gender;
		String prevGender = "";
		
		while(true) {
			outer:
			while(true) {
				System.out.print("Name gender (M/F): ");
				gender = scnr.nextLine();
				gender = gender.toUpperCase();
				switch(gender) {
				case "F":
					if(prevGender.equals("F")) {
						break outer;
					}
					prevGender = "F";
					markov = new MainChain();
					markov.initChain("F");
					break outer;
				case "M":
					if(prevGender.equals("M")) {
						break outer;
					}
					prevGender = "M";
					markov = new MainChain();
					markov.initChain("M");
					break outer;
				}
			}
			while(true) {
				try {
					System.out.print("Names to generate: ");
					toGenerate = Integer.parseInt(scnr.nextLine());
					if(toGenerate > 0) {
						break;
					}
				} catch (Exception e) {}
			}
			System.out.println();
			for(int i = 0; i < toGenerate; i++) {
				System.out.println(markov.getName());
			}
			System.out.println();
		}
	}
}