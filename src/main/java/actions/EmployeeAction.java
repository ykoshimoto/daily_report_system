package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
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
}
