package ntx.sap.fm;

import ntx.ts.srv.TSparams;
import ntx.sap.sys.*;
import com.sap.conn.jco.*;

/**
 * Отсканирована ячейка-источник при перемещении
 */
public class Z_TS_SKL_MOVE1 {

  // importing params
  public String CELL = ""; // Складское место
  public String LGNUM = ""; // Номер склада/комплекс
  public String LGORT = ""; // Склад
  //
  // exporting params
  public String LGPLA = ""; // Складское место
  public String LGTYP = ""; // Тип склада
  public String NO_PAL = ""; // Без сканирования паллеты
  public String PAL1 = ""; // № единицы складирования
  public String NOT_WHOLE = ""; // Флаг невозм перем всего товара с паллеты
  public String ABC = ""; // Признак материала на складе ABC
  //
  // переменные для работы с ошибками
  public boolean isErr;
  public String err;
  public String errFull;
  public boolean isSapErr;
  //
  // вспомогательные переменные
  private static volatile JCoFunction function;
  private static volatile JCoParameterList impParams;
  private static volatile JCoParameterList expParams;
  private static volatile boolean isInit = false;

  public void execute() {
    isErr = false;
    isSapErr = false;
    err = "";
    errFull = "";

    if (TSparams.logDocLevel == 1) {
      System.out.println("Вызов ФМ Z_TS_SKL_MOVE1");
    } else if (TSparams.logDocLevel >= 2) {
      System.out.println("Вызов ФМ Z_TS_SKL_MOVE1:");
      System.out.println("  CELL=" + CELL);
      System.out.println("  LGNUM=" + LGNUM);
      System.out.println("  LGORT=" + LGORT);
    }

    // вызов САПовской процедуры
    Exception e = execute(this);

    if (e == null) {
      if (TSparams.logDocLevel >= 2) {
        System.out.println("Возврат из ФМ Z_TS_SKL_MOVE1:");
        System.out.println("  LGPLA=" + LGPLA);
        System.out.println("  LGTYP=" + LGTYP);
        System.out.println("  NO_PAL=" + NO_PAL);
        System.out.println("  PAL1=" + PAL1);
        System.out.println("  NOT_WHOLE=" + NOT_WHOLE);
        System.out.println("  ABC=" + ABC);
        System.out.println("  err=" + err);
      }
    } else {
      // обработка ошибки
      isErr = true;
      isSapErr = true;
      errFull = e.toString();
      ErrDescr ed = SAPconn.describeErr(errFull);
      err = ed.err;

      System.err.println("Error calling SAP procedure Z_TS_SKL_MOVE1:");
      if (ed.isShort || !err.equals(errFull)) {
        System.err.println(err);
      }
      if (!ed.isShort) {
        e.printStackTrace();
      }
      System.err.flush();

      if (errFull.startsWith("com.sap.conn.jco.JCoException: (104) JCO_ERROR_SYSTEM_FAILURE:")) {
        System.out.println("!!! Error in Z_TS_SKL_MOVE1: " + err);
      }
    }
  }

  private static synchronized Exception execute(Z_TS_SKL_MOVE1 params) {
    Exception ret = null;

    try {
      if (!isInit) {
        ret = init();
        if (ret != null) {
          return ret;
        }
      }

      impParams.clear();
      expParams.clear();

      impParams.setValue("CELL", params.CELL);
      impParams.setValue("LGNUM", params.LGNUM);
      impParams.setValue("LGORT", params.LGORT);

      ret = SAPconn.executeFunction(function);

      if (ret == null) {
        params.LGPLA = expParams.getString("LGPLA");
        params.LGTYP = expParams.getString("LGTYP");
        params.NO_PAL = expParams.getString("NO_PAL");
        params.PAL1 = expParams.getString("PAL1");
        params.NOT_WHOLE = expParams.getString("NOT_WHOLE");
        params.ABC = expParams.getString("ABC");
        params.err = expParams.getString("ERR");
        if (!params.err.isEmpty()) {
          params.isErr = true;
          params.errFull = params.err;
        }
      }
    } catch (Exception e) {
      return e;
    }

    return ret;
  }

  private static JCoException init() {
    try {
      function = SAPconn.getFunction("Z_TS_SKL_MOVE1");
    } catch (JCoException e) {
      return e;
    }

    if (function == null) {
      return new JCoException(0, "Z_TS_SKL_MOVE1 not found in SAP.");
    }

    impParams = function.getImportParameterList();
    expParams = function.getExportParameterList();

    isInit = true;

    return null;
  }
}
