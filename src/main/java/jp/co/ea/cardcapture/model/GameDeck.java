package jp.co.ea.cardcapture.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameDeck {

	/**
	 * デッキカードリスト
	 */
	private List<GameCard> deck = new ArrayList<GameCard>();

	/**
	 * デッキ名
	 */
	private final String label;
	
	/**
	 * コンストラクタ ラベル設定
	 * @param label
	 */
	public GameDeck(String label) {
		this.label = label;
	}

	/**
	 * deckをシャッフル
	 */
	public void shuffle() {
		if (deck.size() == 0) return;

		// Collections.shuffleを使用しても良いがアナログでやる
		List<GameCard> workDeck = new ArrayList<>(deck);
		SecureRandom random = new SecureRandom();
		final Integer endIndex = deck.size() - 1;
		// デッキからカードをランダムで抜き、最後尾に足す
		for (int i = 0; i < 10000; i++) {
			// index0～（deck.size()-1未満）のランダム値を取得
			int index = random.nextInt(endIndex);
			var card = workDeck.remove(index);
			workDeck.add(card);
		}

		deck = workDeck;
	}
	
	/**
	 * code配列からDeck作成
	 * @param list
	 */
	public void createDeck(List<Integer> list) {
		var workDeck = new ArrayList<GameCard>();
		for (Integer num: list) {
			var card = new GameCard(num);
			workDeck.add(card);
		}
		
		deck = workDeck;
	}

	/**
	 * デッキTOPカードを見る
	 * @return TOPカードナンバー
	 */
	public GameCard lookTop() {
		if (deck.size() == 0) {
			return null;
		}
		return deck.get(0);
	}
	
	/**
	 * デッキサイズ取得
	 */
	public int size() {
		return deck.size();
	}
	
	/**
	 * デッキからcodeのカードを削除
	 * @param cardCode 対象カードコード
	 */
	public void removeCard(Integer cardCode) {
		deck.removeIf(card -> card.getCode() == cardCode);
	}

}
