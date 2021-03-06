package com.example.user.projectse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewEventActivity extends Activity implements AdapterView.OnItemSelectedListener {

    Button dateBut,timeBut;
    int year_x=-1, month_x=-1, day_x=-1, hour_x=-1,minute_x=-1;
    static final int DIALOG_ID = 0;
    static final int DIALOG_ID2 = 1;
    Spinner spinner;
    TextView title_x, location_x;
    String typeOfEvent;

/// integration

    private List<Event> EventList;
    private ListView listView;
    private TextView EventName, EventDate, EventType;
    private EventsAdapter adapter;
    EventsHandler db;
/// integration


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        showDialogOnButtonClick();

/// integration
        listView = (ListView) findViewById(R.id.eventList);
        db = new EventsHandler(this);
        EventList = db.getAllContacts();
        adapter = new EventsAdapter(this, EventList);
        listView.setAdapter(adapter);
/// integration

        //   A D D    B U T T O N =fab
        // when clicking on the Add button a new event is created using the data from the user
        // the event object is passed to the database:
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() { //add button listener
                @Override
                public void onClick(View v) {  // onclick ADD button
                    Context context=  getApplicationContext();  //function for using Toast (for displaying messages on screen)

                    // verify all inputs are valid
                    if(title_x.getText().length()<1)    // validate title
                        Toast.makeText(context, "enter title", Toast.LENGTH_LONG).show();
                    else if(location_x.getText().length()<1)  // validate location
                        Toast.makeText(context, "enter location " , Toast.LENGTH_LONG).show();
                    else if(typeOfEvent.length()>14) // ensure event type was chosen
                        Toast.makeText(context, "select event type "+typeOfEvent , Toast.LENGTH_LONG).show();
                    else if(year_x== -1 || month_x==-1 || day_x==-1) // ensure a date was chosen
                        Toast.makeText(context, "choose date" , Toast.LENGTH_LONG).show();
                    else if(hour_x== -1 || minute_x==-1) // ensure time is chosen
                        Toast.makeText(context, "choose time " , Toast.LENGTH_LONG).show();

                    else        // if all inputs are valid create event object to send to database
                    {

                        Time time=new Time(0);
                        time.setHours(hour_x);
                        time.setMinutes(minute_x);
                        time.setSeconds(0);

                        Date d=new Date(year_x,month_x,day_x);
                        d.setDate(day_x);
                        d.setMonth(month_x);
                        d.setYear(year_x-1900);
                        // create the Event object
                        Event event =new Event(d,time,location_x.getText().toString(),typeOfEvent,title_x.getText().toString());

                        //TEST to see the event created:
                        Toast.makeText(context, " title: "+event.title +" loction: " +  event.location +" type: "+ event.eventType+ " Date: " + event.date.toString()+ d.toString()+ " time: "+ event.time.toString(), Toast.LENGTH_LONG).show();

                        // #### TO DO: send event object to database

                        int id = db.addContact(event);
                        event.setId(id);
                        EventList.add(event);
                        adapter.notifyDataSetChanged();

                        // ####        return to main page
                        finish();
                    }
                }
            });
        }


        title_x= (EditText)findViewById(R.id.titleText); // link variable title_x to EditText title
        location_x= (EditText)findViewById(R.id.locationText);// link variable location_x to EditText location
        spinner = (Spinner) findViewById(R.id.spinnere);// link variable spinner to the spinner on content_main
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Event Type");
        categories.add("Test");
        categories.add("Assignment");
        categories.add("Quiz");
        categories.add("Personal");
        categories.add("Other");
        spinner.setPrompt("select event type");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        typeOfEvent=item;   // save the type of the event
        // (TEST) Show selected spinner item on screen
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }




    public void showDialogOnButtonClick() /// buttons function
    {
        dateBut = (Button)findViewById(R.id.dateButton);
        timeBut=(Button)findViewById(R.id.timeButton);
        timeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID2);


            }
        });

        dateBut.setOnClickListener (

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);

                    }
                }
        );
    }
    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID) {       // date picker dialog
            DatePickerDialog temp= new DatePickerDialog(this, dpicker, year_x, month_x, day_x);
            Calendar currentDate = Calendar.getInstance();  //new calendar object
            temp.getDatePicker().setMinDate(currentDate.getTimeInMillis()); // set the minimun available date for today
            return temp;    //return the date chosen
        }
        if(id == DIALOG_ID2) {      // time picker dialog
            return new TimePickerDialog(this,1, tpicker, hour_x,minute_x,true);
        }
        return null;
    }
    // time listener- after user picked time
    protected TimePickerDialog.OnTimeSetListener tpicker = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x=hourOfDay;
            minute_x=minute;
            timeBut.setText( hour_x+ ":" + minute_x);
        }
    };

    private DatePickerDialog.OnDateSetListener dpicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        // date listener- after user picked a date
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x=year;
            month_x= monthOfYear;
            day_x= dayOfMonth;
            dateBut.setText( day_x + " / " + (month_x+1) + " / " + year_x);

        }
    };



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

