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
	/** ディスカードアクション */
	public static final Integer ACTION_DISCARD = 5;
	
	// ゲーム状態コード
	/** ゲーム前 **/
	public static final Integer GAMESTATE_INIT = 0;
	/** フェイズ：セットアップ **/
	public static final Integer GAMESTATE_SETUP = 1;
	/** フェイズ：アクション **/
	public static final Integer GAMESTATE_ACTION_SELECT = 2;
	/** フェイズ：結果チェック **/
	public static final Integer GAMESTATE_RESULT = 4;
	/** フェイズ：ディスカード **/
	public static final Integer GAMESTATE_DISCARD = 5;
	
	
	/** ゲーム中 **/
	public static final Integer GAMESTATE_PLAYING = 11;
	/** ゲーム勝利 **/
	public static final Integer GAMESTATE_WIN = 8;
	/** ゲーム敗北 **/
	public static final Integer GAMESTATE_DEFEAT= 9;
	
}
