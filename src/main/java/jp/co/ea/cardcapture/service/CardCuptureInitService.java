package jp.co.ea.cardcapture.service;

import org.springframework.stereotype.Service;

import jp.co.ea.cardcapture.component.PlayerSession;
import jp.co.ea.cardcapture.controller.dto.InitAppResponse;
import jp.co.ea.cardcapture.controller.dto.InitGameRes;
import jp.co.ea.cardcapture.model.GameCard;
import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@Slf4j // これをつけるだけで 'log' 変数が利用可能になる
public class CardCuptureInitService {

	// 共通ステート
	private final GlobalStateService state;
	// プレイヤー情報（セッション）
	private final PlayerSession pSession;

	public InitAppResponse initApp() {
		log.info("CardCuptureInitService: appInit 開始");

		InitAppResponse response = new InitAppResponse();
		response.setTrumpDeck(state.getDeck());

		log.info("CardCuptureInitService: appInit 終了");

		return response;
	}

	/**
	 * ゲーム開始処理
	 * @return 
	 */
	public PlayerSession initGame() {

		log.info("CardCuptureInitService: initGame 開始");
		
		// sessionの初期化
		pSession.init();

		// EnemyDeck、PlayerDeck初期化
		GameDeck playerDeck = new GameDeck("PlayerDeck");
		// トランプカードのコードリストをALLコピー
		GameDeck enemyDeck = new GameDeck("EnemyDeck");
		enemyDeck.createDeck(state.getKeyList());

		// PlayerDeck作成
		//　enemyDeckからコード取得、それがプレイヤー初期カードの場合、
		// プレイヤーDeckに移動
		for (Integer card_code : state.getKeyList()) {
			var isMove = false;
			// コードの３桁目を取得（スート値）
			var pCard = new GameCard(card_code);
			var suit = pCard.getSuit();
			var number = pCard.getNumber();
			// ジョーカーの場合、カードをPlayerDeckへ移動
			if (suit == 5) {
				isMove = true;
			} else if (2 <= number && number <= 4) {
				// ナンバーが2～4の場合、PlayerDeckへ移動
				isMove = true;
			}

			// 移動対象の場合、EnemyDeck→PlayerDeck移動
			if (isMove) {
				CardCaptureUtility.deckCardMove(pCard, enemyDeck, playerDeck);
			}
		}
		// デッキシャッフル
		enemyDeck.shuffle();
		playerDeck.shuffle();

		log.info("CardCuptureInitService: initGame: PlayerDeck、EnemyDeck作成");

		// 盤面の作成
		// EnemyArea作成
		GameDeck enemyArea = new GameDeck("enemyArea");
		for (int i = 0; i < 4; i++) {
			// EnemyDeckのTopからドロー、EnemyAreaに追加
			var card = enemyDeck.lookTop();
			// TOPカードがある場合
			if (card != null) {
				CardCaptureUtility.deckCardMove(card, enemyDeck, enemyArea);
			}
		}
		log.info("CardCuptureInitService: initGame: gameStart: EnemyArea作成　枚数:" + enemyArea.size());

		// PlayerHandsの作成
		GameDeck playerHands = new GameDeck("playerHands");
		for (int i = 0; i < 4; i++) {
			// PlayerDeckのTopからドロー、PlayerHandsに追加
			var card = playerDeck.lookTop();
			// TOPカードがある場合
			if (card != null) {
				CardCaptureUtility.deckCardMove(card, playerDeck, playerHands);
			}
		}
		log.info("CardCuptureInitService: initGame: playerHands作成　枚数:" + playerHands.size());

		// 捨て場作成
		GameDeck discards = new GameDeck("discards");
		// 封印デッキ作成
		GameDeck SealArea = new GameDeck("SealArea");
		log.info("CardCuptureInitService: initGame: 捨て場、封印デッキ作成");

		// セッション格納、レスポンス設定
		pSession.setEnemyDeck(enemyDeck);
		pSession.setPlayerDeck(playerDeck);
		pSession.setEnemyArea(enemyArea);
		pSession.setPlayerHands(playerHands);
		pSession.setDiscards(discards);
		pSession.setSealArea(SealArea);

		log.info("CardCuptureInitService; initGame: 終了");

		return pSession;
	}

	public InitGameRes gameStart() {

		log.info("CardCuptureInitService: gameStart 開始");

		var response = new InitGameRes();

		// ゲームデッキ
		GameDeck playerDeck = pSession.getPlayerDeck();
		GameDeck enemyDeck = pSession.getEnemyDeck();
		GameDeck enemyArea = new GameDeck("enemyArea");
		GameDeck playerHands = new GameDeck("playerHands");
		GameDeck discards = new GameDeck("discards");
		GameDeck SealArea = new GameDeck("SealArea");

		// EnemyArea作成
		for (int i = 0; i < 4; i++) {
			// EnemyDeckのTopからドロー、EnemyAreaに追加
			var card = enemyDeck.lookTop();
			// TOPカードがある場合
			if (card != null) {
				CardCaptureUtility.deckCardMove(card, enemyDeck, enemyArea);
			}
		}
		log.info("CardCuptureInitService: gameStart: EnemyArea枚数:" + enemyArea.size());

		// PlayerHandsの作成
		for (int i = 0; i < 4; i++) {
			// PlayerDeckのTopからドロー、PlayerHandsに追加
			var card = playerDeck.lookTop();
			// TOPカードがある場合
			if (card != null) {
				CardCaptureUtility.deckCardMove(card, playerDeck, playerHands);
			}
		}
		log.info("CardCuptureInitService: gameStart: playerHands枚数:" + playerHands.size());

		//セッション・データ格納
		pSession.setEnemyDeck(enemyDeck);
		pSession.setPlayerDeck(playerDeck);
		pSession.setEnemyArea(enemyArea);
		pSession.setPlayerHands(playerHands);
		pSession.setDiscards(discards);
		pSession.setSealArea(SealArea);

		response.setPlayerDeck(playerDeck);
		response.setPlayerHands(playerHands);
		response.setEnemyDeck(enemyDeck);
		response.setEnemyArea(enemyArea);
		response.setRound(1); // 開始ラウンド
		response.setDiscardSize(discards.size());
		response.setSealAreaSize(SealArea.size());
		// プレイヤーカード総数
		response.setPlayerDeckSize(playerDeck.size() + playerHands.size() + discards.size());

		log.info("CardCuptureInitService: gameStart 終了");

		return response;
	}

}
