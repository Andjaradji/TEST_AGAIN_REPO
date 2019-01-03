package com.vexanium.vexgift.module.luckydraw.helper;

import android.os.AsyncTask;
import android.widget.TextView;

import com.vexanium.vexgift.bean.model.LuckyDraw;
import com.vexanium.vexgift.bean.model.LuckyDrawWinner;

public class WinnerCoverAsync extends AsyncTask<Void,Void,String> {

    private LuckyDraw luckyDraw;
    private TextView textView;

    public WinnerCoverAsync(TextView view, LuckyDraw luckyDraw){
        textView = view;
        this.luckyDraw = luckyDraw;
    }

    @Override
    protected String doInBackground(Void...voids) {
        String winnerText = "";
        int number = 1;
        for(LuckyDrawWinner winner : luckyDraw.getLuckyDrawWinners()){

            if(!winnerText.equals("")){
                winnerText = winnerText + "\n";
            }

            if(winner.getLuckyDrawId() == luckyDraw.getId()){
                String email = winner.getUser().getEmail();
                int atPosition = email.indexOf('@');
                String shownEmail = "";
                if(atPosition > 10) {
                    String cover = "";
                    for(int i = 9; i <= atPosition; i++){
                        cover += '*';
                    }
                    shownEmail = email.substring(0, 3) + "***" + email.substring(6,8) + cover + email.substring(atPosition);
                }else{
                    shownEmail = email.substring(0,3) + "***" +email.substring(6,email.length());
                }
                winnerText = winnerText + number+". " + winner.getUser().getName() + " (" + shownEmail + ")";
                number = number + 1;
            }
        }
        return winnerText;
    }

    @Override
    protected void onPostExecute(String winnerText) {
        if(!winnerText.equals("")){
            textView.setText(winnerText);
        }
    }
}
