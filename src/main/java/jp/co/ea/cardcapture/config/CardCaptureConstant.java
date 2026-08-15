package jp.co.ea.cardcapture.config;

/**
 * CardCaptur定数クラス
 */

public record CardCaptureConstant() {

	// カード系
	public static final String SUIT_SPADE = "♠";

	public static final String SUIT_HEART = "♥";

	public static final String SUIT_DIAMOND = "♦";

	public static final String SUIT_CLUB = "♣";

	// ゲーム状態
	/** ゲーム状態：ゲーム中  */
	public static final String PLAY_INGAME = "inGame";
	/** ゲーム状態：敗北：手札全絵札 */
	public static final String PLAY_LOSE_ALLFACE = "allFace";

	// アクションコード
	/** 捕獲アクション */
	public static final Integer ACTION_CAPTURE = 1;
	/** 封印アクション */
	public static final Integer ACTION_SEAL = 2;
	/** 吹き飛ばしアクション */
	public static final Integer ACTION_BLOWAWAY = 3;
	/** 投了アクション */
	public static final Integer ACTION_CONCEDE = 4;
	
}
