package lexer;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

class TokenIterator implements Iterator<Token> {
	private final ScanContext context;
	private Optional<Token> nextToken;

	TokenIterator(ScanContext context) {
		this.context = context;
		nextToken = readToNextToken(context);
	}

	@Override
	public boolean hasNext() {
		return nextToken.isPresent();
	}

	@Override
	public Token next() {
		if (!nextToken.isPresent()) {
			throw new NoSuchElementException();
		}

		Token token = nextToken.get();
		nextToken = readToNextToken(context);

		return token;
	}

	private Optional<Token> readToNextToken(ScanContext context) {
		State current = State.START; // 초기 상태는 START
		while (true) {
			TransitionOutput output = current.transit(context);
			// 초기에 enum Start에서 trnsit(context) 실행.
			// start상태에서 transit 실행하면 WS 걸려서 GOTO_START
			if (output.nextState() == State.MATCHED) {
				return output.token();
			} else if (output.nextState() == State.FAILED) {
				throw new ScannerException();
			} else if (output.nextState() == State.EOS) {
				return Optional.empty();
			}

			current = output.nextState();
		}
	}
}
