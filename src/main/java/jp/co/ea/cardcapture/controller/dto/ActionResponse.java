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
	
	// アクション実行成功した場合、盤面更新結果を更新
	private GameDeck enemyDeck;
	private GameDeck enemyArea;
	private GameDeck sealArea;
	private GameDeck playerDeck;
	private GameDeck playerHands;
	private GameDeck discards;


}
