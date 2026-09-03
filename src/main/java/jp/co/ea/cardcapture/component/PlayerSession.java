package jp.co.ea.cardcapture.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@SessionScope
@Data
@Slf4j
public class PlayerSession {
	private String playerName = "";
	private GameDeck enemyDeck = new GameDeck("EnemyDeck");
	private GameDeck playerDeck = new GameDeck("PlayerDeck");
	private GameDeck enemyArea = new GameDeck("enemyArea");
	private GameDeck playerHands = new GameDeck("playerHands");
	private GameDeck discards = new GameDeck("discards");
	private GameDeck sealArea = new GameDeck("playerHands");
	//捕獲判定
	private List<Boolean> canCaptureCards = new ArrayList<Boolean>();

	private int rounds = 1;

	// 手札の絵札数
	private int numFacePlayer = 0;
	// EnemyAreaの絵札数
	private int numFaceEnemy = 0;

	// 封印判定
	private boolean canSealed;
	// 吹き飛ばし判定
	private boolean canBlowAway;

	// アクション実行チェック結果
	private boolean canExecuteAction;

	// 処理順序
	private int processState = 0;
	// ゲーム状態
	private int gameState = 0;
	// ゲーム状態メッセージ
	private String gameStateMessage = "";

	// 初期化
	public void init() {
		
		log.info("PlayerSession: init; 開始");
		
		playerName = "";
		enemyDeck.clearDeck();
		playerDeck.clearDeck();
		enemyArea.clearDeck();
		playerHands.clearDeck();
		discards.clearDeck();
		sealArea.clearDeck();

		log.info("enemyDeck: {} ", enemyDeck.getDeck().toString());

		
//		playerName = "";
//		enemyDeck = new GameDeck("EnemyDeck");
//		playerDeck = new GameDeck("PlayerDeck");
//		enemyArea = new GameDeck("enemyArea");
//		playerHands = new GameDeck("playerHands");
//		discards = new GameDeck("discards");
//		sealArea = new GameDeck("playerHands");

		rounds = 1;

		// 手札の絵札数
		numFacePlayer = 0;
		// EnemyAreaの絵札数
		numFaceEnemy = 0;
		// 封印判定
		canSealed = false;
		// 吹き飛ばし判定
		canBlowAway = false;
		// アクション実行チェック結果
		canExecuteAction = false;
		// 処理順序
		processState = 0;
		// ゲーム状態
		gameState = 0;
		// ゲーム状態メッセージ
		gameStateMessage = "";
		
		log.info("PlayerSession: init; 終了");

	}

	// 状態をリセットする
	public void reset() {
		playerName = "";
		enemyDeck.clearDeck();
		playerDeck.clearDeck();
		enemyArea.clearDeck();
		playerHands.clearDeck();
		discards.clearDeck();
		sealArea.clearDeck();

		rounds = 1;

		// 手札の絵札数
		numFacePlayer = 0;
		// EnemyAreaの絵札数
		numFaceEnemy = 0;
		// 封印判定
		canSealed = false;
		// 吹き飛ばし判定
		canBlowAway = false;
		// アクション実行チェック結果
		canExecuteAction = false;
		// 処理順序
		processState = 0;
		// ゲーム状態
		gameState = 0;
		// ゲーム状態メッセージ
		gameStateMessage = "";
	}
}
