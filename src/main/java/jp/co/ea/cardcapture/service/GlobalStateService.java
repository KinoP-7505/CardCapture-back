package jp.co.ea.cardcapture.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import jp.co.ea.cardcapture.config.TrumpMark;
import jp.co.ea.cardcapture.model.TrumpCard;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * トランプデッキ
 * 
 * トランプセットを現す
 * deck カードを参照するベース
 */
@Slf4j // これをつけるだけで 'log' 変数が利用可能になる
@Service
@Data
public class GlobalStateService {
	
	// 共通で使用するトランプデッキセット
	private final Map<Integer, TrumpCard> deck = new ConcurrentHashMap<Integer, TrumpCard>();

	// コンストラクタ
	public GlobalStateService() {
		createDeck();
	}
	

	/**
	 * トランプデッキの作成
	 */
	public void createDeck() {
		
		log.info("createDeck; 開始");
		
		// デッキが作成済みの場合は処理をスキップする。
		if (deck.size() > 0) return; 

		// スート回数（1～4）
		for (int suit = 1; suit <= 4; suit++) {
			// ナンバー回数（2～14）
			for (int num = 2; num <= 14 ; num++) {
				// CARD作成
				var card = new TrumpCard(suit, num);
//				log.info("createDeck; makeCard :" + card.cardLabel());
				// デッキに格納
				deck.put(card.code(), card);
//				log.info("suit="+suit+ " / num="+num + " / card-c:"+card.code());
			}
		}
		
		// ジョーカーの追加
		// 仮に２枚
		int maxNum = 2;
		for (int num = 1; num <= maxNum; num++) {
			var card = new TrumpCard(TrumpMark.JOKER.getCode(),num);
			deck.put(card.code(), card);
//			log.info("createDeck; makeCard :" + card.cardLabel());
		}

		log.info("createDeck; 終了");
	
	}
	
	// TrumpDeckの全カードのコードをList取得
	public List<Integer> getKeyList() {
		
		return new ArrayList<Integer>(deck.keySet());
	}
}
