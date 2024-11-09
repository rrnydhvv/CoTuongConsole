package ChineseChess;

import java.util.*;

public class CChess {
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String CYAN = "\u001B[36m";

	public static void main(String[] agrs) {
		CChessBoard board = new CChessBoard();

		Scanner sc = new Scanner(System.in);
		board.printBoard();

		boolean isRedTurn = true;
		int act = 0, r, c;
		Piece tmp = null;
		String winner = "";
		while (true) {
			if (board.isGameOver(isRedTurn)) {
				if (board.isCheckmate(isRedTurn)) {
					if (isRedTurn) {
						winner = "Cyan";
						System.out.print(CYAN);
					} else {
						winner = "Red";
						System.out.print(RED);
					}
					System.out.println("Checkmate! " + winner + " wins the game." + RESET);
				} else if (board.isStalemate(isRedTurn)) {
					System.out.println("Stalemate! The game is a draw.");
				}
				break; // Kết thúc vòng lặp game
			}
			if (isRedTurn) {
				System.out.println(RED + "It's Red turn:" + RESET);
			} else {
				System.out.println(CYAN + "It's Cyan turn:" + RESET);
			}

			System.out.println("Choose your action: 1.Make move | 2. Quit");
			try {
				act = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.err.println("Please try again!");
				sc.nextLine();
				continue;
			}

			if (act == 2)
				break;
			if (act != 1) {
				System.err.println("Invalid action! Please try again.");
				continue;
			}

			System.out.println("Pick one of your pieces (EX: 0 4 -> Red KING): ");
			try {
				r = sc.nextInt();
				c = sc.nextInt();
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.err.println("Invalid input! Please try again.");
				sc.nextLine();
				continue;
			}

			tmp = board.pieceAt(r, c);
			if (tmp == null) {
				System.out.println("No piece at the specified location.");
				continue;
			}
			if (tmp.isRed != isRedTurn) {
				System.out.println("It's not your piece!");
				continue;
			} else {
				if (!board.printValidMove(r, c))
					continue;
				System.out.println("Choose your moves (EX: 1 4 -> Move Red KING to (1,4):");
				try {
					r = sc.nextInt();
					c = sc.nextInt();
					sc.nextLine();
				} catch (InputMismatchException e) {
					System.err.println("Invalid input! Please try again.");
					sc.nextLine();
					continue;
				}

				boolean moveSuccess = board.attemptMove(tmp.row, tmp.col, r, c);
				if (moveSuccess) {
					System.out.println("Move successful!");
					board.printBoard();
				} else {
					System.out.println("Invalid move, please try again!");
					continue;
				}
			}
			isRedTurn = !isRedTurn;
		}
		try {
			Thread.sleep(1200); // Độ trễ 1000ms =1 giây
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		board.displayMoveHistory();
		sc.close();
	}
}
