package lexer;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.stream.Stream;

public class ScannerMain {
	public static final void main(String... args) throws Exception {
		ClassLoader cloader = ScannerMain.class.getClassLoader();
		File file = new File("C:\\Users\\KimDoYeon\\Desktop\\as03.txt");
		// File file = new File(cloader.getResource("lexer/as03.txt").getFile());
		System.out.println(file.getAbsolutePath()); // 파일의 Path를 출력.

		testTokenStream(file); // 입력받은 파일을 Token화 시작.
	}

	// use tokens as a Stream
	private static void testTokenStream(File file) throws FileNotFoundException {
		Stream<Token> tokens = Scanner.stream(file);
		tokens.map(ScannerMain::toString).forEach(System.out::println);
	}

	private static String toString(Token token) {
		return String.format("%-3s: %s", token.type().name(), token.lexme());
	}

}
