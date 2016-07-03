package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

//import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by aranz on 22-May-16.
 */
public class ListAdapter extends BaseAdapter {

    protected Context mContext;

    protected List<Note> mNotes;

    protected int lastPosition; // For COOL Animation :)



    public ListAdapter(Context mContext, List<Note> mNotes) {
        this.mContext = mContext;
        this.mNotes = mNotes;
        this.lastPosition = -1;

    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Note getItem(int position) {
        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(R.layout.note_view_mini , parent , false);
        }
        TextView title = (TextView) v.findViewById(R.id.nvm_title_textview);
        TextView datetime = (TextView) v.findViewById(R.id.nvm_time_textview);
        TextView location = (TextView) v.findViewById(R.id.nvm_location_textview);
        TextView likes = (TextView) v.findViewById(R.id.nvm_likes_textview);
        TextView permission = (TextView) v.findViewById(R.id.nvm_permission_textview);
//        NetworkImageView thumbNail = (NetworkImageView) v.findViewById(R.id.infoImageImageView);
//        String url = mVideos.get(position).getImgURL();
//        thumbNail.setImageUrl(url, VolleyUtilSingleTone.getInstance(mContext).getImageLoader());
        Note curNote = mNotes.get(position);
        title.setText(curNote.getTitle());
        datetime.setText(Html.fromHtml(curNote.getTimestamp()));
        location.setText(curNote.getAddress());
        likes.setText(""+curNote.getLikes());
        permission.setText(curNote.isPublic() ? "Public":"Private");

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        v.startAnimation(animation);
        lastPosition = position;
        return v;
    }


    // Added Ability to change the List - and then able to call notifyDataSetChanged();
    public void updateList(List<Note> updatedList){
        this.mNotes = updatedList;
    }
}
