package jp.co.ea.cardcapture.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jp.co.ea.cardcapture.CardCaptureBackApplication;
import jp.co.ea.cardcapture.component.PlayerSession;
import jp.co.ea.cardcapture.config.CardCaptureConstant;
import jp.co.ea.cardcapture.controller.dto.ActionRequest;
import jp.co.ea.cardcapture.controller.dto.ActionResponse;
import jp.co.ea.cardcapture.controller.dto.CardCaptureResponse;
import jp.co.ea.cardcapture.controller.dto.InitAppResponse;
import jp.co.ea.cardcapture.model.GameCard;
import jp.co.ea.cardcapture.model.GameDeck;
import jp.co.ea.cardcapture.service.CardCuptureInitService;
import jp.co.ea.cardcapture.service.CardCupturePlayService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cardcapture/")
@Tag(name = "CardCaptur API", description = "CardCaptureゲームで使用するAPI")
@Data
@Slf4j
public class CardCaptureController {

	private final CardCaptureBackApplication cardCaptureBackApplication;

	// プレイヤーセッションクラス
	private final PlayerSession pSession;

	// 初期化サービス
	private final CardCuptureInitService ccinitService;

	// ゲームプレイサービス
	private final CardCupturePlayService ccPlayService;

	CardCaptureController(CardCaptureBackApplication cardCaptureBackApplication, PlayerSession pSession,
			CardCuptureInitService ccinitService, CardCupturePlayService ccPlayService) {
		this.cardCaptureBackApplication = cardCaptureBackApplication;
		this.pSession = pSession;
		this.ccinitService = ccinitService;
		this.ccPlayService = ccPlayService;
	}

	@Operation(summary = "アプリケーション初期化", description = "トランプカード情報、定数")
	@GetMapping("/initApp")
	public ResponseEntity<InitAppResponse> initApp() {

		var response = ccinitService.initApp();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "初期ゲームの作成", description = "ゲーム開始時Deckを初期化")
	@GetMapping("/initGame")
	public ResponseEntity<CardCaptureResponse> initGame(HttpSession session) {
		log.info("CardCaptureController: initGame: ゲーム初期化 round :" + pSession.getRounds());
		log.info("executeAction Session ID: {}", session.getId());

		ccinitService.initGame();

		var response = new CardCaptureResponse();

		// デッキ
		response.setEnemyArea(pSession.getEnemyArea());
		response.setPlayerHands(pSession.getPlayerHands());
		// 表示情報
		response.setRounds(pSession.getRounds());
		response.setEnemyDeckSize(pSession.getEnemyDeck().size());
		response.setSealAreaSize(pSession.getSealArea().size());
		response.setPlayerDeckSize(pSession.getPlayerDeck().size());
		response.setDiscardSize(pSession.getDiscards().size());

		// プロセス２へ
		response.setProcessState(pSession.getProcessState());
		response.setGameState(pSession.getGameState());

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "ラウンド開始", description = "ラウンド開始")
	@GetMapping("/startRound")
	public ResponseEntity<CardCaptureResponse> startRound(HttpSession session) {
		log.info("CardCaptureController: startRound: ラウンド開始 round :{}, Session ID: {}", pSession.getRounds(),
				session.getId());

		// カード補充
		ccPlayService.enemyPhase();
		ccPlayService.drawPhase();
		// プロセス２へ
		pSession.setProcessState(CardCaptureConstant.GAMESTATE_ACTION_SELECT);

		var response = new CardCaptureResponse();

		// デッキ
		response.setEnemyArea(pSession.getEnemyArea());
		response.setPlayerHands(pSession.getPlayerHands());
		// 表示情報
		response.setRounds(pSession.getRounds());
		response.setEnemyDeckSize(pSession.getEnemyDeck().size());
		response.setSealAreaSize(pSession.getSealArea().size());
		response.setPlayerDeckSize(pSession.getPlayerDeck().size());
		response.setDiscardSize(pSession.getDiscards().size());

		response.setProcessState(pSession.getProcessState());
		response.setGameState(pSession.getGameState());

		log.info("CardCaptureController: startRound: 終了");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "アクション実行", description = "プレイヤーアクション判定・実行")
	@PostMapping("/executeAction")
	public ResponseEntity<ActionResponse> executeAction(
			@Valid @RequestBody ActionRequest request, HttpSession session) {
		log.info("CardCaptureController: executeAction: アクション実行 開始 Session ID: {}", session.getId());

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
			log.info("CardCaptureController: executeAction: アクション実行 チェックOK");

			// カードの移動先は前処理にて決定済み
			ccPlayService.excecuteAction(actionCode, target, selected.getDeck());

			// 勝利判定
			ccPlayService.checkWinCondition();

			// レスポンスに結果をセット
			response.setGameState(pSession.getGameState());
			response.setGameStateMessage(pSession.getGameStateMessage());

			// 次プロセスは設定済み
			response.setProcessState(pSession.getProcessState());

			// ラウンドを設定
			response.setRounds(pSession.getRounds());

			response.setIsSucces(isExecute);

			response.setGameState(pSession.getGameState());
			response.setGameStateMessage(pSession.getGameStateMessage());

			response.setEnemyArea(pSession.getEnemyArea());
			response.setPlayerHands(pSession.getPlayerHands());

			response.setEnemyDeckSize(pSession.getEnemyDeck().size());
			response.setSealAreaSize(pSession.getSealArea().size());
			response.setPlayerDeckSize(pSession.getPlayerDeck().size());
			response.setDiscardSize(pSession.getDiscards().size());

		}

		log.info("CardCaptureController: executeAction: アクション実行 終了");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "ディスカード実行", description = "プレイヤーディスカード・実行")
	@PostMapping("/executeDiscards")
	public ResponseEntity<ActionResponse> executeDiscards(
			@Valid @RequestBody ActionRequest request, HttpSession session) {
		log.info("CardCaptureController: executeDiscards: ディスカード実行 開始 Session ID: {}", session.getId());

		// リクエスト変換
		// 選択コード
		var selected = new GameDeck("selected");
		selected.createDeck(request.getSelected());

		ccPlayService.excecuteDiscards(selected.getDeck());

		// レスポンス
		var response = new ActionResponse();
		response.setPlayerHands(pSession.getPlayerHands());
		response.setDiscardSize(pSession.getDiscards().size());
		response.setRounds(pSession.getRounds());

		// プロセス１（セットアップ）へ
		response.setProcessState(pSession.getProcessState());

		log.info("CardCaptureController: executeDiscards: ディスカード実行 開始");

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
