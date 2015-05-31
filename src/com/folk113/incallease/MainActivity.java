package com.folk113.incallease;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener{
	private final String TAG = "MainActivity";
	private ThreadAdpter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        PowerManager mManager = (PowerManager)Context.getSystemService(Context.POWER_SERVICE);
//        mManager.
        setContentView(R.layout.main);
        findViewById(R.id.increase).setOnClickListener(this);
        findViewById(R.id.decrease).setOnClickListener(this);
        ListView listView = (ListView)findViewById(R.id.listView1);
        mAdapter = new ThreadAdpter(this);
        listView.setAdapter(mAdapter);
    }
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.increase:
			mAdapter.increaseThread();
			break;
		case R.id.decrease:
			mAdapter.decreaseThread();
			break;
		default:
			break;
		}
		
	}
}
