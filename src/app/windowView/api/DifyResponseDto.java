package app.windowView.api;

import java.util.List;

public class DifyResponseDto {

	//
	private String id;

	//チャットbotの応答メッセージ
	private String answer;

	//メッセージID　未使用
	private String message_id;

	//チャット履歴追跡Id　未使用
	private String conversation_id;

	//チャットbotが参照したドキュメントの情報を格納するリスト。
	private List<RetrieverResourceDto> retriever_resources;

	public String getId() {
		return id;
	}

	public String getAnswer() {
		return answer;
	}

	public String getMessage_id() {
		return message_id;
	}

	public String getConversation_id() {
		return conversation_id;
	}

	public List<RetrieverResourceDto> getRetriever_resources() {
		return retriever_resources;
	}

}
