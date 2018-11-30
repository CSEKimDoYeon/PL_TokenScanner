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
		State current = State.START; // �ʱ� ���´� START
		while (true) {
			TransitionOutput output = current.transit(context);
			// �ʱ⿡ enum Start���� trnsit(context) ����.
			// start���¿��� transit �����ϸ� WS �ɷ��� GOTO_START
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
