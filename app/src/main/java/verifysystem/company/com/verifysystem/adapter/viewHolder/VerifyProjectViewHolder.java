package verifysystem.company.com.verifysystem.adapter.viewHolder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import verifysystem.company.com.verifysystem.R;


/**
 * Created by Michael on 4/16/2017.
 */

public class VerifyProjectViewHolder extends RecyclerView.ViewHolder {

    public TextView verify_project_index,verify_report_name,verify_report_object,verify_report_attribute;
    public ImageView verify_control_image;
    public TextView verify_control_text;
    public LinearLayout verify_linear;
    public TextView verify_status;
    public TextView check_layout_plan_text;
    public LinearLayout verify_control_linear;
    public TextView check_verify_data_text;
    public TextView delete_text;

    public VerifyProjectViewHolder(View itemView) {
        super(itemView);
        verify_project_index = (TextView) itemView.findViewById(R.id.verify_project_index);
        verify_report_name = (TextView) itemView.findViewById(R.id.verify_report_name);
        verify_report_object = (TextView) itemView.findViewById(R.id.verify_report_object);
        verify_report_attribute = (TextView) itemView.findViewById(R.id.verify_report_attribute);
        verify_control_image = (ImageView) itemView.findViewById(R.id.verify_control_image);
        verify_control_text = (TextView) itemView.findViewById(R.id.verify_control_text);
        verify_linear = (LinearLayout) itemView.findViewById(R.id.verify_linear);
        verify_status = (TextView) itemView.findViewById(R.id.verify_status);

        check_layout_plan_text = (TextView) itemView.findViewById(R.id.check_layout_plan_text);
        check_layout_plan_text.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        verify_control_linear = (LinearLayout) itemView.findViewById(R.id.verify_control_linear);
        check_verify_data_text = (TextView) itemView.findViewById(R.id.check_verify_data_text);
        check_verify_data_text.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        delete_text = (TextView) itemView.findViewById(R.id.delete_text);
    }
}
