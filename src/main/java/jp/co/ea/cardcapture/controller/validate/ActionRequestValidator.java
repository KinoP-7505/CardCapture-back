package jp.co.ea.cardcapture.controller.validate;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import jp.co.ea.cardcapture.config.CardCaptureConstant;
import jp.co.ea.cardcapture.controller.dto.ActionRequest;

/**
 * ActionRequestバリデータ（実装）
 *   型引数1：
 */
public class ActionRequestValidator implements ConstraintValidator<ValidActionRequest, ActionRequest> {

	@Override
	public boolean isValid(ActionRequest value, ConstraintValidatorContext context) {
		// 対象のオブジェクト自体が null の場合はスキップ（別の @NotNull などに任せる）
		if (value == null) {
			return true;
		}

		// 単体項目が null の場合は、単体バリデーション（@NotNull）に任せるため相関チェックはスキップ
		if (value.getActionCode() == null) {
			return true;
		}

		boolean isValid = true;

		// 1. 手札選択枚数チェック
		if (!checkNumberSelected(value)) {
			// エラーが発生した時だけデフォルトメッセージを無効化してカスタムノードを追加する
            context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("手札選択枚数エラー")
					.addPropertyNode("selected") // エラー対象のフィールド名を指定
					.addConstraintViolation();
			isValid = false;
		}

		// 2. 封印アクション時の絵札チェック
		if (!checkFaceCard(value)) {
			// エラーが発生した時だけデフォルトメッセージを無効化する
            context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("封印アクションの場合、絵札は選択できません")
					.addPropertyNode("targetEnemy") // エラー対象のフィールド名を指定
					.addConstraintViolation();
			isValid = false;
		}

		return isValid;
	}

	// --- 相関チェックの個別ロジック ---

	/**
	 * アクション毎選択カード枚数チェック
	 * @param request リクエストDTO
	 * @return チェック結果
	 */
	private boolean checkNumberSelected(ActionRequest request) {
		if (request.getSelected() == null) {
			return true; // null の場合は @NotNull に任せる
		}

		List<Integer> selected = request.getSelected();
		Integer actionCode = request.getActionCode();

		if (actionCode.equals(CardCaptureConstant.ACTION_CAPTURE)) {
			return !selected.isEmpty() && selected.size() <= 4;
		} else if (actionCode.equals(CardCaptureConstant.ACTION_SEAL)) {
			return selected.size() == 1;
		} else if (actionCode.equals(CardCaptureConstant.ACTION_BLOWAWAY)) {
			return selected.size() == 2;
		}
		return true;
	}

	/**
	 * 絵札チェック
	 * @param request
	 * @return
	 */
	private boolean checkFaceCard(ActionRequest request) {
		if (request.getTargetEnemy() == null) {
			return true; // null の場合は @NotNull に任せる
		}

		if (request.getActionCode().equals(CardCaptureConstant.ACTION_SEAL)) {
			var number = request.getTargetEnemy() % 100;
			return number <= 10;
		}
		return true;
	}

}
