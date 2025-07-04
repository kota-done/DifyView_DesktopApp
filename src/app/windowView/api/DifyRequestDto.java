package app.windowView.api;

import java.util.Map;

public class DifyRequestDto {

	//
	private final String query;

	//
	private final String response_mode = "streaming";

	//共通関数のPCMACアドレス取得メソッドから取得。
	private final String usrMacAddress;

	//ユーザーチャット履歴追跡用ID　追加機能予定のため未使用
	private final String conversation_id;

	//ユーザーリクエストをフォーム項目として格納する用。　7/7時点で未使用。
	private final Map<String, Object> inputs;

	/**
	 *ユーザーメッセージを引数にするコンストラクタ　未使用項目（MACアドレス、チャット追跡ID、フォーム項目）あり。
	 * @param query　ユーザーメッセージ、Jsonの項目名と同一
	 */
	public DifyRequestDto(String usrInputs) {
		this.query = usrInputs;
		this.usrMacAddress = null;
		this.conversation_id = null;
		this.inputs = null;
	}

	public String getQuery() {
		return query;
	}

	public String getResponse_mode() {
		return response_mode;
	}

	public String getUsrMacAddress() {
		return usrMacAddress;
	}

	public String getConversation_id() {
		return conversation_id;
	}

	public Map<String, Object> getInputs() {
		return inputs;
	}

}
