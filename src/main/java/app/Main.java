package app;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.windowView.window.WindowLogic;

/**
 * デスクトップアプリ起動クラス
 *
 * このクラスは、
 *  - 起動前のエラーをユーザーに通知する
 *  - JavaFXや別スレッドでのエラーも拾って通知できるようにする
 *  - SwingのJOptionPane を使ってポップアップ通知を行う
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        // 未捕捉例外ハンドラをセット
        // JavaFXのApplication Threadや別スレッドの例外も拾う
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("未捕捉例外が発生しました。thread={}", thread.getName(), throwable);
            notifyUser("予期しない内部エラーが発生しました。", throwable);
        });
        
        try {
            // 起動処理
            WindowLogic logic = new WindowLogic();
            logic.execute();

        } catch (Throwable t) {
            // execute内の例外をキャッチ
            logger.error("起動中に例外が発生しました。", t);
            notifyUser("アプリケーションの起動に失敗しました。", t);
        }
    }

    /**
     * 起動前エラーをユーザーに通知する
     *
     * @param message ユーザー向けのタイトルや説明
     * @param t       発生した例外
     */
    private static void notifyUser(String message, Throwable t) {
        // まず標準エラー出力に出す（ログとして残す）
        System.err.println("[" + message + "] " + (t == null ? "" : t.getMessage()));
        if (t != null) {
            t.printStackTrace(System.err);
        }

        // SwingのEDTを使ってダイアログを出す
        try {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        buildDialogText(message, t),
                        "エラー",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        } catch (Throwable ignored) {
            // Swing 上でさらに例外が出る可能性があるため、安全に握りつぶし
        }
    }

    /**
     * ユーザーに出すテキストを整形する
     *
     * @param message   表示するメインメッセージ
     * @param throwable エラー詳細
     * @return 表示用テキスト
     */
    private static String buildDialogText(String message, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(message);
        if (throwable != null) {
            String detail = throwable.getMessage();
            if (detail != null && !detail.isBlank()) {
                sb.append("\n\n詳細: ").append(detail);
            }
        }
        return sb.toString();
    }
}