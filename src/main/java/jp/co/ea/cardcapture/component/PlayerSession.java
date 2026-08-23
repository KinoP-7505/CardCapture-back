package jp.co.ea.cardcapture.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jp.co.ea.cardcapture.model.GameDeck;
import lombok.Data;

@Component
@SessionScope
@Data
public class PlayerSession {
	private String playerName;
	private GameDeck enemyDeck;
	private GameDeck playerDeck;
	private GameDeck enemyArea;
	private GameDeck playerHands;
	private GameDeck discards;
	private GameDeck sealArea;
	//捕獲判定
	private List<Boolean> canCaptureCards = new ArrayList<Boolean>();

	private int rounds = 1;
	// ゲーム状態
//	private String playState;

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
		// TODO 自動生成されたメソッド・スタブ
		playerName = "";
		enemyDeck = new GameDeck("EnemyDeck");
		playerDeck = new GameDeck("PlayerDeck");
		enemyArea = new GameDeck("enemyArea");
		playerHands = new GameDeck("playerHands");
		discards = new GameDeck("discards");
		sealArea = new GameDeck("playerHands");

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
