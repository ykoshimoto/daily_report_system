package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

/**
 * 従業員に関わる処理を行うActionクラス
 */
public class EmployeeAction extends ActionBase{

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

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException{
        //指定されたページ数の一覧画面に表示するデータを取得
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);

        //すべての従業員データの件数を取得
        long employeeCount = service.countAll();

        putRequestScope(AttributeConst.EMPLOYEES, employees);
        putRequestScope(AttributeConst.EMP_COUNT, employeeCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションから削除
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH, flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_EMP_INDEX);

    }

    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void entryNew() throws ServletException, IOException{
        putRequestScope(AttributeConst.TOKEN, getTokenId());
        putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

        //新規登録画面を表示
        forward(ForwardConst.FW_EMP_NEW);
    }

    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException{
        //CSRF対策 tokenのチェック
        if(checkToken()) {
            //パラメータの値を元に従業員情報のインスタンスを生成する
            EmployeeView ev = new EmployeeView(
                    null, //id
                    getRequestParam(AttributeConst.EMP_CODE), //社員番号
                    getRequestParam(AttributeConst.EMP_NAME), //氏名
                    getRequestParam(AttributeConst.EMP_PASS), //パスワード
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)), //管理者権限フラグ
                    null, //登録日時
                    null, //更新日時
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue()); //削除フラグ

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報登録
            List<String> errors = service.create(ev, pepper);

            if(errors.size() > 0) {
                //登録中にエラーがあった場合
                putRequestScope(AttributeConst.TOKEN, getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE, ev);
                putRequestScope(AttributeConst.ERR, errors);

                //新規登録画面を再表示
                forward(ForwardConst.FW_EMP_NEW);
            }else {
                //登録中にエラーがなかった場合
                //セッションに登録完了のフラッシュメッセージ
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());
                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }
        }
    }

    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void show() throws ServletException, IOException{
        //idを条件に従業員データを取得
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

            //データが取得できなかった、または論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        putRequestScope(AttributeConst.EMPLOYEE, ev);

        forward(ForwardConst.FW_EMP_SHOW);

    }

    /**
     * 編集画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void edit() throws ServletException, IOException{

        //idを条件に従業員データを取得
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

            //データが取得できなかった、または論理削除されている場合はエラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }

        putRequestScope(AttributeConst.TOKEN, getTokenId());
        putRequestScope(AttributeConst.EMPLOYEE, ev);

        forward(ForwardConst.FW_EMP_EDIT);

    }






















}
