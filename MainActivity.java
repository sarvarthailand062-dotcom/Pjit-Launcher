package com.pjit.launcher;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout list;
    ArrayList<AppInfo> games = new ArrayList<>();

    static class AppInfo {
        String label, pkg;
        AppInfo(String l,String p){label=l;pkg=p;}
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        showHome();
    }

    TextView title(String s){
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(24); t.setTextColor(Color.DKGRAY);
        t.setPadding(24,24,24,16); return t;
    }

    void showHome(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16,16,16,16);

        root.addView(title("PJIT Launcher"));

        Button add=new Button(this);
        add.setText("Add Game");
        root.addView(add);
        add.setOnClickListener(v->pickGame());

        TextView note=new TextView(this);
        note.setText("Select a game below to launch it. Combat features must be integrated into your own game; this launcher does not inject into other games.");
        note.setPadding(20,15,20,15);
        root.addView(note);

        list=new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        ScrollView sv=new ScrollView(this); sv.addView(list);
        root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));

        setContentView(root);
        refresh();
    }

    void refresh(){
        list.removeAllViews();
        for(AppInfo a:games){
            Button b=new Button(this);
            b.setText(a.label+"\\n"+a.pkg);
            b.setAllCaps(false);
            list.addView(b);
            b.setOnClickListener(v->launch(a.pkg));
        }
    }

    void pickGame(){
        final PackageManager pm=getPackageManager();
        Intent i=new Intent(Intent.ACTION_MAIN,null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps=pm.queryIntentActivities(i,0);
        ArrayList<String> names=new ArrayList<>();
        ArrayList<ResolveInfo> refs=new ArrayList<>();
        for(ResolveInfo r:apps){
            String p=r.activityInfo.packageName;
            if(!p.equals(getPackageName())){
                names.add(r.loadLabel(pm).toString());
                refs.add(r);
            }
        }
        new AlertDialog.Builder(this).setTitle("Add Game")
            .setItems(names.toArray(new String[0]),(d,which)->{
                ResolveInfo r=refs.get(which);
                String p=r.activityInfo.packageName;
                games.add(new AppInfo(r.loadLabel(pm).toString(),p));
                refresh();
            }).setNegativeButton("Cancel",null).show();
    }

    void launch(String pkg){
        Intent i=getPackageManager().getLaunchIntentForPackage(pkg);
        if(i!=null) startActivity(i);
        else Toast.makeText(this,"Game launch unavailable",Toast.LENGTH_SHORT).show();
    }
}