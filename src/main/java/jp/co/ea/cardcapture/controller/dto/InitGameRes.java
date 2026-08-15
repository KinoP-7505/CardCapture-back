package jp.co.ea.cardcapture.controller.dto;

import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;

@Data
public class InitGameRes {

	//	private Map<Integer, TrumpCard> deck;

	/**
	 * エネミーデッキ
	 * カードコードのArrayList
	 */
	private GameDeck enemyDeck;

	/**
	 * エネミーエリア
	 * カードコードのArrayList
	 */
	private GameDeck enemyArea;

	/**
	 * プレイヤーデッキ
	 * カードコードのArrayList
	 */
	private GameDeck playerDeck;

	/**
	 * プレイヤー手札
	 * カードコードのArrayList
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
}
