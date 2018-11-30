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
	START { // �ʱ� state���� ����.
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			// context�� ���� Char ��ü�� �д´�.
			char v = ch.value(); // Char ��ü�� value�� �����Ѵ�.
			switch (ch.type()) { // ch�� Ÿ�Կ� ���� case�� ������.
			case LETTER: // ch�� letter �� ���.
				context.append(v); // v�� append�Ͽ� ������
				return GOTO_ACCEPT_ID; // ACCEPT_ID state�� �Ѿ��.
			case DIGIT: // ch�� ������ ���.
				context.append(v); // v�� append�Ͽ� ������
				return GOTO_ACCEPT_INT; // ACCEPT_INT state�� �Ѿ��.
			case SPECIAL_CHAR: // ch�� Ư�������� ���.
				if (v == '#') { // Ư�����ڰ� '#' �� ���.
					context.append(v); // v�� append �ϰ�
					return GOTO_SHARP; // SHARP state�� �Ѿ��.
				} else {
					context.append(v);
					return GOTO_SIGN;
					// ���� Ư�����ڰ� '#'�� �ƴ� ��� SIGN state�� �Ѿ��.
				}
			case WS: // ch�� �����ϰ��.
				return GOTO_START; // START state�� �Ѿ��.
			case END_OF_STREAM: // Stream�� ������ ���
				return GOTO_EOS; // EOS state�� �Ѿ��.
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
			case DIGIT: // ch�� ������ ���
				context.append(v); // append �� �Ŀ� (ex. a1b)
				return GOTO_ACCEPT_ID; // �ٽ� ACCEPT_ID state�� �����Ѵ�.
			case SPECIAL_CHAR:
				if (v == '?') { // letter�� ����Ǵٰ� '?' �� ������.
					context.append(v);
					String temp1 = context.getLexime();
					if (temp1.equals("null?")) {
						return GOTO_MATCHED(TokenType.NULL_Q, temp1);
						// null? �� �ϼ��Ǿ��� ��� NULL_Q Ÿ�� ��ȯ.
					} else if (temp1.equals("eq?")) {
						return GOTO_MATCHED(TokenType.EQ_Q, temp1);
						// eq? �� �ϼ��Ǿ��� ��� EQ_Q Ÿ�� ��ȯ.
					} else if (temp1.equals("atom?")) {
						return GOTO_MATCHED(TokenType.ATOM_Q, temp1);
						// atom? �� �ϼ��Ǿ��� ��� ATOM_Q Ÿ�� ��ȯ.
					} else
						return GOTO_MATCHED(TokenType.QUESTION, temp1);
					// �̿��� ID + '?' �� �ϼ��Ǿ��� ��� QUESTION Ÿ�� ��ȯ.
				} else // ��� ��쵵 �ƴ� ��� FAIL
					return GOTO_FAILED;
			case WS:

			case END_OF_STREAM:
				String temp = context.getLexime();
				/*
				 * Stream�� ������ �� String�� �˻��Ͽ� ��ġ�ϴ� String�� ���� Token�� Type�� ��ȯ�Ѵ�.
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
			case LETTER: // ���� ������ ���ڰ� �������� FAIL state.
				return GOTO_FAILED;
			case DIGIT: // ���ڰ� ������ ��쿡�� append �ϰ� ACCEPT_INT�� ���ƿ´�.
				context.append(ch.value());
				return GOTO_ACCEPT_INT;
			case SPECIAL_CHAR: // Ư�����ڰ� ������ ��쿡�� FAIL.
				return GOTO_FAILED;
			case WS:

			case END_OF_STREAM: // Stream�� ������ ��� �ش� ���ڿ��� ��ȯ.
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
			case LETTER: // ���ڰ� ������ ��쿡�� FAILED state.
				return GOTO_FAILED;
			case DIGIT: // ���ڰ� ������ ��쿡�� append
				context.append(v);
				return GOTO_ACCEPT_INT;
			case WS:
			case END_OF_STREAM:
				String temp = context.getLexime();
				char tempChar = temp.charAt(0);

				return GOTO_MATCHED(fromSpecialCharactor(tempChar), temp);
			// tempChar�� �ش��ϴ� SPECIAL_CHAR Type�� ��ȯ�Ѵ�.
			default:
				throw new AssertionError();
			}
		}
	},
	SHARP { // '#' �� ������ ��� ���� ������� SHARP state���� �����Ѵ�.
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch (ch.type()) {
			case LETTER:
				context.append(v);
				if (v == 'T') {
					String temp = context.getLexime(); // #T �� ��� TRUE
					return GOTO_MATCHED(TokenType.TRUE, temp);
				} else if (v == 'F') {
					String temp = context.getLexime(); // #F �� ��� FALSE
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
	MATCHED { // MATCHED state ����.
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED { // FAILED state ����.
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS { // End of Stream state ����.
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};

	abstract TransitionOutput transit(ScanContext context);
}
