package com.example.banglacall;
import android.app.*; import android.os.*; import android.content.*; import android.provider.Settings; import android.widget.*; import android.view.*;
import com.google.mlkit.nl.translate.*;
public class MainActivity extends Activity {
 static volatile boolean active=false, diagnostic=false; static volatile String selector="", pkg="";
 private Translator translator; private TextView status; private EditText packageInput,idInput,test;
 @Override public void onCreate(Bundle b){super.onCreate(b); translator=Translation.getClient(new TranslatorOptions.Builder().setSourceLanguage("en").setTargetLanguage("bn").build());
 ScrollView s=new ScrollView(this); LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setPadding(24,48,24,32); s.addView(l); setContentView(s);
 TextView h=new TextView(this); h.setText("বাংলা Call Caption — পরীক্ষামূলক"); h.setTextSize(26); l.addView(h);
 TextView note=new TextView(this); note.setText("English Live Caption চালু করুন। এই অ্যাপ শুধু নির্বাচিত caption node পড়বে। কল অডিও রেকর্ড করে না। Samsung caption node প্রকাশ না করলে কাজ করবে না।"); l.addView(note);
 status=new TextView(this); l.addView(status); button(l,"১. বাংলা মডেল ডাউনলোড",v->translator.downloadModelIfNeeded().addOnSuccessListener(this,x->status.setText("মডেল প্রস্তুত")).addOnFailureListener(this,e->status.setText("Download failed: "+e.getMessage())));
 button(l,"২. Accessibility settings",v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
 button(l,"৩. Caption node পরীক্ষা (শুধু ID)",v->{active=false;diagnostic=true; if(CaptionService.instance!=null)CaptionService.instance.showDiagnostic();else status.setText("Accessibility service চালু করুন");});
 packageInput=new EditText(this); packageInput.setHint("Caption package"); packageInput.setSingleLine(); l.addView(packageInput);
 idInput=new EditText(this); idInput.setHint("Exact caption view ID"); idInput.setSingleLine(); l.addView(idInput);
 button(l,"৪. অনুবাদ চালু",v->{String p=packageInput.getText().toString().trim(), id=idInput.getText().toString().trim(); if(p.isEmpty()||!id.startsWith(p+":id/")||CaptionService.instance==null){status.setText("সঠিক package ও view ID দিন, service চালু করুন।");return;} pkg=p;selector=id;diagnostic=false;active=true;CaptionService.instance.startSession();status.setText("Session active; test with consent.");});
 button(l,"বন্ধ করুন",v->{active=false;diagnostic=false;if(CaptionService.instance!=null)CaptionService.instance.stopSession();});
 test=new EditText(this);test.setHint("English test sentence");l.addView(test);button(l,"Test English → বাংলা",v->{String t=test.getText().toString().trim();if(!t.isEmpty())translator.downloadModelIfNeeded().onSuccessTask(x->translator.translate(t)).addOnSuccessListener(this,status::setText).addOnFailureListener(this,e->status.setText("Error: "+e.getMessage()));});
 }
 private void button(LinearLayout l,String t,View.OnClickListener c){Button b=new Button(this);b.setText(t);b.setAllCaps(false);b.setOnClickListener(c);l.addView(b);}
 @Override protected void onDestroy(){translator.close();super.onDestroy();}
}