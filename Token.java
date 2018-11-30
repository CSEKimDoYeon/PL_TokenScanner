package lexer;

import java.util.HashMap;
import java.util.Map;

public class Token {
	private final TokenType type;
	private final String lexme;

	static Token ofName(String lexme) {
		TokenType type = KEYWORDS.get(lexme);
		if (type != null) {
			return new Token(type, lexme);
		} else if (lexme.endsWith("?")) { // 해당 lexme가 '?'로 끝났을경우 
			if (lexme.substring(0, lexme.length() - 1).contains("?")) {
				throw new ScannerException("invalid ID=" + lexme);
				// ID 중간에 '?'가 들어간 경우 invalid ID 출력.
			}
			return new Token(TokenType.QUESTION, lexme); 
			// 아닐 경우 QUESTION 타입 반환.
		} else if (lexme.contains("?")) { // lexme가 '?'를 포함할 경우
			throw new ScannerException("invalid ID=" + lexme);
			// invalid ID 출력.
		} else {
			return new Token(TokenType.ID, lexme);
		}
	}

	Token(TokenType type, String lexme) {
		this.type = type;
		this.lexme = lexme;
	}

	public TokenType type() {
		return this.type;
	}

	public String lexme() {
		return this.lexme;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", type, lexme);
	}

	private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
	static { // KEYWORD에 따라서 type이 일치하면 그에 상응하는 TokenType를 해시맵에 PUT 한다.
		KEYWORDS.put("define", TokenType.DEFINE);
		KEYWORDS.put("lambda", TokenType.LAMBDA);
		KEYWORDS.put("cond", TokenType.COND);
		KEYWORDS.put("quote", TokenType.QUOTE);
		KEYWORDS.put("not", TokenType.NOT);
		KEYWORDS.put("cdr", TokenType.CDR);
		KEYWORDS.put("car", TokenType.CAR);
		KEYWORDS.put("cons", TokenType.CONS);
		KEYWORDS.put("eq?", TokenType.EQ_Q);
		KEYWORDS.put("null?", TokenType.NULL_Q);
		KEYWORDS.put("atom?", TokenType.ATOM_Q);
	}
}
