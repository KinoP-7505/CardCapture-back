package jp.co.ea.cardcapture.controller.dto;

import java.util.ArrayList;
import java.util.List;

import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;

@Data
public class CardCaptureResponse {

	/**
	 * エネミーデッキ
	 */
	private GameDeck enemyDeck;

	/**
	 * エネミーエリア
	 */
	private GameDeck enemyArea;

	/**
	 * プレイヤーデッキ
	 */
	private GameDeck playerDeck;

	/**
	 * プレイヤー手札
	 */
	private GameDeck playerHands;

	/**
	 * 現在ラウンド数
	 */
	private int round;

	/**
	 * 捨て札枚数
	 */
	private int discardSize;

	/**
	 * 封印枚数
	 */
	private int sealAreaSize;

	/**
	 * プレイヤーデッキ枚数
	 */
	private int playerDeckSize;
	
	// ゲーム状態
	private String playState;
	
	// 手札の絵札数
	private int numFacePlayer = 0;
	// EnemyAreaの絵札数
	private int numFaceEnemy = 0;
	
	//捕獲判定
	private List<Boolean> canCaptureCards = new ArrayList<Boolean>();
	// 封印判定
	private boolean canSealed;
	// 吹き飛ばし判定
	private boolean canBlowAway;
}
