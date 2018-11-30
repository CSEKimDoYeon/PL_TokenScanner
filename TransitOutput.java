package lexer;

import java.util.Optional;

class TransitionOutput {
	private final State nextState;
	private final Optional<Token> token;
	/* 각 State가 어떠한 TransitionOutput 객체를 만드는지를 정의한다. */
	static TransitionOutput GOTO_START = new TransitionOutput(State.START);
	static TransitionOutput GOTO_ACCEPT_ID = new TransitionOutput(State.ACCEPT_ID);
	static TransitionOutput GOTO_ACCEPT_INT = new TransitionOutput(State.ACCEPT_INT);
	static TransitionOutput GOTO_SIGN = new TransitionOutput(State.SIGN);
	static TransitionOutput GOTO_FAILED = new TransitionOutput(State.FAILED);
	static TransitionOutput GOTO_EOS = new TransitionOutput(State.EOS);
	static TransitionOutput GOTO_SHARP = new TransitionOutput(State.SHARP);

	static TransitionOutput GOTO_MATCHED(TokenType type, String lexime) {
		return new TransitionOutput(State.MATCHED, new Token(type, lexime));
	}

	static TransitionOutput GOTO_MATCHED(Token token) {
		return new TransitionOutput(State.MATCHED, token);
	}
	// MATCHED 일 경우에는 해당 state와 토큰을 반환한다.

	TransitionOutput(State nextState, Token token) {
		this.nextState = nextState;
		this.token = Optional.of(token);
	}

	TransitionOutput(State nextState) {
		this.nextState = nextState;
		this.token = Optional.empty();
	}

	State nextState() { // 다음 상태를 반환한다.
		return this.nextState;
	}

	Optional<Token> token() { // Token을 Type으로 가지는 Optional 메소드. 토큰 반환.
		return this.token;
	}
}