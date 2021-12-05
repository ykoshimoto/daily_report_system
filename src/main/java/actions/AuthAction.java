package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;
import services.EmployeeService;

/**
 * 認証に関する処理を行うActionクラス
 */
public class AuthAction extends ActionBase {

    private EmployeeService service;

    /**
     * メソッドを実行する
     */
    @Override
    public void process() throws ServletException, IOException{
        service = new EmployeeService();

        //メソッドを実行
        invoke();

        service.close();

    }


    public void showLogin() throws ServletException, IOException{

        //CSRF対策用トークンを設定
        putRequestScope(AttributeConst.TOKEN, getTokenId());

        //セッションにフラッシュメッセージが登録されている場合はリクエストスコープに移す。
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(
                    AttributeConst.FLUSH,
                    getSessionScope(AttributeConst.FLUSH));
            removeSessionScope(AttributeConst.FLUSH);
        }

        //ログイン画面を表示
        forward(ForwardConst.FW_LOGIN);
    }
}