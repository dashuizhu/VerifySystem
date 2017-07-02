package verifysystem.company.com.verifysystem.adapter.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import verifysystem.company.com.verifysystem.R;


/**
 * Created by Michael on 5/05/2017.
 */

public class VerifyDataViewHolder extends RecyclerView.ViewHolder {

    public TextView verify_data_index;
    public TextView verify_data_no;
    public TextView verify_data_device_id;
    public TextView verify_data_temp;
    public TextView verify_data_hum;
    public TextView verify_data_voltage;
    public TextView verify_data_time;

    public VerifyDataViewHolder(View itemView) {
        super(itemView);
        verify_data_index = (TextView) itemView.findViewById(R.id.verify_data_index);
        verify_data_no = (TextView) itemView.findViewById(R.id.verify_data_no);
        verify_data_device_id = (TextView) itemView.findViewById(R.id.verify_data_device_id);
        verify_data_temp = (TextView) itemView.findViewById(R.id.verify_data_temp);
        verify_data_hum = (TextView) itemView.findViewById(R.id.verify_data_hum);
        verify_data_voltage = (TextView) itemView.findViewById(R.id.verify_data_voltage);
        verify_data_time = (TextView) itemView.findViewById(R.id.tv_verify_data_time);
    }
}
