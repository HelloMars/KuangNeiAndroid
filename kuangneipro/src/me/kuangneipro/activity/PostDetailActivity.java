package me.kuangneipro.activity;

import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import me.kuangneipro.R;
import me.kuangneipro.Adapter.PostDetailAdapter;
import me.kuangneipro.Adapter.PostingImageAdapter;
import me.kuangneipro.core.HttpActivity;
import me.kuangneipro.entity.ChannelEntity;
import me.kuangneipro.entity.PostEntity;

public class PostDetailActivity extends HttpActivity {
	public final static String SELECT_POST_INFO = "me.kuangnei.select.POST";
	
	private PostEntity mPost;
	
	private ExpandableListView mExpandableList;
	private PostDetailAdapter mPostDetailAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_detail);
		
		mPost = (PostEntity) (getIntent().getParcelableExtra(SELECT_POST_INFO));
		
		//getSupportActionBar().setTitle(mChannel.getTitle());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_holo_dark);
		
		mExpandableList = (ExpandableListView) findViewById(R.id.expandableListView);
		
		mPostDetailAdapter = new PostDetailAdapter(this, mPost);
		mExpandableList.setAdapter(mPostDetailAdapter);
		
		this.fillDetailData();
	}
	
	private void fillDetailData() {
		ImageView icon = (ImageView) findViewById(R.id.imgIcon);
		TextView name = (TextView) findViewById(R.id.txtName);
		TextView date = (TextView) findViewById(R.id.txtDate);
		TextView dislikeNum = (TextView) findViewById(R.id.txtDislikeNum);
		TextView likeNum = (TextView) findViewById(R.id.txtLikeNum);
		TextView replyNum = (TextView) findViewById(R.id.txtReplyNum);
		
		name.setText(mPost.mUserName);
		dislikeNum.setText(Integer.toString(mPost.mDislikeNum));
		likeNum.setText(Integer.toString(mPost.mLikeNum));
		replyNum.setText(Integer.toString(mPost.mReplyNum));
		date.setText(mPost.getDate());
		Picasso.with(this)
        	.load(mPost.mUserAvatar)
        	.placeholder(android.R.drawable.ic_menu_my_calendar)
        	.placeholder(R.drawable.ic_launcher)
        	.error(android.R.drawable.ic_menu_report_image)
        	.resize(80, 80)
        	.centerCrop()
        	.into(icon);
	}
}
