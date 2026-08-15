package jp.co.ea.cardcapture.controller;

import java.util.ArrayList;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jp.co.ea.cardcapture.component.PlayerSession;
import jp.co.ea.cardcapture.config.CardCaptureConstant;
import jp.co.ea.cardcapture.controller.dto.ActionRequest;
import jp.co.ea.cardcapture.controller.dto.ActionResponse;
import jp.co.ea.cardcapture.controller.dto.CardCaptureResponse;
import jp.co.ea.cardcapture.controller.dto.InitGameRes;
import jp.co.ea.cardcapture.model.GameCard;
import jp.co.ea.cardcapture.model.GameDeck;
import jp.co.ea.cardcapture.service.CardCuptureInitService;
import jp.co.ea.cardcapture.service.CardCupturePlayService;
import lombok.Data;

/**
 * 
 * コントローラー
 * CardCaptureゲームで使用するAPI
 * 
 * SwaggerUIの表示
 * http://localhost:8080/swagger-ui.html
 * 
 */
@RestController
@RequestMapping("/api/cardcapture/")
@Tag(name = "CardCaptur API", description = "CardCaptureゲームで使用するAPI")
@Data
public class CardCaptureController {

	// プレイヤーセッションクラス
	private final PlayerSession pSession;

	// 初期化サービス
	private final CardCuptureInitService ccinitService;

	// ゲームプレイサービス
	private final CardCupturePlayService ccPlayService;

	@Operation(summary = "初期ゲームの作成", description = "ゲーム開始時Deckを作成")
	@PostMapping("/initGame")
	public ResponseEntity<InitGameRes> initGame() {
		var response = new InitGameRes();

		response = ccinitService.init();

		response = ccinitService.gameStart();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "ゲーム開始", description = "ゲーム開始テスト")
	@PostMapping("/startGameTest")
	public ResponseEntity<CardCaptureResponse> startGameTest() {
		var deckres = new InitGameRes();

		deckres = ccinitService.init();

		deckres = ccinitService.gameStart();

		var response = new CardCaptureResponse();

		ccPlayService.enemyPhase();

		var list = new ArrayList<GameCard>();
		PlayerSession playData = ccPlayService.disCardAndDrowPhase(list);

		response.setEnemyDeck(playData.getEnemyDeck());
		response.setEnemyArea(playData.getEnemyArea());
		response.setPlayerDeck(playData.getPlayerDeck());
		response.setPlayerHands(playData.getPlayerHands());
		response.setDiscardSize(1);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "アクションチェック", description = "アクションチェックテスト")
	@PostMapping("/actionCheckTest")
	public ResponseEntity<CardCaptureResponse> actionCheckTest() {

		var deckres = new InitGameRes();

		deckres = ccinitService.init();

		deckres = ccinitService.gameStart();

		ccPlayService.enemyPhase();

		var list = new ArrayList<GameCard>();
		PlayerSession playData = ccPlayService.disCardAndDrowPhase(list);

		playData = ccPlayService.actionCheck();

		var response = new CardCaptureResponse();

		response.setEnemyDeck(playData.getEnemyDeck());
		response.setEnemyArea(playData.getEnemyArea());
		response.setPlayerDeck(playData.getPlayerDeck());
		response.setPlayerHands(playData.getPlayerHands());
		response.setDiscardSize(1);

		response.setPlayState(pSession.getPlayState());
		response.setCanCaptureCards(pSession.getCanCaptureCards());
		response.setCanSealed(pSession.isCanSealed());
		response.setCanBlowAway(pSession.isCanBlowAway());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "アクション実行", description = "プレイヤーアクション判定・実行")
	@PostMapping("/executeAction")
	public ResponseEntity<ActionResponse> executeAction(
			@Valid @RequestBody ActionRequest request) {
		var response = new ActionResponse();

		// リクエスト変換
		// アクションコード
		var actionCode = request.getActionCode();
		// 対象コード
		var target = new GameCard(request.getTargetEnemy());
		// 選択コード
		var selected = new GameDeck("selected");
		selected.createDeck(request.getSelected());

		// アクションコード毎の実行チェック
		boolean isExecute = false;
		if (actionCode == CardCaptureConstant.ACTION_CAPTURE) {
			isExecute = ccPlayService.checkActionCapture(target, selected.getDeck());
		} else if (actionCode == CardCaptureConstant.ACTION_SEAL) {
			isExecute = ccPlayService.checkActionSeal(target, selected.getDeck());
		} else if (actionCode == CardCaptureConstant.ACTION_BLOWAWAY) {
			isExecute = ccPlayService.checkActionBlowAway(target, selected.getDeck());
		}
		response.setIsSucces(isExecute);

		// チェックOKの場合、アクション実行
		if (isExecute) {
			// カードの移動先は前処理にて決定済み
			PlayerSession pSession = ccPlayService.excecuteAction(actionCode, target, selected.getDeck());
			// レスポンスに結果をセット
			response.setEnemyDeck(pSession.getEnemyDeck());
			response.setEnemyArea(pSession.getEnemyArea());
			response.setPlayerHands(pSession.getPlayerHands());
			response.setDiscards(pSession.getDiscards());
			response.setSealArea(pSession.getSealArea());
		}

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
