package ntx.sap.fm;

import ntx.ts.srv.TSparams;
import ntx.sap.sys.*;
import com.sap.conn.jco.*;
import ntx.sap.struct.*;

/**
 * Добавление поставки в ПНП
 */
public class Z_TS_IN2 {

  // importing params
  public String VBELN = ""; // Номер документа сбыта
  public String VBELN2 = ""; // Номер документа сбыта
  public String IGNORE_ZTS28 = ""; // не проверять zts28
  //
  // exporting params
  public ZTS_QTY_DIF_S QTY_DIF = new ZTS_QTY_DIF_S(); // Расхождения по кол-ву при приемке
  public String VBELNS = ""; // Поставки ПНП
  public String ZCOMP_CLIENT = ""; // Комплектация под клиента
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
      System.out.println("Вызов ФМ Z_TS_IN2");
    } else if (TSparams.logDocLevel >= 2) {
      System.out.println("Вызов ФМ Z_TS_IN2:");
      System.out.println("  VBELN=" + VBELN);
      System.out.println("  VBELN2=" + VBELN2);
      System.out.println("  IGNORE_ZTS28=" + IGNORE_ZTS28);
    }

    // вызов САПовской процедуры
    Exception e = execute(this);

    if (e == null) {
      if (TSparams.logDocLevel >= 2) {
        System.out.println("Возврат из ФМ Z_TS_IN2:");
        System.out.println("  QTY_DIF.QTY_VBEL=" + QTY_DIF.QTY_VBEL);
        System.out.println("  QTY_DIF.QTY_SCAN=" + QTY_DIF.QTY_SCAN);
        System.out.println("  QTY_DIF.QTY_NEDOST=" + QTY_DIF.QTY_NEDOST);
        System.out.println("  QTY_DIF.QTY_IZL=" + QTY_DIF.QTY_IZL);
        System.out.println("  QTY_DIF.QTY_PRT=" + QTY_DIF.QTY_PRT);
        System.out.println("  VBELNS=" + VBELNS);
        System.out.println("  ZCOMP_CLIENT=" + ZCOMP_CLIENT);
        System.out.println("  err=" + err);
      }
    } else {
      // обработка ошибки
      isErr = true;
      isSapErr = true;
      errFull = e.toString();
      ErrDescr ed = SAPconn.describeErr(errFull);
      err = ed.err;

      System.err.println("Error calling SAP procedure Z_TS_IN2:");
      if (ed.isShort || !err.equals(errFull)) {
        System.err.println(err);
      }
      if (!ed.isShort) {
        e.printStackTrace();
      }
      System.err.flush();

      if (errFull.startsWith("com.sap.conn.jco.JCoException: (104) JCO_ERROR_SYSTEM_FAILURE:")) {
        System.out.println("!!! Error in Z_TS_IN2: " + err);
      }
    }
  }

  private static synchronized Exception execute(Z_TS_IN2 params) {
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

      JCoStructure QTY_DIF_s = expParams.getStructure("QTY_DIF");

      impParams.setValue("VBELN", params.VBELN);
      impParams.setValue("VBELN2", params.VBELN2);
      impParams.setValue("IGNORE_ZTS28", params.IGNORE_ZTS28);

      ret = SAPconn.executeFunction(function);

      if (ret == null) {
        params.VBELNS = expParams.getString("VBELNS");
        params.ZCOMP_CLIENT = expParams.getString("ZCOMP_CLIENT");
        params.err = expParams.getString("ERR");
        if (!params.err.isEmpty()) {
          params.isErr = true;
          params.errFull = params.err;
        }
        params.QTY_DIF.QTY_VBEL = QTY_DIF_s.getBigDecimal("QTY_VBEL");
        params.QTY_DIF.QTY_SCAN = QTY_DIF_s.getBigDecimal("QTY_SCAN");
        params.QTY_DIF.QTY_NEDOST = QTY_DIF_s.getBigDecimal("QTY_NEDOST");
        params.QTY_DIF.QTY_IZL = QTY_DIF_s.getBigDecimal("QTY_IZL");
        params.QTY_DIF.QTY_PRT = QTY_DIF_s.getBigDecimal("QTY_PRT");
      }
    } catch (Exception e) {
      return e;
    }

    return ret;
  }

  private static JCoException init() {
    try {
      function = SAPconn.getFunction("Z_TS_IN2");
    } catch (JCoException e) {
      return e;
    }

    if (function == null) {
      return new JCoException(0, "Z_TS_IN2 not found in SAP.");
    }

    impParams = function.getImportParameterList();
    expParams = function.getExportParameterList();

    isInit = true;

    return null;
  }
}
