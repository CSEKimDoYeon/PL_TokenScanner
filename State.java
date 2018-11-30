package lexer;

import static lexer.TokenType.fromSpecialCharactor;
import static lexer.TokenType.ID;
import static lexer.TokenType.INT;
import static lexer.TransitionOutput.GOTO_ACCEPT_ID;
import static lexer.TransitionOutput.GOTO_ACCEPT_INT;
import static lexer.TransitionOutput.GOTO_EOS;
import static lexer.TransitionOutput.GOTO_FAILED;
import static lexer.TransitionOutput.GOTO_MATCHED;
import static lexer.TransitionOutput.GOTO_SIGN;
import static lexer.TransitionOutput.GOTO_START;
import static lexer.TransitionOutput.GOTO_SHARP;

enum State {
	START { // 초기 state에서 시작.
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			// context의 다음 Char 객체를 읽는다.
			char v = ch.value(); // Char 객체의 value를 추출한다.
			switch (ch.type()) { // ch의 타입에 따라 case를 나눈다.
			case LETTER: // ch가 letter 일 경우.
				context.append(v); // v를 append하여 붙히고
				return GOTO_ACCEPT_ID; // ACCEPT_ID state로 넘어간다.
			case DIGIT: // ch가 숫자일 경우.
				context.append(v); // v를 append하여 붙히고
				return GOTO_ACCEPT_INT; // ACCEPT_INT state로 넘어간다.
			case SPECIAL_CHAR: // ch가 특수문자일 경우.
				if (v == '#') { // 특수문자가 '#' 일 경우.
					context.append(v); // v를 append 하고
					return GOTO_SHARP; // SHARP state로 넘어간다.
				} else {
					context.append(v);
					return GOTO_SIGN;
					// 만약 특수문자가 '#'이 아닐 경우 SIGN state로 넘어간다.
				}
			case WS: // ch가 공백일경우.
				return GOTO_START; // START state로 넘어간다.
			case END_OF_STREAM: // Stream이 끝났을 경우
				return GOTO_EOS; // EOS state로 넘어간다.
			default:
				throw new AssertionError();
			}
		}
	},
	ACCEPT_ID { // ACCEPT_ID state
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch (ch.type()) {
			case LETTER:
			case DIGIT: // ch가 숫자인 경우
				context.append(v); // append 한 후에 (ex. a1b)
				return GOTO_ACCEPT_ID; // 다시 ACCEPT_ID state를 시작한다.
			case SPECIAL_CHAR:
				if (v == '?') { // letter가 진행되다가 '?' 를 만나면.
					context.append(v);
					String temp1 = context.getLexime();
					if (temp1.equals("null?")) {
						return GOTO_MATCHED(TokenType.NULL_Q, temp1);
						// null? 이 완성되었을 경우 NULL_Q 타입 반환.
					} else if (temp1.equals("eq?")) {
						return GOTO_MATCHED(TokenType.EQ_Q, temp1);
						// eq? 가 완성되었을 경우 EQ_Q 타입 반환.
					} else if (temp1.equals("atom?")) {
						return GOTO_MATCHED(TokenType.ATOM_Q, temp1);
						// atom? 이 완성되었을 경우 ATOM_Q 타입 반환.
					} else
						return GOTO_MATCHED(TokenType.QUESTION, temp1);
					// 이외의 ID + '?' 가 완성되었을 경우 QUESTION 타입 반환.
				} else // 어떠한 경우도 아닐 경우 FAIL
					return GOTO_FAILED;
			case WS:

			case END_OF_STREAM:
				String temp = context.getLexime();
				/*
				 * Stream이 끝났을 때 String을 검사하여 일치하는 String에 따라서 Token의 Type을 반환한다.
				 */
				if (temp.equals("define")) {
					return GOTO_MATCHED(TokenType.DEFINE, temp);
				} else if (temp.equals("lambda")) {
					return GOTO_MATCHED(TokenType.LAMBDA, temp);
				} else if (temp.equals("cond")) {
					return GOTO_MATCHED(TokenType.COND, temp);
				} else if (temp.equals("quote")) {
					return GOTO_MATCHED(TokenType.QUOTE, temp);
				} else if (temp.equals("not")) {
					return GOTO_MATCHED(TokenType.NOT, temp);
				} else if (temp.equals("cdr")) {
					return GOTO_MATCHED(TokenType.CDR, temp);
				} else if (temp.equals("car")) {
					return GOTO_MATCHED(TokenType.CAR, temp);
				} else if (temp.equals("cons")) {
					return GOTO_MATCHED(TokenType.CONS, temp);
				} else
					return GOTO_MATCHED(ID, temp);
			default:
				throw new AssertionError();
			}
		}
	},
	ACCEPT_INT {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			switch (ch.type()) {
			case LETTER: // 숫자 다음에 문자가 왔을때는 FAIL state.
				return GOTO_FAILED;
			case DIGIT: // 숫자가 들어왔을 경우에는 append 하고 ACCEPT_INT로 돌아온다.
				context.append(ch.value());
				return GOTO_ACCEPT_INT;
			case SPECIAL_CHAR: // 특수문자가 들어왔을 경우에는 FAIL.
				return GOTO_FAILED;
			case WS:

			case END_OF_STREAM: // Stream이 끝났을 경우 해당 숫자열을 반환.
				return GOTO_MATCHED(INT, context.getLexime());
			default:
				throw new AssertionError();
			}
		}
	},
	SIGN {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();

			switch (ch.type()) {
			case LETTER: // 문자가 들어왔을 경우에는 FAILED state.
				return GOTO_FAILED;
			case DIGIT: // 숫자가 들어왔을 경우에는 append
				context.append(v);
				return GOTO_ACCEPT_INT;
			case WS:
			case END_OF_STREAM:
				String temp = context.getLexime();
				char tempChar = temp.charAt(0);

				return GOTO_MATCHED(fromSpecialCharactor(tempChar), temp);
			// tempChar에 해당하는 SPECIAL_CHAR Type를 반환한다.
			default:
				throw new AssertionError();
			}
		}
	},
	SHARP { // '#' 을 만났을 경우 따로 만들어준 SHARP state에서 시작한다.
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch (ch.type()) {
			case LETTER:
				context.append(v);
				if (v == 'T') {
					String temp = context.getLexime(); // #T 일 경우 TRUE
					return GOTO_MATCHED(TokenType.TRUE, temp);
				} else if (v == 'F') {
					String temp = context.getLexime(); // #F 일 경우 FALSE
					return GOTO_MATCHED(TokenType.FALSE, temp);
				} else
					return GOTO_FAILED;
			case DIGIT:
				return GOTO_FAILED;
			case SPECIAL_CHAR:
				return GOTO_FAILED;
			case WS:
				// return GOTO_START;
			case END_OF_STREAM:
				return GOTO_MATCHED(TokenType.Sharp, context.getLexime());
			default:
				throw new AssertionError();
			}
		}
	},
	MATCHED { // MATCHED state 정의.
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED { // FAILED state 정의.
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS { // End of Stream state 정의.
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};

	abstract TransitionOutput transit(ScanContext context);
}
