package jp.co.ea.cardcapture.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import jp.co.ea.cardcapture.component.PlayerSession;
import jp.co.ea.cardcapture.config.CardCaptureConstant;
import jp.co.ea.cardcapture.config.TrumpMark;
import jp.co.ea.cardcapture.model.GameCard;
import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class CardCupturePlayService {

	// 共通ステート
	private final GlobalStateService state;
	// プレイヤー情報（セッション）
	private final PlayerSession pSession;

	/**
	 * エネミーフェイズ
	 */
	public void enemyPhase() {

		log.info("CardCupturePlayService: enemyPhase; 開始");

		// EnemyAreaチェック
		var enemyArea = pSession.getEnemyArea();
		var enemyDeck = pSession.getEnemyDeck();

		log.info("CardCupturePlayService: enemyPhase; EnemyDeck:" + enemyDeck.size());

		log.info("CardCupturePlayService: enemyPhase; 盤面操作後 EnemyDeck:" + enemyDeck.size());
		log.info("CardCupturePlayService: enemyPhase; 盤面操作後 enemyArea:" + enemyArea.size());

		// エリアが４枚未満、かつ、EnemyDeckが0でない場合
		while (enemyArea.size() < 4 && enemyDeck.size() > 0) {
			// EnemyDeckのTopからドロー、EnemyAreaに追加
			var card = enemyDeck.lookTop();
			// TOPカードが存在する場合
			if (card != null) {
				CardCaptureUtility.deckCardMove(card, enemyDeck, enemyArea);
			}
		}
		log.info("CardCupturePlayService: enemyPhase; 補充後 EnemyDeck:" + enemyDeck.size());
		log.info("CardCupturePlayService: enemyPhase; 補充後 enemyArea:" + enemyArea.size());

		log.info("CardCupturePlayService: enemyPhase; 終了");

	}

	/**
	 * ドローフェイズ 手札を4枚になるようドロー
	 * @param selectCard プレイヤーが選択したカード
	 */
	public void drawPhase() {
		var playerDeck = pSession.getPlayerDeck();
		var playerHands = pSession.getPlayerHands();
		var discards = pSession.getDiscards();

		// 手札を4枚に補充
		while (playerHands.size() < 4) {
			// デッキ0枚の場合、デッキ再作成
			if (playerDeck.size() == 0) {
				CardCaptureUtility.reMakeDeck(discards, playerDeck);
			}

			// ドロー
			var card = playerDeck.lookTop();
			CardCaptureUtility.deckCardMove(card, playerDeck, playerHands);
		}

		log.info("CardCupturePlayService: disCardAndDrowPhase; 補充後Pカード枚数:"
				+ (playerHands.size() + discards.size() + playerDeck.size()));
		log.info("CardCupturePlayService: disCardAndDrowPhase; 補充後手札枚数:" + playerHands.size());
		log.info("CardCupturePlayService: disCardAndDrowPhase; 補充後捨て場枚数:" + discards.size());
		log.info("CardCupturePlayService: disCardAndDrowPhase; 補充後山札枚数:" + playerDeck.size());
		log.info("CardCupturePlayService: disCardAndDrowPhase; ドロー後playerHands:" + playerHands.size());
	}

	/**
	 * アクションチェック
	 * プレイヤーの実行可能アクションをチェックする
	 * @return プレイヤー状態
	 */
	public PlayerSession actionCheck() {

		var playerHands = pSession.getPlayerHands();
		var enemyArea = pSession.getEnemyArea();

		// 手札チェック
		// 全フェイスカード判定
		int numFacePlayer = 0;
		for (GameCard card : playerHands.getDeck()) {
			// フェイスカードの場合
			if (card.isFace()) {
				numFacePlayer++;
			}
		}
		pSession.setNumFacePlayer(numFacePlayer);

		// 全てフェイスカードの場合、敗北ONに設定し、終了
		if (numFacePlayer == playerHands.size()) {
			pSession.setProcessState(CardCaptureConstant.GAMESTATE_DEFEAT);
			return pSession;
		}

		// 捕獲可能チェック
		List<Boolean> captureCards = new ArrayList<Boolean>();
		for (GameCard eCard : enemyArea.getDeck()) {
			// エネミーカードのスート
			var enemyCardSuit = eCard.getSuit();

			boolean isCapture = false;
			for (GameCard pCard : enemyArea.getDeck()) {
				var pCardSuit = pCard.getSuit();
				// スートが一致する敵カードがあれば、そのカードは捕獲アクション可能
				if (enemyCardSuit == pCardSuit) {
					isCapture = true;
					break;
				}
			}
			// 捕獲カード状態に追加
			captureCards.add(isCapture);

		}
		pSession.setCanCaptureCards(captureCards);

		// 封印可能チェック
		// EnemyAreaの最前カードを参照
		GameCard targetEnemyCard = enemyArea.getDeck().get(0);
		// 封印可能判定（フェイスカード判定反転）を格納
		pSession.setCanSealed(targetEnemyCard.isNumberCard());

		// 吹き飛ばし可能チェック

		int numFaceEnemy = 0;
		for (GameCard eCard : enemyArea.getDeck()) {
			// フェイスカードの場合
			if (eCard.isFace()) {
				numFaceEnemy++;
			}
		}
		// numFaceEnemy
		pSession.setNumFaceEnemy(numFaceEnemy);

		// 敵エリアが全てフェイスカードではない場合、吹き飛ばし可能
		pSession.setCanBlowAway(numFaceEnemy < enemyArea.size());

		return pSession;
	}

	/**
	 * 捕獲アクションチェック
	 * @param targetEnemy 捕獲対象カード
	 * @param selected 選択カード
	 * @return アクション結果
	 */
	public boolean checkActionCapture(GameCard targetEnemy, List<GameCard> selected) {
		
		log.info("CardCupturePlayService: checkActionCapture: 捕獲アクションチェック 開始" );

		int numberTotal = 0;
		int maxNumber = 0;
		int numberJoker = 0;

		// 捕獲実行チェック
		// 1.選択カードチェック 選択カードに対象カードスートと同じカードが在ること
		for (GameCard sCard : selected) {
			var eSuit = targetEnemy.getSuit();
			var sSuit = sCard.getSuit();

			// ジョーカーの場合、ジョーカー枚数をカウント、ループ継続
			if (sSuit == TrumpMark.JOKER.getCode()) {
				++numberJoker;
				continue;
			}

			// スート不一致が在る場合は実行チェックエラー(false返却）
			if (eSuit != sSuit) {
				return false;
			} else {
				// 最大ナンバー
				if (maxNumber == 0 || (maxNumber > 0 && maxNumber < sCard.getNumber())) {
					// maxNumberが初期値、または、
					// 最大ナンバーが更新されている、かつ、現在参照カードナンバーが大きい場合
					// maxNumberを現在カードナンバーで更新
					maxNumber = sCard.getNumber();
				}
				// ナンバー合計加算
				numberTotal += sCard.getNumber();
			}
		}

		// 2.ナンバーチェック
		// 選択カードナンバー合計が捕獲カードナンバー以上の場合
		// ジョーカーナンバーは選択カードの最大値と同じ
		// ジョーカー枚数を加算
		boolean isSucces = numberTotal + (maxNumber * numberJoker) >= (targetEnemy.getNumber());

		log.info("CardCupturePlayService: checkActionCapture: 捕獲アクションチェック 終了" );
		
		// 結果を返却
		return isSucces;
	}

	/**
	 * アクション実行
	 * @return アクション結果
	 */
	public PlayerSession excecuteAction(Integer actionCode, GameCard targetEnemy, List<GameCard> selected) {
		
		log.info("CardCupturePlayService: excecuteAction: アクション実行 開始" );

//		setTestSession();

		GameDeck enemyDeck = pSession.getEnemyDeck();
		GameDeck enemyArea = pSession.getEnemyArea();
		GameDeck playerHand = pSession.getPlayerHands();

		// エネミーカードの移動先
		GameDeck eTarget = null;
		// プレイヤーカードの移動先
		GameDeck pTarget = null;
		// 対象EnemyCardを移動
		if (actionCode == CardCaptureConstant.ACTION_CAPTURE) {
			// 捕獲アクション
			eTarget = pSession.getDiscards();
			pTarget = pSession.getDiscards();
			// Enemyカード移動
			CardCaptureUtility.deckCardMove(targetEnemy, enemyArea, eTarget);
		} else if (actionCode == CardCaptureConstant.ACTION_SEAL) {
			// 封印アクション
			eTarget = pSession.getSealArea();
			pTarget = pSession.getSealArea();
			// Enemyカード移動
			CardCaptureUtility.deckCardMove(targetEnemy, enemyArea, eTarget);
		} else if (actionCode == CardCaptureConstant.ACTION_BLOWAWAY) {
			// 吹き飛ばしアクション　EnemyCardはEnemyDeckの最下に送る
			var deck = enemyDeck.getDeck();
			deck.add(targetEnemy);
			// 対象EnemyカードをEnemyAreaから削除する。
			enemyArea.removeCard(targetEnemy.getCode());
			// プレイヤーカードは封印デッキに追加
			pTarget = pSession.getSealArea();
		} else if (actionCode == CardCaptureConstant.ACTION_DISCARD) {
			// 手札をディスカードエリアに移動
			pTarget = pSession.getDiscards();

			// 次のラウンド
			int nextRound = pSession.getRounds() + 1;
			pSession.setRounds(nextRound);
		}

		// 使用カードを移動 枚数はチェック済み
		// プレイヤーカード移動先は決定済み
		for (GameCard card : selected) {
			// 選択Cardを移動
			CardCaptureUtility.deckCardMove(card, playerHand, pTarget);
		}
		
		log.info("CardCupturePlayService: excecuteAction: アクション実行 終了" );

		return pSession;
	}

	/**
	 * 封印アクションチェック
	 * @param targetEnemy 対象カード
	 * @param selected 選択カード
	 * @return アクション結果
	 */
	public boolean checkActionSeal(GameCard targetEnemy, List<GameCard> selected) {
		
		log.info("CardCupturePlayService: checkActionSeal: 封印アクションチェック 開始" );

//		setTestSession();

		// 封印チェック
		// 対象カードはEnemyAreaの最前であること
		var enemyArea = pSession.getEnemyArea();
		var enemyFrontCard = enemyArea.getDeck().get(0);
		boolean isFront = targetEnemy.getCode() == enemyFrontCard.getCode();

		// 選択カードは数字カード
		boolean isNumber = selected.get(0).isNumberCard();

		log.info("CardCupturePlayService: checkActionSeal: 封印アクションチェック 終了" );

		// チェックOKであること
		return isFront && isNumber;
	}

	/**
	 * 吹き飛ばしアクションチェック
	 * @param targetEnemy 対象カード
	 * @param selected 選択カード
	 * @return アクション結果
	 */
	public boolean checkActionBlowAway(GameCard targetEnemy, List<GameCard> selected) {

		// setTestSession();

		// 対象カードはEnemyAreaに存在すること
		boolean isExist = false;
		var enemyArea = pSession.getEnemyArea();
		for (GameCard eCard : enemyArea.getDeck()) {
			if (targetEnemy.getCode() == eCard.getCode()) {
				isExist = true;
				break;
			}
		}

		var selected1 = selected.get(0);
		var selected2 = selected.get(1);

		// 選択カードは数字カード
		// 結果返却
		return isExist && selected1.isNumberCard() && selected2.isNumberCard();

	}

	/**
	 * セッションのモック
	 */
	private void setTestSession() {

		var enemyArea = new GameDeck("enemyArea");
		var deck = enemyArea.getDeck();
		deck.add(new GameCard(110));
		deck.add(new GameCard(109));
		deck.add(new GameCard(108));
		deck.add(new GameCard(111));
		pSession.setEnemyArea(enemyArea);

		var enemyDeck = new GameDeck("enemyDeck");
		deck = enemyDeck.getDeck();
		deck.add(new GameCard(211));
		deck.add(new GameCard(210));
		deck.add(new GameCard(209));
		deck.add(new GameCard(208));
		pSession.setEnemyDeck(enemyDeck);

		var playerHands = new GameDeck("playerHands");
		deck = enemyDeck.getDeck();
		deck.add(new GameCard(104));
		deck.add(new GameCard(105));
		deck.add(new GameCard(501));
		deck.add(new GameCard(204));
		pSession.setPlayerHands(playerHands);

		var discard = new GameDeck("discards");
		pSession.setDiscards(discard);

	}

	/**
	 * ゲーム勝利判定
	 * @return 判定結果
	 */
	public PlayerSession checkWinCondition() {
		GameDeck enemyDeck = pSession.getEnemyDeck();
		GameDeck enemyArea = pSession.getEnemyArea();
		
		// ゲーム状態
		int gameState = 0;
		// ゲーム状態メッセージ
		String gameStateMessage = "";	
		// 敵デッキなし、敵エリアなしの場合、プレイヤー勝利
		if (enemyArea.size() == 0 && enemyDeck.size() == 0 ) {
			gameState = CardCaptureConstant.GAMESTATE_WIN;
			gameStateMessage = "ゲームに勝利しました。";
		} else {
			gameState = CardCaptureConstant.GAMESTATE_PLAYING;
			gameStateMessage = "ゲーム継続";
		}
		
		pSession.setGameState(gameState);
		pSession.setGameStateMessage(gameStateMessage);
		
		return pSession;
	}

}
