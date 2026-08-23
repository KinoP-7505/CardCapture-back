package jp.co.ea.cardcapture.controller.dto;

import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;

/**
 * アクションレスポンス
 */
@Data
public class ActionResponse {
	
	/**
	 * アクション実行成功
	 */
	private Boolean isSucces;
	private Integer processState; // 処理状態	
	
	// アクション実行成功した場合、盤面更新結果を更新
	private GameDeck enemyArea; // エネミーエリア
	private GameDeck playerHands; // プレイヤーハンド
	
	private int rounds; // ラウンド数
	private int enemyDeckSize;  // エネミーデッキサイズ
	private int sealAreaSize; // シールデッキサイズ
	private int playerDeckSize; // プレイヤーデッキサイズ
	private int discardSize; // 捨て札デッキサイズ
	
	// ゲーム状態
	private int gameState = 0;
	// ゲーム状態メッセージ
	private String gameStateMessage = "";	

//	private GameDeck sealArea;
//	private GameDeck playerDeck;
//	private GameDeck discards;

//	  enemyArea: GameDeck // エネミーエリア
//	  playerHands: GameDeck // プレイヤーハンド
//	  rounds: number // ラウンド数
//	  enemyDeckSize: number // エネミーデッキサイズ
//	  sealDeckSize: number // シールデッキサイズ
//	  playerDeckSize: number // プレイヤーデッキサイズ
//	  discardSize: number // 捨て札デッキサイズ
//	  processState: number // 処理状態	

}