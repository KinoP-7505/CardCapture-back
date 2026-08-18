package jp.co.ea.cardcapture.controller.dto;

import java.util.Map;

import jp.co.ea.cardcapture.model.TrumpCard;
import lombok.Data;

@Data
public class InitAppResponse {
	
	private Map<Integer, TrumpCard> trumpDeck;

}
