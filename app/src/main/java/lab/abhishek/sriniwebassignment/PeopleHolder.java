package lab.abhishek.sriniwebassignment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by Abhishek on 09-Apr-17.
 */

public class PeopleHolder extends RecyclerView.ViewHolder {

    private View mView;
    private ImageView iv_photo;
    private TextView tv_fname, tv_lname, tv_location, tv_price;
    public CardView cardView;

    public PeopleHolder(View itemView) {
        super(itemView);
        mView = itemView;
        cardView = (CardView) mView.findViewById(R.id.cardView);
    }

    public void setvalues(String fname,String lname, String location, String price, String image_url, Context context){

        iv_photo = (ImageView) mView.findViewById(R.id.cardImage);
        tv_fname = (TextView) mView.findViewById(R.id.card_fname);
        tv_lname = (TextView) mView.findViewById(R.id.card_lname);
        tv_location = (TextView) mView.findViewById(R.id.card_location);
        tv_price = (TextView) mView.findViewById(R.id.card_price);

        Picasso.with(context).load(image_url).placeholder(R.mipmap.userdp).into(iv_photo);
        tv_fname.setText(fname);
        tv_lname.setText(lname);
        tv_location.setText(location);
        tv_price.setText(price);

    }


}
