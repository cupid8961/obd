package real_bus_tablet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fr3ts0n.ecu.gui.androbd.R;

import java.util.ArrayList;

/**
 * Created by KST03 on 2017-11-03.
 */

public class Tab_Second_Adapter extends BaseAdapter {
    private ArrayList<Tab_Second_Data> listViewItemList = new ArrayList<Tab_Second_Data>();

    public Tab_Second_Adapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }


    // 받아온 지각/결석 원아의 정보들을 UI에 뿌림
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.second_row_layout,parent,false);
        }

        TextView titleText = (TextView)convertView.findViewById(R.id.textitem);
        TextView descText = (TextView)convertView.findViewById(R.id.textitem1);
        TextView late_type = (TextView)convertView.findViewById(R.id.textitem2);
        ImageView kid_image = (ImageView)convertView.findViewById(R.id.kid_late_image);
        TextView late_cont = (TextView)convertView.findViewById(R.id.late_cont_res);

        Tab_Second_Data listViewItem = listViewItemList.get(position);

        titleText.setText(listViewItem.getTitle());
        descText.setText(listViewItem.getDesc());
        late_type.setText(listViewItem.getLate_typestr());
        late_cont.setText("\n< 사  유 > \n"+listViewItem.getLate_contstr());

        if(listViewItem.getKid_imagestr().equals("null")){
            kid_image.setImageResource(R.mipmap.tablet_ity_50x50);
        }else{
            Glide.with(context.getApplicationContext()).load(listViewItem.getKid_imagestr()).into(kid_image);
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void addItem(String title, String desc, String late_type, String kid_image, String late_cont) {
       Tab_Second_Data item = new Tab_Second_Data();

        item.setTitlestr(title);
        item.setDesc(desc);
        item.setLate_typestr(late_type);
        item.setKid_imagestr(kid_image);
        item.setLate_contstr(late_cont);



        listViewItemList.add(item);

    }
    public void clear_list_item(){
        listViewItemList.removeAll(listViewItemList);
    }


}