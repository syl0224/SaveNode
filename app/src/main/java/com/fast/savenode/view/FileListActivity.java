package com.fast.savenode.view;

import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.fast.savenode.presenter.FileListActivityPresenter;
import org.jetbrains.annotations.Nullable;
import static com.fast.savenode.Constants.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE;

/**
 * Created by ziliang.z on 2017/4/11.
 */

public class FileListActivity extends ListActivity {
    private static final String TAG = "FileListActivity";
    private FileListActivityPresenter _presenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _presenter = new FileListActivityPresenter(this);

    }

    @Override
    protected void onResume() {
        _presenter.loadList();
        super.onResume();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        _presenter.onListItemClicked(l,v,position,id);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                _presenter.loadList();
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied, finish.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (_presenter.onBackPressed())
            super.onBackPressed();
    }
}
