package com.example.androidproject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private TextView tvQuestion,tvScore,tvQuestionNo,tvTimer;
    private Button btnNext, btn1, btn2, btn3, btn4;
    private ImageView imageView;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private ArrayList<Relative> relatives;
    int totalQuestions;
    int qCounter = 0;
    int score = 0;
    boolean answered = false;
    CountDownTimer countDownTimer;
    private QuestionModel currentQuestion;
    private ArrayList<QuestionModel> questionsList;
    private String isScoreSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        questionsList = getIntent().getParcelableArrayListExtra("question_list");
        relatives = new ArrayList<>();
        tvQuestion = findViewById(R.id.textQuestion);
        imageView = findViewById(R.id.imageView);
        tvScore = findViewById(R.id.textScore);
        tvQuestionNo = findViewById(R.id.textQuestionNo);
        tvTimer = findViewById(R.id.textTimer);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btnNext = findViewById(R.id.btnNext);

        //addQuestions();
        totalQuestions = questionsList.size();
        if(totalQuestions == 5){
            showNextQuestion();
        }


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered==false){
                    Toast.makeText(QuizActivity.this,"Please Select an option", Toast.LENGTH_SHORT).show();
                } else {
                    showNextQuestion();
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered==false){
                    checkAnswer(1);
                    countDownTimer.cancel();
                } else {
                    Toast.makeText(QuizActivity.this,"Please click to next question.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered==false){
                    checkAnswer(2);
                    countDownTimer.cancel();
                } else {
                    Toast.makeText(QuizActivity.this,"Please click to next question.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered==false){
                    checkAnswer(3);
                    countDownTimer.cancel();
                } else {
                    Toast.makeText(QuizActivity.this,"Please click to next question.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered==false){
                    checkAnswer(4);
                    countDownTimer.cancel();
                } else {
                    Toast.makeText(QuizActivity.this,"Please click to next question.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAnswer(int answerNo) {
        answered = true;
        if(answerNo == currentQuestion.getCorrectAnsNo()){
            score++;
            tvScore.setText("Score: " + score);

        }
        btn1.setBackgroundColor(Color.parseColor("#A52A2A"));
        btn2.setBackgroundColor(Color.parseColor("#A52A2A"));
        btn3.setBackgroundColor(Color.parseColor("#A52A2A"));
        btn4.setBackgroundColor(Color.parseColor("#A52A2A"));
        switch(currentQuestion.getCorrectAnsNo()){
            case 1:
                btn1.setBackgroundColor(Color.parseColor("#008000"));
                btn1.setTextColor(Color.BLACK);
                break;
            case 2:
                btn2.setBackgroundColor(Color.parseColor("#008000"));
                btn2.setTextColor(Color.BLACK);
                break;
            case 3:
                btn3.setBackgroundColor(Color.parseColor("#008000"));
                btn3.setTextColor(Color.BLACK);
                break;
            case 4:
                btn4.setBackgroundColor(Color.parseColor("#008000"));
                btn4.setTextColor(Color.BLACK);
                break;

        }
        if(qCounter < totalQuestions){
            btnNext.setText("NEXT QUESTION");
        } else{
            btnNext.setText("Finish");
        }
    }

    private void showNextQuestion() {
        btn1.setBackgroundColor(Color.parseColor("#673AB7"));
        btn2.setBackgroundColor(Color.parseColor("#673AB7"));
        btn3.setBackgroundColor(Color.parseColor("#673AB7"));
        btn4.setBackgroundColor(Color.parseColor("#673AB7"));
        btn1.setTextColor(Color.WHITE);
        btn2.setTextColor(Color.WHITE);
        btn3.setTextColor(Color.WHITE);
        btn4.setTextColor(Color.WHITE);

        if(qCounter < totalQuestions){
            timer();
            currentQuestion = questionsList.get(qCounter);
            tvQuestion.setText(currentQuestion.getQuestion());
            StorageReference imgRef = storageReference.child(currentQuestion.getCorrectImage());
            imgRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            });
            btn1.setText(currentQuestion.getOption1());
            btn2.setText(currentQuestion.getOption2());
            btn3.setText(currentQuestion.getOption3());
            btn4.setText(currentQuestion.getOption4());
            qCounter++;

            btnNext.setText("Submit");
            tvQuestionNo.setText("Question: " + qCounter+ "/" + totalQuestions);
            answered = false;

        }else{

            questionsList.clear();
            Intent intent = new Intent(QuizActivity.this, TotalScoreActivity.class);
            String strName = Integer.toString(score);
            Date date =  new Date(System.currentTimeMillis());
            intent.putExtra("name", strName);
            SimpleDateFormat formatter = new SimpleDateFormat("dd'/'MM'/'yyyy HH:mm");
            String formatDate = formatter.format(date);
            Map<String, Object> scoreHistory = new HashMap<>();
            scoreHistory.put("Score", strName);
            scoreHistory.put("Date", formatDate);
            intent.putParcelableArrayListExtra("question_list", questionsList);
            db.collection("Scores")
                    .add(scoreHistory)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            isScoreSaved = "true";
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isScoreSaved = "false";
                        }
                    });
            intent.putExtra("isScoreSaved", isScoreSaved);
            startActivity(intent);

        }
    }

    private void timer() {
        countDownTimer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("00:" + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                showNextQuestion();
            }
        }.start();
    }
}