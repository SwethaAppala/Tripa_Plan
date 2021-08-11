package uk.ac.tees.aad.W9299136.Utills;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import uk.ac.tees.aad.W9299136.R;

public class CustomDialog {
    Context context;
    Dialog dialog;
    TextView title;
    ProgressBar progressBar;

    public CustomDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
    }

    public void ShowDialog(String message) {

        dialog.setContentView(R.layout.progress_loading_bar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBar = dialog.findViewById(R.id.progressBar);
        title = dialog.findViewById(R.id.title);
        progressBar.setVisibility(View.VISIBLE);
        title.setText(message);
        dialog.create();
        dialog.show();
    }

    public void DismissDialog() {
        dialog.dismiss();
    }
}
