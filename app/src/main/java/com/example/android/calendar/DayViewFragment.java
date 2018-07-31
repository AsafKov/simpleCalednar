package com.example.android.calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class DayViewFragment extends Fragment {

    public static final int RC_EDIT = 0;
    public static final int RC_ADD = 1;
    public static final int RC_DELETE = 2;

    // App configuration keys
    public static final String DAY_ID = "dayId";

    // Widgets
    private FloatingActionButton fab;

    // Planned day and its activities
    private Day mDay;
    private RecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Restore data after app-configuration
        if(savedInstanceState != null && !savedInstanceState.isEmpty())
            mDay = Day.findDayById(UUID.fromString(savedInstanceState.getString(DAY_ID)));
        else
            mDay = new Day(Calendar.getInstance().getTime());

        adapter = new RecyclerViewAdapter(getActivity(), new ArrayList<Event>(), this);
        adapter.updateDataSet(mDay.getEvents());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        super.onCreateView(inflater, parent, savedInstanceState);
        View v = inflater.inflate(R.layout.day_layout, parent, false);

        RecyclerView eventsContainer = v.findViewById(R.id.eventsRecyclerView);
        registerForContextMenu(eventsContainer);
        eventsContainer.setAdapter(adapter);
        eventsContainer.setLayoutManager(new LinearLayoutManager(getActivity()));

        getActivity().setTitle(mDay.getDate());
        initWidgets(v);
        setHasOptionsMenu(true);
        return v;
    }

    // Save necessary data upon app configuration
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(DAY_ID, mDay.getId().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    // Initialize widgets and their functions
    private void initWidgets(View v){

        fab = v.findViewById(R.id.fabulousFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), EventCreatorActivity.class);
                intent.putExtra(RecyclerViewAdapter.EX_DAY_ID, mDay.getId());
                startActivityForResult(intent, RC_ADD);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_main, menu);
        menu.add(mDay.getDate());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.deleteEventMenuItem:{
                ArrayList<Event> pressedEvents = adapter.getPressedItems();
                if(pressedEvents.isEmpty()){
                    Toast.makeText(getContext(), R.string.noEventsChosenToast, Toast.LENGTH_LONG).show();
                    return true;
                }
                mDay.removeEvents(pressedEvents);
                adapter.onItemsRemoved();
                adapter.updateDataSet(mDay.getEvents());
                adapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        UUID eventId = (UUID)data.getSerializableExtra(EventCreatorFragment.EX_EVENT_ID);
        boolean preformedOverwrite = data.getBooleanExtra(EventCreatorFragment.EX_PREFORMED_OVERWRITE, false);
        Event event = mDay.findEventById(eventId);

        switch (requestCode){
            case RC_EDIT:{
                int currentPosition = mDay.getIndex(event);
                int previousPosition = adapter.getPreviousPosition(eventId);
                adapter.updateDataSet(mDay.getEvents());
                if(previousPosition != currentPosition && !preformedOverwrite)
                    adapter.notifyItemMoved(previousPosition, mDay.getIndex(event));
                else
                    adapter.notifyDataSetChanged();
                adapter.notifyItemChanged(currentPosition);
            } break;
            case RC_ADD:{
                adapter.updateDataSet(mDay.getEvents());
                if(!preformedOverwrite)
                    adapter.notifyItemInserted(mDay.getIndex(event));
                else
                    adapter.notifyDataSetChanged();
            } break;
        }
    }
}