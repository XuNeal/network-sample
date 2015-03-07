package me.xuneal.demo.networksample.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;
import me.xuneal.demo.networksample.app.dao.GitHub;
import me.xuneal.demo.networksample.app.model.Contributor;
import retrofit.RestAdapter;

import java.lang.ref.WeakReference;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    TextView mTextView;
    private RetainedFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv_content);
        // the AsyncTask will cause memory leak
//        new ShowContributorsMemoryLeak().execute();

        // the AsyncTask will not cause memory leak
        //        new ShowContributorsNoMemoryLeak(mTextView).execute();

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            // load the data from the web
            dataFragment.setData(new ShowContributorsNoMemoryLeakAndSaveInstance(this));
            dataFragment.getData().execute();
        } else {
            ShowContributorsNoMemoryLeakAndSaveInstance task = dataFragment.getData();
            if (task.mContributors == null) {
                showContributors(task.mContributors);
            } else {
                task.changeActivity(this);
            }
        }
    }

    private void showContributors(String contributor) {
        mTextView.setText(contributor);
    }

    private class ShowContributorsMemoryLeak extends AsyncTask<Void, Void, List<Contributor>> {



        @Override
        protected List<Contributor> doInBackground(Void... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
            return restAdapter.create(GitHub.class).contributors("square", "retrofit");
        }

        @Override
        protected void onPostExecute(List<Contributor> contributors) {
            super.onPostExecute(contributors);
            StringBuilder stringBuilder = new StringBuilder();
            for (Contributor contributor : contributors) {
                stringBuilder.append(contributor.login).append(System.getProperty("line.separator"));
            }
            mTextView.setText(stringBuilder.toString());
        }
    }



    static class ShowContributorsNoMemoryLeak extends AsyncTask<Void, Void, List<Contributor>> {

        public WeakReference<TextView> mWeakTextView;

        public ShowContributorsNoMemoryLeak(TextView textView) {
            this.mWeakTextView = new WeakReference<TextView>(textView);
        }

        @Override
        protected List<Contributor> doInBackground(Void... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

            return restAdapter.create(GitHub.class).contributors("square", "retrofit");
        }

        @Override
        protected void onPostExecute(List<Contributor> contributors) {
            super.onPostExecute(contributors);
            StringBuilder stringBuilder = new StringBuilder();
            for (Contributor contributor : contributors) {
                stringBuilder.append(contributor.login).append(System.getProperty("line.separator"));
            }
            TextView textView = mWeakTextView.get();
            if (textView !=null) {
                textView.setText(stringBuilder.toString());
            }
        }
    }

    static public class RetainedFragment extends Fragment {

        // data object we want to retain
        private ShowContributorsNoMemoryLeakAndSaveInstance data;

        // this method is only called once for this fragment
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // retain this fragment
            setRetainInstance(true);
        }

        public void setData(ShowContributorsNoMemoryLeakAndSaveInstance data) {
            this.data = data;
        }

        public ShowContributorsNoMemoryLeakAndSaveInstance getData() {
            return data;
        }
    }

    static class ShowContributorsNoMemoryLeakAndSaveInstance extends AsyncTask<Void, Void, List<Contributor>> {
        private String mContributors;
        private WeakReference<Activity> mWeakActivity;



        public ShowContributorsNoMemoryLeakAndSaveInstance(Activity activity) {
            this.mWeakActivity = new WeakReference<Activity>(activity);
        }

        public void changeActivity(Activity activity) {
            mWeakActivity = new WeakReference<Activity>(activity);
        }

        @Override
        protected List<Contributor> doInBackground(Void... params) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.github.com")
                    .build();
            restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

            return restAdapter.create(GitHub.class).contributors("square", "retrofit");
        }

        @Override
        protected void onPostExecute(List<Contributor> contributors) {
            super.onPostExecute(contributors);
            StringBuilder stringBuilder = new StringBuilder();
            for (Contributor contributor : contributors) {
                stringBuilder.append(contributor.login).append(System.getProperty("line.separator"));
            }
            mContributors = stringBuilder.toString();
            Activity activity = mWeakActivity.get();
            if (activity !=null) {
                ((MainActivity)activity).showContributors(stringBuilder.toString());
            }
        }
    }
}
