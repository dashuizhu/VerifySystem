package verifysystem.company.com.verifysystem.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import verifysystem.company.com.verifysystem.R;


/**
 * Created by Michael on 4/16/2017.
 */

public class VerifyDeviceViewHolder extends RecyclerView.ViewHolder {

    public TextView verify_device_index,verify_device_status;
    public TextView tv_device_sn, tv_deivce_types, tv_deivce_checkUnit, tv_device_certificateNo,
            tv_device_validDate;

    public VerifyDeviceViewHolder(View itemView) {
        super(itemView);
        verify_device_index = (TextView) itemView.findViewById(R.id.verify_device_index);
        verify_device_status = (TextView) itemView.findViewById(R.id.verify_device_status);
        tv_device_sn = (TextView) itemView.findViewById(R.id.tv_device_sn);
        tv_deivce_types = (TextView) itemView.findViewById(R.id.tv_device_types);
        tv_deivce_checkUnit = (TextView) itemView.findViewById(R.id.tv_deivce_checkUnit);
        tv_device_certificateNo = (TextView) itemView.findViewById(R.id.tv_device_certificateNo);
        tv_device_validDate = (TextView) itemView.findViewById(R.id.tv_device_verifyDate);
    }
}
