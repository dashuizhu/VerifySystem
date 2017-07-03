package verifysystem.company.com.verifysystem.database;

import android.text.TextUtils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import java.util.List;
import java.util.UUID;
import verifysystem.company.com.verifysystem.model.RecordBean;

/**
 * @author zhuj 2017/6/29 下午4:43.
 */
public class RecordDao extends RealmObject {

  private final static String COLUMN_ID = "id";

  @PrimaryKey private String id;
  private String snNo;
  private String reportNo;
  private float temperature;
  private float humidity;
  private float voltage;//电压
  private String date;
  private int style;

  public static void saveOrUpdate(final RecordBean bean) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(castDao(bean));
      }
    });
  }


  public static void saveOrUpdates(final List<RecordBean> list) {
    RecordDao recordDao;
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    for (RecordBean bean: list) {
      recordDao = new RecordDao();
      if (TextUtils.isEmpty(bean.getId())) {
        recordDao.id = UUID.randomUUID().toString();
      } else {
        recordDao.id = bean.getId();
      }
      recordDao.reportNo = bean.getReportNo();
      recordDao.snNo = bean.getSnNo();
      recordDao.style = bean.getStyle();
      recordDao.voltage = bean.getVoltage();
      recordDao.humidity = bean.getHumidity();
      recordDao.date = bean.getDate();
      recordDao.temperature = bean.getTemperature();
      try {
        realm.copyToRealmOrUpdate(recordDao);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    realm.commitTransaction();
  }


  /**
   * 查询所有记录,倒序查
   */
  public static RealmResults<RecordDao> getList() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RecordDao> results;
    realm.beginTransaction();
    results = realm.where(RecordDao.class).findAll();
    realm.commitTransaction();
    //只获取最前面30条， 并清除多余的
    //if (results.size()>30) {
    //  results = results.subList(0,30);
    //}
    return results;
  }

  public static RecordBean getBeanById(int kid) {
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    RecordDao dao = realm.where(RecordDao.class).equalTo(COLUMN_ID, kid).findFirst();
    realm.commitTransaction();
    if (dao != null && dao.isValid()) {
      return dao.castBean();
    }
    return null;
  }

  public static RealmResults<RecordDao> getListSync() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RecordDao> results;
    results = realm.where(RecordDao.class).findAllAsync();
    return results;
  }

  /**
   * 清除所有
   */
  public static void cleanAll() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.where(RecordDao.class).findAll().deleteAllFromRealm();
      }
    });
  }

  public static RecordDao castDao(RecordBean bean) {
    RecordDao dao = new RecordDao();
    dao.id = bean.getId();
    dao.reportNo = bean.getReportNo();
    dao.date = bean.getDate();
    dao.snNo = bean.getSnNo();
    dao.style = bean.getStyle();
    dao.humidity = bean.getHumidity();
    dao.voltage = bean.getVoltage();
    dao.temperature = bean.getTemperature();
    return dao;
  }


  public RecordBean castBean() {
    RecordBean bean = new RecordBean();
    bean.setId(id);
    bean.setReportNo(reportNo);
    bean.setDate(date);
    bean.setSnNo(snNo);
    bean.setStyle(style);
    bean.setHumidity(humidity);
    bean.setVoltage(voltage);
    bean.setTemperature(temperature);
    return bean;
  }

}
