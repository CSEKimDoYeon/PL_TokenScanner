package lexer;

class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	} // CharcterType�� ���������� ����.

	static Char of(char ch) { // ch��, Type�� ������ �ִ� Char ��ü ��ȯ.
		return new Char(ch, getType(ch));
	}

	static Char end() { // MIN_VALUE char, EOS Type�� ���� Char ��ü ��ȯ.
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}

	private Char(char ch, CharacterType type) { // Char ��ü ��ȯ.
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
		} // �ش� ch�� ���ĺ��� ��� CharacterTpye�� LETTER�� �����Ѵ�.

		if (Character.isDigit(ch)) {
			return CharacterType.DIGIT;
		} // �ش� ch�� ������ ��� characterType�� DIGIT�� �����Ѵ�.

		switch (ch) { // �ش� ch�� Ư�������� ����� case.
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
			// �ش� ch�� ������ ��쿡 CharacterType�� WS�� ����.
			return CharacterType.WS;
		}
		throw new IllegalArgumentException("input=" + ch);
	}
}
