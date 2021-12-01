package actions;

/*
 * エラー発生時の処理を行うActionクラス
 */
import java.io.IOException;

import javax.servlet.ServletException;

import constants.ForwardConst;

public class UnknownAction extends ActionBase {

    @Override
    public void process() throws ServletException, IOException {
        // エラー画面を表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
    }

}