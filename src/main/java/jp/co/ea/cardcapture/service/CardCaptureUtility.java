package jp.co.ea.cardcapture.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import jp.co.ea.cardcapture.model.GameCard;
import jp.co.ea.cardcapture.model.GameDeck;

/**
 * ユーティリティ
 */
public final class CardCaptureUtility {
	
	// コンストラクタ禁止
	private CardCaptureUtility() {
        // 万が一、クラス内部やリフレクションから呼ばれた場合のエラー対策
        throw new AssertionError("Utility class cannot be instantiated");
    }

	/**
	 * 対象カードを元デッキから先デッキに移動する
	 * @param card_code 移動カード
	 * @param sourceDeck 移動元デッキ
	 * @param targetDeck 移動先デッキ
	 */
	public static void deckCardMove(GameCard card_code, GameDeck sourceDeck, GameDeck targetDeck) {
		sourceDeck.getDeck().remove(card_code);
		targetDeck.getDeck().add(card_code);
	}
	
	/**
	 * デッキシャッフル
	 * @param targetDeck  シャッフル対象デッキ
	 * @return shaffle後のデッキ
	 */
	public static List<Integer> shuffleDeck(List<Integer> targetDeck) {
		// Collections.shuffleを使用しても良いがアナログでやる
		List<Integer> workDeck = new ArrayList<>(targetDeck);
		SecureRandom random = new SecureRandom();
		// デッキからカードをランダムで抜き、最後尾に足す
		for (int i = 0; i < 10000; i++) {
			// index0～（deck.size()-1未満）のランダム値を取得
			int index = random.nextInt(targetDeck.size()-1);
			var card = workDeck.remove(index);
		    workDeck.add(card);
		} 
		
		return workDeck;
	}
	
	/**
	 * デッキ再作成
	 * @param sourceDeck 収集元
	 * @param targetDeck 収集先
	 */
	public static void reMakeDeck(GameDeck sourceDeck, GameDeck targetDeck) {
		
		// 元が0件になるまで実施
		while(sourceDeck.size() != 0) {
			// 元のカードを先へ移動
			var card = sourceDeck.getDeck().get(0);
			deckCardMove(card, sourceDeck, targetDeck);
		}
		
		// 先デッキをシャッフル
		targetDeck.shuffle();
	}


}
