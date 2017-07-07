package verifysystem.company.com.verifysystem.database;

import android.text.TextUtils;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import java.util.List;
import java.util.UUID;
import verifysystem.company.com.verifysystem.model.RecordBean;

/**
 * @author zhuj 2017/6/29 下午4:43.
 */
@RealmClass
public class RecordLastDao extends RealmObject {

  private final static String COLUMN_SN = "snNo";

  @PrimaryKey
  private String snNo;
  private String date;

  public static void saveOrUpdate(final RecordBean bean) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.copyToRealmOrUpdate(castDao(bean));
      }
    });
  }


  public static void saveOrUpdates(final List<RecordBean> list) {
    RecordLastDao recordDao;
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    for (RecordBean bean: list) {
      recordDao = new RecordLastDao();
      recordDao.snNo = bean.getSnNo();
      recordDao.date = bean.getDate();
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
  public static RealmResults<RecordLastDao> getList() {
    Realm realm = Realm.getDefaultInstance();
    RealmResults<RecordLastDao> results;
    realm.beginTransaction();
    results = realm.where(RecordLastDao.class).findAll();
    realm.commitTransaction();
    //只获取最前面30条， 并清除多余的
    //if (results.size()>30) {
    //  results = results.subList(0,30);
    //}
    return results;
  }

  public static RecordBean getBeanBySNNO(String snNo) {
    if (TextUtils.isEmpty(snNo)) {
      return null;
    }
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    RecordLastDao dao = realm.where(RecordLastDao.class).equalTo(COLUMN_SN, snNo).findFirst();
    realm.commitTransaction();
    if (dao != null && dao.isValid()) {
      return dao.castBean();
    }
    return null;
  }

  /**
   * 清除所有
   */
  public static void cleanAll() {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(new Realm.Transaction() {
      @Override public void execute(Realm realm) {
        realm.where(RecordLastDao.class).findAll().deleteAllFromRealm();
      }
    });
  }

  public static RecordLastDao castDao(RecordBean bean) {
    RecordLastDao dao = new RecordLastDao();
    dao.date = bean.getDate();
    dao.snNo = bean.getSnNo();
    return dao;
  }


  public RecordBean castBean() {
    RecordBean bean = new RecordBean();
    bean.setDate(date);
    bean.setSnNo(snNo);
    return bean;
  }

}
