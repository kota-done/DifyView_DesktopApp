package app.windowView.api;

import java.util.HashMap;
import java.util.Map;

public class DifyRequestDto {

	//ユーザーが入力したメッセージ
	private final String query;

	//レスポンスの通信設定。streaming:ストリーミング　blocking:一括
	private final String response_mode = "streaming";

	//共通関数のPCMACアドレス取得メソッドから取得。
	private final String usrMacAddress;

	//ユーザーチャット履歴追跡用ID　追加機能予定のため未使用
	private final String conversation_id;

	//ユーザーリクエストをフォーム項目として格納する用。　7/7時点で未使用。
	private final Map<String, Object> inputs;

	//使用ユーザーID。後々は使用端末のMacアドレスを格納予定。
	private final String user;

	/**
	 *ユーザーメッセージを引数にするコンストラクタ　未使用項目（MACアドレス、チャット追跡ID、フォーム項目）あり。
	 * @param query　ユーザーメッセージ、Jsonの項目名と同一
	 */
	public DifyRequestDto(String usrInputs) {
		this.query = usrInputs;
		//以下は空で用意。
		this.usrMacAddress = null;
		this.conversation_id = null;
		this.inputs = new HashMap<String, Object>();
		this.user = "test-user";
	}

	public String getUser() {
		return user;
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
