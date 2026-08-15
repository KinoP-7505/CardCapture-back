package jp.co.ea.cardcapture.controller.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import jp.co.ea.cardcapture.controller.validate.ValidActionRequest;
import lombok.Data;

@Data
@ValidActionRequest // 自作したクラスレベルカスタムアノテーションを付与
public class ActionRequest {

	/**
	 * アクションコード
	 * 1:捕獲
	 * 2;封印
	 * 3:吹き飛ばし
	 * 4:投了
	 */
	@NotNull(message = "アクションコードは必須です")
	private Integer actionCode;

	/**
	 * 対象敵カード
	 */
	@NotNull(message = "対象敵カードは必須です")
	private Integer targetEnemy;

	/**
	 * 選択カード
	 */
	@NotNull(message = "選択カードは必須です")
	private List<Integer> selected;
}
