package sg.charleswen.weblinks;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.charleswen.weblinks.model.WebLink;

public class MainActivity extends AppCompatActivity {

    List<String> webLinkUrls = new ArrayList<>();
    private List<WebLink> webLinks = new ArrayList<>();
    private Boolean alphabetical = true, first = true;
    private Toolbar toolbar;
    private ImageButton sortBtn;
    private String m_Text;
    private RecyclerView recyclerView;
    private WebLinksAdapter adapter;
    private FloatingActionButton fab;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initComponent();
        initWebList();
        downloadList(webLinkUrls);
    }

    private void initToolbar()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Website Links");

        sortBtn = findViewById(R.id.alphaBtn);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleList();
            }
        });
    }

    private void initWebList()
    {
        webLinkUrls.clear();
        webLinkUrls.add("https://www.channelnewsasia.com");
        webLinkUrls.add("https://yahoo.com");
        webLinkUrls.add("https://google.com");
    }

    private void initComponent()
    {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);

        adapter = new WebLinksAdapter(webLinks);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new WebLinksAdapter.OnClickListener() {
            @Override
            public void onItemClick(View view, WebLink obj, int pos) {
                if (adapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                }
            }

            @Override
            public void onItemLongClick(View view, WebLink obj, int pos) {
                enableActionMode(pos);
            }
        });

        actionModeCallback = new ActionModeCallback();

    }

    public void downloadList(List<String> webLinkList)
    {
        new DownloadWebLink(new DownloadWebLink.AsyncResponse() {

            @Override
            public void processFinish(List<WebLink> output) {
                webLinks.addAll(output);

                if(first) {
                    Collections.sort(webLinks, new Comparator<WebLink>() {
                        public int compare(WebLink p1, WebLink p2) {
                            return p1.getUrl().compareTo(p2.getUrl());
                        }
                    });
                    adapter.notifyDataSetChanged();
                    first = false;
                }
                else
                {
                    if(alphabetical)
                    {
                        Collections.sort(webLinks, new Comparator<WebLink>(){
                            public int compare(WebLink p1, WebLink p2) {
                                return p1.getUrl().compareTo(p2.getUrl());
                            }
                        });
                    }
                    else
                    {
                        Collections.sort(webLinks, new Comparator<WebLink>(){
                            public int compare(WebLink p1, WebLink p2) {
                                return p2.getUrl().compareTo(p1.getUrl());
                            }
                        });
                    }
                    if(output.size()>0) {
                        adapter.notifyItemInserted(webLinks.indexOf(output.get(0)));
                        Toast.makeText(MainActivity.this, "Invalid url", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).execute(webLinkList);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteInboxes();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelections();
            actionMode = null;
        }
    }

    private void deleteInboxes() {
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            adapter.removeData(selectedItemPositions.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    public void toggleList()
    {
        if(!alphabetical)
        {
            Collections.sort(webLinks, new Comparator<WebLink>(){
                public int compare(WebLink p1, WebLink p2) {
                    return p1.getUrl().compareTo(p2.getUrl());
                }
            });
        }
        else
        {
            Collections.sort(webLinks, new Comparator<WebLink>(){
                public int compare(WebLink p1, WebLink p2) {
                    return p2.getUrl().compareTo(p1.getUrl());
                }
            });
        }
        alphabetical=!alphabetical;

        adapter.notifyDataSetChanged();
    }

    public void showDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Add Website Link");
        final EditText input = new EditText(this);
        input.setHint("e.g. https://2appstudio.com");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertDialog.setView(input);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if(m_Text.startsWith("http")) {
                            List<String> list = Collections.singletonList(m_Text);
                            downloadList(list);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Invalid url", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
