package com.newgogiyumandroid.MainActivityFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.newgogiyumandroid.ConstantsCollector
import com.newgogiyumandroid.ConstantsCollector.mySetting
import com.newgogiyumandroid.R

class ProfileFragment : Fragment() {

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val SP_NAME = "my_sp_storage"

    // 퍼미션 승인 요청시 사용하는 요청 코드
    val REQUEST_PERMISSION_CODE = 1

    private lateinit var myActivity: Context
    private lateinit var myContext: Context

    //SharedPreferences - three things
    private lateinit var locationManager: LocationManager
    private lateinit var tv1: TextView

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    lateinit var locationSwitch:Switch

    // 지역 선택을 위한 array

    private val language = arrayOf("English", "Korean")
    private val region = arrayOf("Seattle", "New York", "Georgia", "Bay Area")

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        //------- for location status ---------
        myActivity = requireActivity()
        myContext = requireContext()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myContext)
        locationSwitch = root.findViewById<Switch>(R.id.location)
        tv1 = root.findViewById<TextView>(R.id.tv1)
        val location_textview = root.findViewById<TextView>(R.id.location_textview)

        // 1. location part
        // 만약 Location이 비어있으면 false로 있으면 true로 switch 부분 default 값을 줌
        // mySetting[0] -> location, language, region
        locationSwitch.isChecked = readSharedPreference(mySetting[0]) != ""
        tv1.text = ConstantsCollector.readSharedPreference(mySetting[0], requireActivity())

        if(locationSwitch.isChecked){
            location_textview.text = "Location is On"
        }else{
            location_textview.text = "Location is Off"
        }
        //--- default 값을 주는거 끝


        locationManager = myContext.getSystemService(LOCATION_SERVICE) as LocationManager

        // ---------- if gps is not connected ----------
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) locationSwitch.isChecked = false

        locationSwitch.setOnCheckedChangeListener { v, isChecked ->
            if(isChecked){
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                } else {
                    val myLocation = getMyLocation()
                    if (myLocation != null) writeSharedPrefernce(mySetting[0], myLocation)
                    location_textview.text = "Location is On"
                }
            } else {
                removeSharedPreference(mySetting[0])
                location_textview.text = "Location is Off"
            }
            tv1.text = readSharedPreference(mySetting[0])
        }

        // ------------------ 2. select Language Part ------------------
        val myLanguageLayout = root.findViewById<ConstraintLayout>(R.id.myLanguageLayout)
        val myLanguage = root.findViewById<TextView>(R.id.myLanguage)

        if(readSharedPreference(mySetting[1]) != "") myLanguage.text = readSharedPreference(mySetting[1])

        //when language is change, language of saved data is also changed.
        val savedFoodDB = ConstantsCollector.getSavedFoodDB(requireContext())
        val savedFoodArray = savedFoodDB.savedMenuDao().getAll()

        val savedRestaurantDB = ConstantsCollector.getSavedRestaurantDB(requireContext())
        val savedRestaurantArray = savedRestaurantDB.savedRestaurantDao().getAll()

        val languageDialog = AlertDialog.Builder(myContext)
            .setItems(language) { _, i ->
                run {
                    myLanguage.setText(language[i])
                    writeSharedPrefernce(mySetting[1], language[i])

                    if(language[i] == "Korean") {
                        for(i in 0 until savedFoodArray.size){ savedFoodArray[i].name = savedFoodArray[i].k_name }
                        for(i in 0 until savedRestaurantArray.size){ savedRestaurantArray[i].name = savedRestaurantArray[i].k_name }
                    }else{
                        for(i in 0 until savedFoodArray.size){ savedFoodArray[i].name = savedFoodArray[i].e_name }
                        for(i in 0 until savedRestaurantArray.size){ savedRestaurantArray[i].name = savedRestaurantArray[i].e_name }
                    }

                    for(i in 0 until savedFoodArray.size){ savedFoodDB.savedMenuDao().update(savedFoodArray[i]) }
                    for(i in 0 until savedRestaurantArray.size){ savedRestaurantDB.savedRestaurantDao().update(savedRestaurantArray[i]) }
                    writeSharedPrefernce(mySetting[1], language[i])
                }
            }
            .setTitle("Language")
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()


        myLanguageLayout.setOnClickListener { languageDialog.show() }
        // ------------------ End : 2. select Language Part ------------------


        // ------------------ 3. select Region Part ------------------
        val myRegionLayout = root.findViewById<ConstraintLayout>(R.id.myRegionLayout)
        val myRegion = root.findViewById<TextView>(R.id.myRegion)

        if(readSharedPreference(mySetting[2]) != "") myRegion.text = readSharedPreference(mySetting[2])

        val regionDialog = AlertDialog.Builder(myContext)
            .setItems(region) { _, i ->
                run {
                    myRegion.setText(region[i])
                    writeSharedPrefernce(mySetting[2], region[i])
                }
            }
            .setTitle("Region")
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .create()

        myRegionLayout.setOnClickListener {
            regionDialog.show()
        }
        return root
    }

    fun writeSharedPrefernce(key:String, data:Any){
        val sp = getActivity()?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val editor = sp?.edit()
        val value:String = when(data) {
            is Location -> "${data.longitude},${data.latitude}"
            is String -> data
            else -> data.toString()
        }

        if (editor != null) {
            editor.putString(key, value)
            editor.apply()
        }
    }

    fun readSharedPreference(key:String): String {
        val sp = getActivity()?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val result = sp?.getString(key, "") ?: ""
        return result
    }

    private fun removeSharedPreference(key:String){
        val sp = getActivity()?.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sp?.edit()?.remove(key)?.apply()
    }

    //   --------------------- Location 부분 받아오는 부분 ---------------------
    @SuppressLint("MissingPermission")
    fun getMyLocation() : Location? {
            locationManager = myContext.getSystemService(LOCATION_SERVICE) as LocationManager
            if (hasPermissions()) {
                val providers: List<String> = locationManager.getProviders(true)
                var bestLocation: Location? = null
                for (provider in providers) {
                    tv1.text = provider
                    val l: Location = locationManager.getLastKnownLocation(provider) ?: continue
                    tv1.text = "oops"

                    if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                        // Found best last known location: %s", l);
                        bestLocation = l
                    }
                }
                return bestLocation
            } else {
                // 권한 요청
                requestPermissions(PERMISSIONS, REQUEST_PERMISSION_CODE)
                return null
            }
    }
    // 앱에서 사용하는 권한이 있는지 체크하는 함수
    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(myContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(myContext, "Permission Granted", Toast.LENGTH_SHORT).show()

                val myLocation = getMyLocation()
                if (myLocation != null) writeSharedPrefernce(mySetting[0], myLocation)
                tv1.text = readSharedPreference(mySetting[0])
            }
            else {
                locationSwitch.isChecked = false
                Toast.makeText(myContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //   --------------------- Location 부분 받아오는 부분 ---------------------
    fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(myContext)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, id ->
                locationSwitch.isChecked = false
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }
}

