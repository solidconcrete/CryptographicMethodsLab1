public class main {
	public static void main(String[] args) {
		long B = 66666666;
		System.out.println("BIN: " + convert(B, 2));
		System.out.println("OCT: " + convert(B, 8));
		System.out.println("HEX: " + convert(B, 16));
		System.out.println("Base58: " + convert(B, 58));
		System.out.println("Base64: " + convert(B, 64));

		Sdes.encrypt();
		// System.out.println("Bits changed: " + countDifferences("1111011000010010110010010100010010100110001010011000110110010011", "1101010111001101000001100000000101010110110000110101001000111000"));
	}

	public static String convert(long B, int base) {
		StringBuilder result = new StringBuilder();

		while (B != 0) {
			result.append(convertToLetterIfNeeded(B % base));
			// result.append(remainder % base);
			B = B / base;
		}

		if (base == 2 && result.length() % 4 != 0) {
			while (result.length() % 4 != 0) {
				result.append("0");
			}
		}
		result.reverse();

		// short separatorAt;
		// switch (base) {
		// 	case 2:
		// 	case 16:
		// 		separatorAt = 4;
		// 		break;
		// 	case 8:
		// 		separatorAt = 3;
		// 		break;
		// 	default: separatorAt = 0;
		// }
		//
		// if (separatorAt != 0) {
		// 	int whitespaces = 0;
		// 	for (int pos = separatorAt;  result.length() - pos > separatorAt; pos += separatorAt) {
		// 		result.insert(pos + whitespaces, " ");
		// 		whitespaces++;
		// 	}
		// }
		return result.toString();
	}

	private static char convertToLetterIfNeeded(long remainder) {
		if (remainder < 10) {
			return (char) (remainder + '0');
		} else {
			if (remainder < 35) {
				return (char) (remainder + 55);
			} else {
				return (char) (remainder + 61);
			}
		}
	}

	private static int countDifferences(String a, String b) {
		int res = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				res++;
			}
		}
		return res;
	}
}
