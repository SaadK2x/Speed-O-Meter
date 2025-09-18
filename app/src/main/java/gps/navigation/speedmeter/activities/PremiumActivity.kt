package gps.navigation.speedmeter.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gps.navigation.speedmeter.databinding.ActivityPremiumBinding

class PremiumActivity : AppCompatActivity() {
    val binding: ActivityPremiumBinding by lazy {
        ActivityPremiumBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}