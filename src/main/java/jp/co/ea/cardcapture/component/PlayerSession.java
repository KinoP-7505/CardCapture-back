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
	private int rounds = 1;
	
	// ゲーム状態
	private String playState;
	
	// 手札の絵札数
	private int numFacePlayer = 0;
	// EnemyAreaの絵札数
	private int numFaceEnemy = 0;
	
	//捕獲判定
	private List<Boolean> canCaptureCards = new ArrayList<Boolean>();
	// 封印判定
	private boolean canSealed;
	// 吹き飛ばし判定
	private boolean canBlowAway;
	
	// アクション実行チェック結果
	private boolean canExecuteAction;
	
	// 処理順序
	private int processState = 0;
	

}
