package lexer;

class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	} // CharcterType을 열겨형으로 정의.

	static Char of(char ch) { // ch값, Type를 가지고 있는 Char 객체 반환.
		return new Char(ch, getType(ch));
	}

	static Char end() { // MIN_VALUE char, EOS Type을 가진 Char 객체 반환.
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}

	private Char(char ch, CharacterType type) { // Char 객체 반환.
		this.value = ch;
		this.type = type;
	}

	char value() {
		return this.value;
	}

	CharacterType type() {
		return this.type;
	}

	private static CharacterType getType(char ch) {
		int code = (int) ch;
		if ((code >= (int) 'A' && code <= (int) 'Z') || (code >= (int) 'a' && code <= (int) 'z')) {
			return CharacterType.LETTER;
		} // 해당 ch가 알파벳일 경우 CharacterTpye를 LETTER로 설정한다.

		if (Character.isDigit(ch)) {
			return CharacterType.DIGIT;
		} // 해당 ch가 숫자일 경우 characterType를 DIGIT로 설정한다.

		switch (ch) { // 해당 ch가 특수문자일 경우의 case.
		case '-':
			return CharacterType.SPECIAL_CHAR;
		case '(':
			return CharacterType.SPECIAL_CHAR;
		case ')':
			return CharacterType.SPECIAL_CHAR;
		case '+':
			return CharacterType.SPECIAL_CHAR;
		case '*':
			return CharacterType.SPECIAL_CHAR;
		case '/':
			return CharacterType.SPECIAL_CHAR;
		case '<':
			return CharacterType.SPECIAL_CHAR;
		case '=':
			return CharacterType.SPECIAL_CHAR;
		case '>':
			return CharacterType.SPECIAL_CHAR;
		case '\'':
			return CharacterType.SPECIAL_CHAR;
		case '#':
			return CharacterType.SPECIAL_CHAR;
		case '?':
			return CharacterType.SPECIAL_CHAR;

		}

		if (Character.isWhitespace(ch)) { 
			// 해당 ch가 공백일 경우에 CharacterType를 WS로 설정.
			return CharacterType.WS;
		}
		throw new IllegalArgumentException("input=" + ch);
	}
}
