package com.example.mywordsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;

import com.example.mywordsapp.wordcontract.Words;

public class MainActivity extends AppCompatActivity implements WordItemFragment.OnFragmentInteractionListener{
    private static final String TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    //建立数据库
    protected void onDestroy() {
        super.onDestroy();
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null)
            wordsDB.close();
    }

 @Override
 //建立菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    //菜单
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                SearchDialog();
                return true;
            case R.id.action_insert:
                InsertDialog();
                return true;
            case R.id.action_translate1:
                Intent intent = new Intent(MainActivity.this,TranslateActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void RefreshWordItemFragment() {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList();
    }

    private void RefreshWordItemFragment(String strWord) {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList(strWord);
    }

    //增加单词
    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.InsertUserSql(strWord, strMeaning, strSample);
                        RefreshWordItemFragment();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }

    //删除单词
    private void DeleteDialog(final String strId) {
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("真的删除单词?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WordsDB wordsDB=WordsDB.getWordsDB();
                wordsDB.DeleteUseSql(strId);
                RefreshWordItemFragment();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        })
        .create()
        .show();
    }

    //修改对话框
    private void UpdateDialog(final String strId, final String strWord, final String strMeaning, final String strSample) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText) tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText) tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText) tableLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();
                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.UpdateUseSql(strId, strNewWord , strNewMeaning, strNewSample);

                        RefreshWordItemFragment();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }

    //查询对话框
    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")
                .setView(tableLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();
                        RefreshWordItemFragment(txtSearchWord);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }
    @Override
    public void onWordItemClick(String id) {
        if(isLand()) {
            ChangeWordDetailFragment(id);
        }else{
            Intent intent = new Intent(MainActivity.this,WordDetailActivity.class);
            intent.putExtra(WordDetailFragment.ARG_ID, id);
            startActivity(intent);
        }
    }

    private boolean isLand(){
        if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return false;
    }

    private void ChangeWordDetailFragment(String id){
        Bundle arguments = new Bundle();
        arguments.putString(WordDetailFragment.ARG_ID, id);
        Log.v(TAG, id);

        WordDetailFragment fragment = new WordDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(com.example.mywordsapp.R.id.worddetail, fragment).commit();
    }

    @Override
    public void onDeleteDialog(String strId) {
        DeleteDialog(strId);
    }

    @Override
    public void onUpdateDialog(String strId) {
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null && strId != null) {
            Words.WordDescription item = wordsDB.getSingleWord(strId);
            if (item != null) {
                UpdateDialog(strId, item.word, item.meaning, item.sample);
            }
        }
    }
}