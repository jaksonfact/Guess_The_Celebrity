package com.example.guess_the_celebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.guess_the_celebrity.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
{
    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNamesOriginal=new ArrayList<String>();
    ArrayList<String> celebURLsOriginal=new ArrayList<String>();
    int choosenCelebrity;
    String[] answers=new String[4];
    int locationOfCorrectAnswer=0;
    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    public void celebrityChosen(View view)
    {
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext(),"Correct!!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Wrong! It was " +celebNamesOriginal.get(choosenCelebrity),Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection1=(HttpURLConnection) url.openConnection();
                connection1.connect();
                InputStream inputStream=connection1.getInputStream();
                Bitmap myBitmap= BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
   public void organize(ArrayList celebURLs)//created this method to organize the link and name of heros
    {
        for(int i=0;i<celebURLs.size();i++)
        {
            if(celebURLs.get(i).toString().startsWith("https") && celebURLs.get(i).toString().endsWith("jpg"))
            {
                celebURLsOriginal.add((String)celebURLs.get(i));
            }
        }
        for(int i=0;i<celebURLsOriginal.size();i++)
        {
            Pattern t=Pattern.compile("actor/(.*?)/");
            Matcher a=t.matcher(celebURLsOriginal.get(i));
            while(a.find())
            {
                celebNamesOriginal.add(a.group(1));
            }
        }
    }
    public class Downloadtask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data != -1)
                {
                    char current=(char) data;
                    result += current;
                    data=reader.read();
                }
                return result;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
    public void newQuestion()
    {
        try {
            Random rand = new Random();
            choosenCelebrity = rand.nextInt(celebURLsOriginal.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebrityImage = imageTask.execute(celebURLsOriginal.get(choosenCelebrity)).get();
            imageView.setImageBitmap(celebrityImage);

            locationOfCorrectAnswer = rand.nextInt(4);

            int incorrectAnswerLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNamesOriginal.get(choosenCelebrity);
                } else {
                    incorrectAnswerLocation = rand.nextInt(celebNamesOriginal.size());
                    while (incorrectAnswerLocation == choosenCelebrity) {
                        incorrectAnswerLocation = rand.nextInt(celebNamesOriginal.size());
                    }
                    answers[i] = celebNamesOriginal.get(incorrectAnswerLocation);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
        button0=findViewById(R.id.button0);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        Downloadtask task=new Downloadtask();
        String result=null;
        try{
            result=task.execute("https://www.indiaglitz.com/tamil-actor-photos").get();
            String[] splitResult=result.split("<div class=\"subpage_moviegallery\">");

            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(splitResult[0]);

            while(m.find())
            {
                celebURLs.add(m.group(1));
            }
           organize(celebURLs);
            newQuestion();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}