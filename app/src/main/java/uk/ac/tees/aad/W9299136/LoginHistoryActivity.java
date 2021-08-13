package uk.ac.tees.aad.W9299136;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;

import uk.ac.tees.aad.W9299136.SqlitePersistence.Sqlite;
import uk.ac.tees.aad.W9299136.Utills.LoginHistory;

public class LoginHistoryActivity extends AppCompatActivity {

    Sqlite sqlite;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_history);
        sqlite = new Sqlite(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public ArrayList<LoginHistory> getHistory() {
        ArrayList<LoginHistory> mainList = new ArrayList<LoginHistory>();

        String selectQuery = "SELECT  * FROM signin";

        SQLiteDatabase db = sqlite.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LoginHistoryActivity history;
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                String email = cursor.getString(2);
                mainList.add(new LoginHistory(date,email));

            } while (cursor.moveToNext());
        }

        return mainList;
    }
}