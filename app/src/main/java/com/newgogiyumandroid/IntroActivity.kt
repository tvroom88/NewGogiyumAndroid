package com.newgogiyumandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.intro_activity.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)


        val anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_anim);      // 에니메이션 설정한 파일
        logoImage.startAnimation(anim);

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,MainActivity::class.java))
            finish()  // close this activity
        }, 2000 )
    }

}