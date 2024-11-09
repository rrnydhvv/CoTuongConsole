package ChineseChess;

import java.util.*;

public class CChessBoard {
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String CYAN = "\u001B[36m";
	final static int rows = 10;
	final static int cols = 9;
	ArrayList<String> moveHistory = new ArrayList<String>();
	Set<Piece> pieces = new HashSet<Piece>();

	CChessBoard() {
		pieces.add(new Piece(0, 4, true, Rank.KING));
		pieces.add(new Piece(9, 4, false, Rank.KING));
		for (int i = 0; i < 5; ++i) {
			pieces.add(new Piece(3, i * 2, true, Rank.PAWN));
			pieces.add(new Piece(6, i * 2, false, Rank.PAWN));
		}
		for (int i = 0; i < 2; ++i) {
			pieces.add(new Piece(0, i * 8, true, Rank.ROOK));
			pieces.add(new Piece(0, 1 + i * 6, true, Rank.KNIGHT));
			pieces.add(new Piece(0, 2 + i * 4, true, Rank.BISHOP));
			pieces.add(new Piece(0, 3 + i * 2, true, Rank.GUARD));
			pieces.add(new Piece(2, 1 + i * 6, true, Rank.CANNON));

			pieces.add(new Piece(9, i * 8, false, Rank.ROOK));
			pieces.add(new Piece(9, 1 + i * 6, false, Rank.KNIGHT));
			pieces.add(new Piece(9, 2 + i * 4, false, Rank.BISHOP));
			pieces.add(new Piece(9, 3 + i * 2, false, Rank.GUARD));
			pieces.add(new Piece(7, 1 + i * 6, false, Rank.CANNON));
		}
		recordMove(toString());
	}

	void printBoard() {
		System.out.println(toString());
	}

	public String toString() {
		String Mark = "RNBGKCP";
		String brdStr = " ";
		for (int i = 0; i < cols; ++i)
			brdStr += " " + i;
		brdStr += '\n';
		for (int row = 0; row < rows; ++row) {
			brdStr += row + "";
			for (int col = 0; col < cols; ++col) {
				Piece p = pieceAt(row, col);
				brdStr += " ";
				if (p == null) {
					if (row == 4 || row == 5)
						brdStr += "-";
					else
						brdStr += ".";
				} else if (p.isRed)
					brdStr += RED + Mark.charAt(p.rankToInt()) + RESET;
				else
					brdStr += CYAN + Mark.charAt(p.rankToInt()) + RESET;
			}
			brdStr += '\n';
		}
		return brdStr;
	}

	void displayMoveHistory() {
		System.out.println("Move History:");
		for (int i = 0; i < moveHistory.size(); i++) {
			System.out.println("Move " + i + ":");
			System.out.println(this.moveHistory.get(i));
		}
	}

	void recordMove(String brd) {
		try {
			moveHistory.add(brd);
		} catch (NullPointerException e) {
			return;
		}
	}

	void setPieceAt(int row, int col, Piece piece) {
		removePieceAt(row, col);
		if (piece != null) {
			piece.row = row;
			piece.col = col;
			pieces.add(piece);
		}
	}

	void removePieceAt(int row, int col) {
		Piece toRemove = pieceAt(row, col);
		if (toRemove != null) {
			pieces.remove(toRemove);
		}
	}

	Piece pieceAt(int row, int col) {
		for (Piece p : pieces)
			if (p.row == row && p.col == col)
				return p;
		return null;
	}

	void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
		Piece movingP = pieceAt(fromRow, fromCol);
		Piece targetP = pieceAt(toRow, toCol);
		pieces.remove(movingP);
		pieces.remove(targetP);
		pieces.add(new Piece(toRow, toCol, movingP.isRed, movingP.rank));
		recordMove(toString());
	}

	private boolean outOfBoard(int row, int col) {
		return col < 0 || col > 8 || row < 0 || row > 9;
	}

	private boolean isStraight(int fromRow, int fromCol, int toRow, int toCol) {
		return fromCol == toCol || fromRow == toRow;
	}

	private boolean isDiagonal(int fromRow, int fromCol, int toRow, int toCol) {
		return Math.abs(fromCol - toCol) == Math.abs(fromRow - toRow);
	}

	private int steps(int fromRow, int fromCol, int toRow, int toCol) {
		if (fromCol == toCol) {
			return Math.abs(fromRow - toRow);
		} else if (fromRow == toRow) {
			return Math.abs(fromCol - toCol);
		} else if (isDiagonal(fromRow, fromCol, toRow, toCol)) {
			return Math.abs(fromRow - toRow);
		}
		return 0;
	}

	private boolean outOfPalace(int row, int col, boolean isRed) {
		if (isRed) {
			return col < 3 || col > 5 || row < 0 || row > 2;
		} else {
			return col < 3 || col > 5 || row < 7 || row > 9;
		}
	}

	private boolean selfSide(int row, boolean isRed) {
		return isRed ? row <= 4 : row >= 5;
	}

	private int numPiecesBetween(int fromRow, int fromCol, int toRow, int toCol) {
		if (!isStraight(fromRow, fromCol, toRow, toCol) || steps(fromRow, fromCol, toRow, toCol) < 2) {
			return 0;
		}
		int cnt = 0, head, tail;
		if (fromCol == toCol) { // vertical
			head = Math.min(fromRow, toRow);
			tail = Math.max(fromRow, toRow);
			for (int row = head + 1; row < tail; row++) {
				cnt += (pieceAt(row, fromCol) == null) ? 0 : 1;
			}
		} else {
			head = Math.min(fromCol, toCol);
			tail = Math.max(fromCol, toCol);
			for (int col = head + 1; col < tail; col++) {
				cnt += (pieceAt(fromRow, col) == null) ? 0 : 1;
			}
		}
		return cnt;
	}

	private boolean selfKilling(int fromRow, int fromCol, int toRow, int toCol, boolean isRed) {
		Piece target = pieceAt(toRow, toCol);
		return target != null && target.isRed == isRed;
	}

	private boolean isValidGuardMove(int fromRow, int fromCol, int toRow, int toCol, boolean isRed) {
		if (outOfPalace(toRow, toCol, isRed)) {
			return false;
		}
		return isDiagonal(fromRow, fromCol, toRow, toCol) && steps(fromRow, fromCol, toRow, toCol) == 1;
	}

	private boolean isValidKingMove(int fromRow, int fromCol, int toRow, int toCol, boolean isRed) {
		if (outOfPalace(toRow, toCol, isRed)) {
			return false;
		}
		return isStraight(fromRow, fromCol, toRow, toCol) && steps(fromRow, fromCol, toRow, toCol) == 1;
	}

	private boolean isValidKnightMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (Math.abs(fromCol - toCol) == 1 && Math.abs(fromRow - toRow) == 2) {
			return pieceAt((fromRow + toRow) / 2, fromCol) == null;
		} else if (Math.abs(fromCol - toCol) == 2 && Math.abs(fromRow - toRow) == 1) {
			return pieceAt(fromRow, (fromCol + toCol) / 2) == null;
		}
		return false;
	}

	private boolean isValidBishopMove(int fromRow, int fromCol, int toRow, int toCol, boolean isRed) {
		return selfSide(toRow, isRed) && pieceAt((fromRow + toRow) / 2, (fromCol + toCol) / 2) == null
				&& isDiagonal(fromRow, fromCol, toRow, toCol) && steps(fromRow, fromCol, toRow, toCol) == 2;
	}

	private boolean isValidRookMove(int fromRow, int fromCol, int toRow, int toCol) {
		return isStraight(fromRow, fromCol, toRow, toCol) && numPiecesBetween(fromRow, fromCol, toRow, toCol) == 0;
	}

	private boolean isValidCannonMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (pieceAt(toRow, toCol) == null) {
			return isValidRookMove(fromRow, fromCol, toRow, toCol);
		}
		return numPiecesBetween(fromRow, fromCol, toRow, toCol) == 1;
	}

	private boolean isValidPawnMove(int fromRow, int fromCol, int toRow, int toCol, boolean isRed) {
		if (steps(fromRow, fromCol, toRow, toCol) != 1)
			return false;
		if (!isStraight(fromRow, fromCol, toRow, toCol))
			return false;
		return isRed && toRow > fromRow || !isRed && toRow < fromRow || !selfSide(fromRow, isRed);
	}

	boolean isValidMoveByType(int fromR, int fromC, int toR, int toC) {
		if (fromR == toR && fromC == toC || outOfBoard(toR, toC)) {
			return false;
		}
		Piece p = pieceAt(fromR, fromC);
		if (p == null || selfKilling(fromR, fromC, toR, toC, p.isRed)) {
			return false;
		}
		boolean ok = false;
		switch (p.rank) {
		case GUARD:
			ok = isValidGuardMove(fromR, fromC, toR, toC, p.isRed);
			break;
		case KING:
			ok = isValidKingMove(fromR, fromC, toR, toC, p.isRed);
			break;
		case BISHOP:
			ok = isValidBishopMove(fromR, fromC, toR, toC, p.isRed);
			break;
		case KNIGHT:
			ok = isValidKnightMove(fromR, fromC, toR, toC);
			break;
		case ROOK:
			ok = isValidRookMove(fromR, fromC, toR, toC);
			break;
		case CANNON:
			ok = isValidCannonMove(fromR, fromC, toR, toC);
			break;
		case PAWN:
			ok = isValidPawnMove(fromR, fromC, toR, toC, p.isRed);
			break;
		}
		return ok;
	}

	boolean isUnderCheck(int row, int col, boolean isRed) {
		for (Piece p : pieces)
			if (p.isRed != isRed)
				if (isValidMoveByType(p.row, p.col, row, col))
					return true;
		return false;
	}

	boolean isInSameColumnWithEnemyKing(int row, int col, boolean isRed) {
		Piece enemyKing = findKing(!isRed);
		if (enemyKing == null)
			return false;
		boolean sameCol = (enemyKing.col == col);
		boolean isBlock = this.numPiecesBetween(row, col, enemyKing.row, enemyKing.col) > 0;
		return sameCol && !isBlock;
	}

	Piece findKing(boolean isRed) {
		for (Piece p : pieces)
			if (p.rank == Rank.KING && p.isRed == isRed)
				return p;
		return null;
	}

	boolean isValidAfterMove(int fromRow, int fromCol, int toRow, int toCol) {
		Piece movingPiece = this.pieceAt(fromRow, fromCol);
		Piece capturedPiece = this.pieceAt(toRow, toCol);
		setPieceAt(toRow, toCol, movingPiece);
		boolean isValid = true;
		Piece king = findKing(movingPiece.isRed);
		if (king != null) {
			if (isUnderCheck(king.row, king.col, king.isRed))
				isValid = false;
			if (this.isInSameColumnWithEnemyKing(king.row, king.col, king.isRed))
				isValid = false;
		}

		setPieceAt(fromRow, fromCol, movingPiece);
		setPieceAt(toRow, toCol, capturedPiece);
		return isValid;
	}

	boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
		boolean byType = this.isValidMoveByType(fromRow, fromCol, toRow, toCol);
		boolean afterMove = this.isValidAfterMove(fromRow, fromCol, toRow, toCol);
		return byType && afterMove;
	}

	public boolean printValidMove(int fromRow, int fromCol) {
		Piece piece = this.pieceAt(fromRow, fromCol);
		if (piece == null) {
			System.out.println("No piece at the specified location.");
			return false;
		}
		if (piece.isRed)
			System.out.print(RED);
		else
			System.out.print(CYAN);
		System.out.println("Valid moves for " + piece.rank + " at (" + fromRow + ", " + fromCol + ")" + RESET);
		boolean hasValidMove = false;
		for (int toRow = 0; toRow < rows; toRow++)
			for (int toCol = 0; toCol < cols; toCol++)
				if (isValidMove(fromRow, fromCol, toRow, toCol)) {
					if (!hasValidMove)
						hasValidMove = true;
					System.out.print("(" + toRow + "," + toCol + ") ");
				}
		if (!hasValidMove) {
			if (piece.isRed)
				System.out.print(RED);
			else
				System.out.print(CYAN);
			System.out.println("No valid moves for " + piece.rank + " at (" + fromRow + ", " + fromCol + ").");
			System.out.print(RESET);
		} else
			System.out.println();
		System.out.println();
		return hasValidMove;
	}

	public boolean attemptMove(int fromRow, int fromCol, int toRow, int toCol) {
		if (!isValidMove(fromRow, fromCol, toRow, toCol))
			return false;

		Piece fromPiece = this.pieceAt(fromRow, fromCol);

		removePieceAt(fromRow, fromCol);
		setPieceAt(toRow, toCol, fromPiece);

		recordMove(toString());

		return true;
	}

	boolean isCheckmate(boolean isRed) {
		Piece king = findKing(isRed);
		return isUnderCheck(king.row, king.col, isRed) && !hasValidMove(isRed);
	}

	boolean isStalemate(boolean isRed) {
		Piece king = findKing(isRed);
		return !isUnderCheck(king.row, king.col, isRed) && !hasValidMove(isRed);
	}

	private boolean hasValidMove(boolean isRed) {
		List<Piece> piecesCopy = new ArrayList<>(pieces);
		for (Piece p : piecesCopy)
			if (p.isRed == isRed)
				for (int row = 0; row < rows; row++)
					for (int col = 0; col < cols; col++)
						if (isValidMove(p.row, p.col, row, col))
							return true;

		return false;
	}

	public boolean isGameOver(boolean isRed) {
		return isCheckmate(isRed) || isStalemate(isRed);
	}

}
