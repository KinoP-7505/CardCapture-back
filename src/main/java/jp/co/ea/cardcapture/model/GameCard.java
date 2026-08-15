package jp.co.ea.cardcapture.model;

import lombok.Data;

@Data
public class GameCard {
	
	private int code;
	
	public GameCard(int code) {
		this.code = code;
	}
	
	/**
	 * スート取得
	 * @return スートナンバー
	 */
	public int getSuit() {
		return code / 100;
	}
	
	/**
	 * ナンバー取得
	 * @return カードナンバー
	 */
	public int getNumber() {
		return code % 100;
	}
	
	/**
	 * フェイスカード判定
	 * @return 判定結果(T/F）
	 */
	public boolean isFace() {
		int number = getNumber();
		return (11 <= number && number <= 14);
	}

	/**
	 * ナンバーカード判定
	 * @return 判定結果(T/F）
	 */
	public boolean isNumberCard() {
		return !isFace();
	}


}
