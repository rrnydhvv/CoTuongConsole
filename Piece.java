package ChineseChess;

enum Rank {
	ROOK, KNIGHT, BISHOP, GUARD, KING, CANNON, PAWN
};

public class Piece {
	int col, row;
	boolean isRed; // 1 la quan do, 0 la quan den(xanh)
	Rank rank;

	Piece(int r, int c, boolean i, Rank R) {
		col = c;
		row = r;
		isRed = i;
		rank = R;
	}

	int rankToInt() {
		switch (rank) {
		case ROOK:
			return 0;
		case KNIGHT:
			return 1;
		case BISHOP:
			return 2;
		case GUARD:
			return 3;
		case KING:
			return 4;
		case CANNON:
			return 5;
		case PAWN:
			return 6;
		}
		return -1;
	}
}
