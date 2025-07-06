package com.example.brightnessgpio13;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String EXPORT_PATH = "/sys/class/pwm/pwmchip2/export";
    private static final String BASE_PATH   = "/sys/class/pwm/pwmchip2/pwm1/";
    private static final int PWM_CHANNEL     = 1;
    private static final int PERIOD_NS       = 1000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeekBar sb = findViewById(R.id.seekBar);
        Button btn = findViewById(R.id.btnApply);

        runAsRoot("echo " + PWM_CHANNEL + " > " + EXPORT_PATH);
        runAsRoot("echo " + PERIOD_NS + " > " + BASE_PATH + "period");

        btn.setOnClickListener(v -> {
            int pct = sb.getProgress();
            int duty = pct * PERIOD_NS / 100;
            runAsRoot("echo " + duty + " > " + BASE_PATH + "duty_cycle");
            runAsRoot("echo 1 > " + BASE_PATH + "enable");
        });
    }

    private void runAsRoot(String cmd) {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            su.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
