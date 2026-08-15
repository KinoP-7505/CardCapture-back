package jp.co.ea.cardcapture.model;

import jp.co.ea.cardcapture.config.TrumpMark;
import lombok.extern.slf4j.Slf4j;

/**
 * トランプカードRECORDモデル
 * 
 * suit スート
 * number ナンバー
 * code スートとナンバーの遠し番号（xxyy)
 */
@Slf4j // これをつけるだけで 'log' 変数が利用可能になる
public record TrumpCard(int suit, int number) {
	
	public int suit() {
		return suit;
	}

	public int number() {
		return number;
	}

	// code取得
	public Integer code() {
		return suit * 100 + number;
	}

	/**
	 * カードラベル
	 * @return スート＋ナンバーのラベル
	 */
	public String cardLabel() {
		String suitLabel =TrumpMark.getByCode(suit).getLabel();
		String numberLabel = number + "";
		if (number > 10) {
			numberLabel = TrumpMark.getByCode(number).getLabel();
		}
		
		return "code; "+ code() + " / suitL"  +  suitLabel + " s:" + suit + " / num: " + number + " numLab:" + numberLabel;
	}
	

}
