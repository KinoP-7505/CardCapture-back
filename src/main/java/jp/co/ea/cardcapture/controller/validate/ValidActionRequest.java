package jp.co.ea.cardcapture.controller.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * アノテーションクラス
 */
@Target({ ElementType.TYPE }) // クラスに対して付与できるように指定
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ActionRequestValidator.class) // 後述するバリデータクラスを指定
@Documented
public @interface ValidActionRequest {

	// デフォルトのエラーメッセージ
	String message() default "入力内容に不備があります";

	// バリデーショングループ指定用（必須）
	Class<?>[] groups() default {};

	// ペイロード指定用（必須）
	Class<? extends Payload>[] payload() default {};
}
