package org.company.project.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.company.project.App;
import org.company.project.InternalIntents;
import org.company.project.R;
import org.company.project.event.DirectoryItemSelectedEvent;
import org.company.project.event.EditIndividualEvent;
import org.company.project.ui.menu.CommonMenu;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class DirectoryActivity extends DrawerActivity {

    @Inject
    CommonMenu commonMenu;

    @Inject
    EventBus bus;

    @Inject
    InternalIntents internalIntents;

    @Bind(R.id.ab_toolbar)
    Toolbar toolbar;

    private boolean dualPane = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directory_list);
        App.getInjectComponent(this).inject(this);
        ButterKnife.bind(this);

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        super.setupDrawerWithDrawerButton(toolbar, R.string.drawer_main);

        dualPane = ButterKnife.findById(this, R.id.fragment_pos2) != null;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_pos1, DirectoryFragment.newInstance(dualPane))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return commonMenu.onOptionsItemSelected(this, item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe
    public void onListItemClicked(DirectoryItemSelectedEvent event) {
        if (dualPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_pos2, IndividualFragment.newInstance(event.getId()))
                    .commit();
        } else {
            internalIntents.showIndividual(this, event.getId());
        }
    }

    @Subscribe
    public void onEditItemClicked(EditIndividualEvent event) {
        if (dualPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_pos2, IndividualEditFragment.newInstance(event.getId()))
                    .commit();
        } else {
            internalIntents.editIndividual(this, event.getId());
        }
    }
}
