package verifysystem.company.com.verifysystem.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import verifysystem.company.com.verifysystem.database.RecordDao;
import verifysystem.company.com.verifysystem.model.NetworkResult;
import verifysystem.company.com.verifysystem.model.RecordBean;
import verifysystem.company.com.verifysystem.network.AppModel;

/**
 * @author zhuj 2017/7/3 下午3:06.
 */
public class UploadService extends IntentService {

  private AppModel mAppModel;

  public UploadService() {
    super("upload");
  }


  @Override protected void onHandleIntent(@Nullable Intent intent) {
    if (mAppModel == null) {
      mAppModel = new AppModel();
    }
    RealmResults<RecordDao> result = RecordDao.getList();
    if (result!=null && result.isValid()) {
      List<RecordBean> recordBeanList = new ArrayList<>();
      for (RecordDao dao : result) {
        recordBeanList.add(dao.castBean());
      }
      mAppModel.uploadData(recordBeanList).subscribe(new Subscriber<NetworkResult>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(Throwable throwable) {
          throwable.printStackTrace();
        }

        @Override public void onNext(NetworkResult networkResult) {
          if (networkResult.isNetworkSuccess()) {
            RecordDao.cleanAll();
          }
        }
      });
    }
  }


}
