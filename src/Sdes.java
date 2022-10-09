import java.util.ArrayList;
import java.util.Arrays;

public class Sdes {
	private char[] text = {'a', 'l', 'e', 'k', 's', 'a', 'n', 'd', 'r'};
	private static final ArrayList<Integer> initialKey = new ArrayList<Integer> (Arrays.asList(1, 0, 1, 0, 0, 0, 0, 0, 1, 0));
	private static final String initialKeyString = "1010000010";
	private static final ArrayList<Integer> p10 = new ArrayList<Integer> (Arrays.asList(2, 4, 1, 6, 3, 9, 0, 8, 7, 5));
	private static final ArrayList<Integer> p8 = new ArrayList<Integer> (Arrays.asList(5, 2, 6, 3, 7, 4, 9, 8));
	private static final ArrayList<Integer> p4 = new ArrayList<Integer> (Arrays.asList(1, 3, 2, 0));

	private static final ArrayList<Integer> ip8 = new ArrayList<Integer> (Arrays.asList(1, 5, 2, 0, 3, 7, 4, 6));
	private static final ArrayList<Integer> iip = new ArrayList<Integer> (Arrays.asList(3, 0, 2, 4, 6, 1, 7, 5));
	private static final ArrayList<Integer> expandBits = new ArrayList<Integer> (Arrays.asList(3, 0, 1, 2, 1, 2, 3, 0));

	private static final String[][] s0 = {
			{"01", "00", "11", "10"},
			{"11", "10", "01", "00"},
			{"00", "10", "01", "11"},
			{"11", "01", "11", "10"}
	};

	private static final String[][] s1 = {
			{"00", "01", "10", "11"},
			{"10", "00", "01", "11"},
			{"11", "00", "01", "00"},
			{"10", "01", "00", "11"}
	};

	private static final String initialText = "TEXT";

	public static void encrypt() {
		String key = permute(initialKeyString, p10);
		String shiftedKey = separateShiftAndCombine(key);
		String key1 = permute(shiftedKey, p8);
		String key2 = permute(shiftTwice(shiftedKey), p8);

		//Step 1. Perform initial permutation
		String initiallyPermutatedText = performInitialPermutation("01000001");
		//Step2. Expand the right half of the permuted value
		String expandedRightHalf = expandAndPermutate(initiallyPermutatedText.substring(initiallyPermutatedText.length() / 2));
		//xor the expanded value with key 1
		String xorOfExpandedRightAndKey1 = performXor(expandedRightHalf, key1);
		String changedThroughSBoxes = putTextIntoSBox(xorOfExpandedRightAndKey1);
		String permutatedBoxes = permute(changedThroughSBoxes, p4);
		//XOR on permutated boxes and left half of initially permuted text
		String a = performXor(permutatedBoxes, initiallyPermutatedText.substring(0, initiallyPermutatedText.length() / 2));
		//combine all that with the right part of initially permutated text
		String afterFirstCycle = initiallyPermutatedText.substring(initiallyPermutatedText.length() / 2) + a;
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//Step 3
		String expandedA = expandAndPermutate(a);
		String xorOfAAndKey2 = performXor(expandedA, key2);
		String boxes2 = putTextIntoSBox(xorOfAAndKey2);
		String permutedBoxes2 = permute(boxes2, p4);
		String a2 = performXor(permutedBoxes2, afterFirstCycle.substring(0, afterFirstCycle.length() / 2));
		String res = permute(a2 + a, iip);
		System.out.println("Value encrypted with S-DES: " + res);
	}

	private static String performXor(String param1, String param2) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < param1.length(); i++) {
			boolean a = param1.charAt(i) == '1';
			boolean b = param2.charAt(i) == '1';
			if (a ^ b) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}
		return sb.toString();
	}

	private static void getKeys() {
		String key = permute(initialKeyString, p10);
		String shiftedKey = separateShiftAndCombine(key);
		String key1 = permute(shiftedKey, p8);
		String key2 = permute(shiftTwice(shiftedKey), p8);
	}

	private static ArrayList<Integer> permutation(ArrayList<Integer> key, ArrayList<Integer> permutationTable) {
		ArrayList<Integer> permutedKey = new ArrayList<>();
		for (Integer k : permutationTable) {
			permutedKey.add(key.get(k));
		}
		return permutedKey;
	}

	private static String permute(String key, ArrayList<Integer> permutationTable) {
		StringBuilder permutatedKey = new StringBuilder();
		for (Integer k : permutationTable) {
			permutatedKey.append(key.charAt(k));
		}
		return permutatedKey.toString();
	}

	private static String shiftTwice(String key) {
		String shifted = separateShiftAndCombine(key);
		return separateShiftAndCombine(shifted);
	}

	private static String separateShiftAndCombine(String key) {
		String left = performLeftShift(key.substring(0, key.length() / 2));
		String right = performLeftShift(key.substring(5));
		return left + right;
	}

	private static String performLeftShift(String half) {
		StringBuilder shifted = new StringBuilder();
		for (int i = 1; i < half.length(); i++) {
			shifted.append(half.charAt(i));
		}
		shifted.append(half.charAt(0));
		return shifted.toString();
	}

	private static String performInitialPermutation(String text) {
		StringBuilder permutatedKey = new StringBuilder();
		for (Integer k : ip8) {
			permutatedKey.append(text.charAt(k));
		}
		return permutatedKey.toString();
	}

	private static String expandAndPermutate(String text) {
		StringBuilder sb = new StringBuilder();
		for (Integer k : expandBits) {
			sb.append(text.charAt(k));
		}
		return sb.toString();
	}

	private static String putTextIntoSBox(String text) {
		String left = text.substring(0, text.length() / 2);
		int row1 = binaryToDec(left.charAt(0) + "" + left.charAt(3));
		int col1 = binaryToDec(left.charAt(1) + "" + left.charAt(2));


		String right = text.substring(text.length() / 2);
		int row2 = binaryToDec(right.charAt(0) + "" + right.charAt(3));
		int col2 = binaryToDec(right.charAt(1) + "" + right.charAt(2));

		return s0[row1][col1] + s1[row2][col2];
	}

	private static int binaryToDec(String binary) {
		switch (binary) {
			case "00":
				return  0;
			case "01":
				return  1;
			case "10":
				return 2;
			default:
				return 3;
		}
	}
}
