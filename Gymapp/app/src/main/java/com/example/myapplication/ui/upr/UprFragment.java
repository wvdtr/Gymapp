package com.example.myapplication.ui.upr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

public class UprFragment extends Fragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_upr, container, false);
        Button btn;
        btn = v.findViewById(R.id.buttonrzm);

        TextView answer;
        answer = v.findViewById(R.id.rzm_answer);

        EditText weight, repeats;
        weight = v.findViewById(R.id.editrzm_w);
        repeats = v.findViewById(R.id.editrzm_r);

        btn.setOnClickListener(v1 ->
        {
            int w, r;
            String ans = "";
            try
            {
                w = Integer.parseInt(weight.getText().toString());
            }
            catch(NumberFormatException exception)
            {
                w = 0;
            }
            try
            {
                r = Integer.parseInt(repeats.getText().toString());
            }
            catch(NumberFormatException exception)
            {
                r = 0;
            }

            if(w <= 70)
            {
                ans = Integer.toString(w/3) + " кг. " + Integer.toString(r+2) + " повторений" + "\n" + Integer.toString(w/2) + " кг. " + Integer.toString(r+1) + " повторений " +
                        "\n" + Integer.toString((3*w)/4) + " кг. " + Integer.toString(r) + " повторений";
            }
            else if(w > 70 && w <= 95)
            {
                ans = Integer.toString(w/4) + " кг. " + Integer.toString(r+3) + " повторений" + "\n" + Integer.toString(w/2) + " кг. " + Integer.toString(r+2) + " повторений " +
                        "\n" + Integer.toString((2*w)/3) + " кг. " + Integer.toString(r + 1) + " повторений" + "\n" + Integer.toString((3*w)/4) + " кг. " + Integer.toString(r) + " повторений";
            }
            else if(w > 95 && w <= 120)
            {
                ans = Integer.toString(w/4) + " кг. " + Integer.toString(r+4) + " повторений" + "\n" + Integer.toString(w/2) + " кг. " + Integer.toString(r+2) + " повторений "
                        + "\n" + Integer.toString((2*w)/3) + " кг. " + Integer.toString(r + 2) + " повторений" + "\n"
                        + Integer.toString((3*w)/4) + " кг. " + Integer.toString(r + 1) + " повторений" + "\n" + Integer.toString((9*w)/10) + " кг. " + Integer.toString(r) + " повторений";
            }
            else if(w > 120 && w <= 200)
            {
                ans = Integer.toString(w/5) + " кг. " + Integer.toString(r+4) + " повторений" + "\n" + Integer.toString(35*w/100) + " кг. " + Integer.toString(r+3) + " повторений" + "\n" + Integer.toString(w/2) + " кг. " + Integer.toString(r+2) + " повторений "
                        + "\n" + Integer.toString((2*w)/3) + " кг. " + Integer.toString(r + 2) + " повторений" + "\n"
                        + Integer.toString((3*w)/4) + " кг. " + Integer.toString(r + 1) + " повторений" + "\n" + Integer.toString((85*w)/100) + " кг. " + Integer.toString(r) + " повторений";
            }
            else if(w > 200 && w <= 300)
            {
                ans = Integer.toString(w/6) + " кг. " + Integer.toString(r+4) + " повторений" + "\n" +
                        Integer.toString(w/4) + " кг. " + Integer.toString(r+3) + " повторений" + "\n" + Integer.toString(4*w/10) + " кг. " + Integer.toString(r+2) + " повторений "
                        + "\n" + Integer.toString((60*w/100)) + " кг. " + Integer.toString(r + 1) + " повторений" + "\n"
                        + Integer.toString((3*w/4)) + " кг. " + Integer.toString(r) + " повторений" + "\n" + Integer.toString((85*w/100)) + " кг. " + Integer.toString(r) + " повторений";
            }
            else if(w > 300)
            {
                ans = Integer.toString(w/6) + " кг. " + Integer.toString(r+5) + " повторений" + "\n" +
                        Integer.toString(w/4) + " кг. " + Integer.toString(r+4) + " повторений" + "\n" + Integer.toString(4*w/10) + " кг. " + Integer.toString(r+3) + " повторений "
                        + "\n" + Integer.toString((6*w/10)) + " кг. " + Integer.toString(r + 2) + " повторений" + "\n"
                        + Integer.toString((3*w/4)) + " кг. " + Integer.toString(r+1) + " повторений" + "\n" + Integer.toString((85*w/100)) + " кг. " + Integer.toString(r) + " повторений";
            }
            answer.setText(ans);
        });
        return v;
    }
}
