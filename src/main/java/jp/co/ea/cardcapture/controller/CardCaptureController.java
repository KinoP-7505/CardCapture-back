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
import jp.co.ea.cardcapture.component.PlayerSession;
import jp.co.ea.cardcapture.config.CardCaptureConstant;
import jp.co.ea.cardcapture.controller.dto.ActionRequest;
import jp.co.ea.cardcapture.controller.dto.ActionResponse;
import jp.co.ea.cardcapture.controller.dto.CardCaptureResponse;
import jp.co.ea.cardcapture.controller.dto.InitAppResponse;
import jp.co.ea.cardcapture.controller.dto.InitGameRes;
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

	// プレイヤーセッションクラス
	private final PlayerSession pSession;

	// 初期化サービス
	private final CardCuptureInitService ccinitService;

	// ゲームプレイサービス
	private final CardCupturePlayService ccPlayService;

	@Operation(summary = "アプリケーション初期化", description = "トランプカード情報、定数")
	@GetMapping("/initApp")
	public ResponseEntity<InitAppResponse> initApp() {

		var response = ccinitService.initApp();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "初期ゲームの作成", description = "ゲーム開始時Deckを作成")
	@PostMapping("/initGame")
	public ResponseEntity<InitGameRes> initGame() {
		var response = new InitGameRes();

		// response = ccinitService.init();

		response = ccinitService.gameStart();

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "ラウンド開始", description = "ラウンド開始")
	@GetMapping("/startRound")
	public ResponseEntity<CardCaptureResponse> startRound(HttpSession session) {
//		var deckres = new InitGameRes();
		
		log.info("executeAction Session ID: {}", session.getId());
		
//		PlayerSession initDecks = null;
		
		// ゲーム開始時の場合
		if (pSession.getRounds() == 1) {
			ccinitService.initGame();
		} else {
			// カード補充
			ccPlayService.enemyPhase();
			ccPlayService.drawPhase();
		}

		var response = new CardCaptureResponse();
		
//		PlayerSession playData = pSession;

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
		response.setProcessState(CardCaptureConstant.GAMESTATE_ACTION_SELECT);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

//	@Operation(summary = "アクションチェック", description = "アクションチェックテスト")
//	@PostMapping("/actionCheckTest")
//	public ResponseEntity<CardCaptureResponse> actionCheckTest() {
//
//		var deckres = new InitGameRes();
//
////		deckres = ccinitService.init();
//
//		deckres = ccinitService.gameStart();
//
//		ccPlayService.enemyPhase();
//
//		var list = new ArrayList<GameCard>();
//		PlayerSession playData = ccPlayService.disCardAndDrowPhase(list);
//
//		playData = ccPlayService.actionCheck();
//
//		var response = new CardCaptureResponse();
//
////		response.setEnemyDeck(playData.getEnemyDeck());
//		response.setEnemyArea(playData.getEnemyArea());
////		response.setPlayerDeck(playData.getPlayerDeck());
//		response.setPlayerHands(playData.getPlayerHands());
//		response.setDiscardSize(1);
//
//		response.setPlayState(pSession.getPlayState());
//		response.setCanCaptureCards(pSession.getCanCaptureCards());
//		response.setCanSealed(pSession.isCanSealed());
//		response.setCanBlowAway(pSession.isCanBlowAway());
//
//		return ResponseEntity.status(HttpStatus.OK).body(response);
//	}

	@Operation(summary = "アクション実行", description = "プレイヤーアクション判定・実行")
	@PostMapping("/executeAction")
	public ResponseEntity<ActionResponse> executeAction(
			@Valid @RequestBody ActionRequest request, HttpSession session) {
		log.info("CardCaptureController: executeAction: アクション実行 開始" );
		
		log.info("executeAction Session ID: {}", session.getId());

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
		} else if (actionCode == CardCaptureConstant.ACTION_DISCARD) {
			// ディスカードの場合、チェックなし
			isExecute = true;
		}
		response.setIsSucces(isExecute);

		// チェックOKの場合、アクション実行
		if (isExecute) {
			log.info("CardCaptureController: executeAction: アクション実行 チェックOK" );

			// カードの移動先は前処理にて決定済み
			ccPlayService.excecuteAction(actionCode, target, selected.getDeck());
			
			// 勝利判定
			PlayerSession pSession = ccPlayService.checkWinCondition();
			
			// レスポンスに結果をセット
			if (actionCode == CardCaptureConstant.ACTION_DISCARD) {
				response.setPlayerHands(pSession.getPlayerHands());
				response.setDiscardSize(pSession.getDiscards().size());
				response.setRounds(pSession.getRounds());
				
				// プロセス１（セットアップ）へ
				response.setProcessState(CardCaptureConstant.GAMESTATE_SETUP);				
				
			} else {
				response.setIsSucces(isExecute);
				response.setEnemyArea(pSession.getEnemyArea());
				response.setPlayerHands(pSession.getPlayerHands());
				
//				response.setRounds(pSession.getRounds());
				response.setSealAreaSize(pSession.getSealArea().size());
				response.setPlayerDeckSize(pSession.getPlayerDeck().size());
				response.setDiscardSize(pSession.getDiscards().size());
				
				response.setGameState(pSession.getGameState());
				response.setGameStateMessage(pSession.getGameStateMessage());
				
				// プロセス３へ
				response.setProcessState(CardCaptureConstant.GAMESTATE_DISCARD);
			}

			
		}

		log.info("CardCaptureController: executeAction: アクション実行 終了" );
		
//		レスポンス内容を確認

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
