package jp.co.ea.cardcapture.config;

/**
 * トランプスートENUM
 */
public enum TrumpMark {
	
	// 定数列挙（カンマ区切り）
	// スートマーク
	SPADE(1,"スペード", "♠"),
	HEART(2,"ハート","♥"),
	DIAMOND(3,"ダイヤ","♦"),
	CLUB(4,"クラブ","♣"),
	JOKER(5, "ジョーカー", "JK"),
	// ナンバーマーク
	JACK(11,"ジャック", "J"),
	QUEEN(12, "クイーン", "Q"),
	KING(13, "キング", "K"),
	ACE(14,"エース","A");
	
	
	// フィールド
	// コード
	private final int code;
	// ラベル
	private final String label;
	// マーク
	private final String mark;

	TrumpMark(int code, String label, String mark) {
		this.code = code;
		this.label = label;
		this.mark = mark;
	}

	// getter
	public int getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	public String getMark() {
		return mark;
	}

	// 【逆引きメソッド】値(code)からEnumオブジェクトを取得する
    public static TrumpMark getByCode(int code) {
        return java.util.Arrays.stream(TrumpMark.values())
                .filter(s -> s.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("存在しないコード値です: " + code));
    }

}
