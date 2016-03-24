package com.itba.edu.ar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class MissingUserFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.must_login)
               .setNeutralButton(R.string.dialog_button_login, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Intent intent = new Intent( getActivity().getApplicationContext(), LoginActivity.class);
                	   startActivity(intent);
                   }
               });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}