package com.rakuishi.weight;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.evernote.android.state.StateSaver;
import com.google.android.gms.fitness.data.DataPoint;
import com.rakuishi.weight.databinding.ActivityMainBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        FitnessClient.Callback, View.OnClickListener, FitnessWeightAdapter.Callback {

    private FitnessClient client = null;
    private CompositeDisposable compositeDisposable;
    private ActivityMainBinding binding;
    private FitnessWeightAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        StateSaver.restoreInstanceState(this, savedInstanceState);

        adapter = new FitnessWeightAdapter(this, this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getResources()));
        binding.recyclerView.setAdapter(adapter);
        binding.fab.setOnClickListener(this);

        compositeDisposable = new CompositeDisposable();
        client = ((App) getApplication()).getFitnessClient();
        client.onCreate(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        client.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        client.onPause();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
    }

    // region FitnessClient.Callback

    @Override
    public void onConnectionSuccess() {
        loadFitnessWeight();
    }

    @Override
    public void onConnectionFail(Exception e) {
        Timber.d("onConnectionFail: " + e.getMessage());
    }

    // endregion

    // region View.OnClickListener

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            Disposable disposable = client.insert(60.f)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(() -> {
                        loadFitnessWeight();
                    }, throwable -> {
                        Timber.d(throwable.getMessage());
                    });
            compositeDisposable.add(disposable);
        }
    }

    // endregion

    // region FitnessWeightAdapter.Callback

    @Override
    public void onClickDataPoint(DataPoint dataPoint) {
        Timber.d("onClickDataPoint: " + dataPoint.toString());
    }

    // endregion

    private void loadFitnessWeight() {
        Disposable disposable = client.find(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(dataPoints -> adapter.setDataPoints(dataPoints));
        compositeDisposable.add(disposable);
    }
}
