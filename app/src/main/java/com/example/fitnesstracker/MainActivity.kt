package com.example.fitnesstracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

data class Workout(val type: String, val distance: Float, val duration: Float, val calories: Float, val intensity: Int)

class MainActivity : AppCompatActivity() {

    private val workoutList = mutableListOf<Workout>()
    private val adapter = WorkoutAdapter(workoutList)

    private lateinit var workoutRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var distanceEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var intensitySeekBar: SeekBar
    private lateinit var filterRadioGroup: RadioGroup
    private lateinit var walkRadioButton: RadioButton
    private lateinit var runRadioButton: RadioButton
    private lateinit var strengthRadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workoutRecyclerView = findViewById(R.id.workoutRecyclerView)
        workoutRecyclerView.layoutManager = LinearLayoutManager(this)
        workoutRecyclerView.adapter = adapter

        distanceEditText = findViewById(R.id.distanceEditText)


        loadWorkouts()



        addButton.setOnClickListener {
            val distance = distanceEditText.text.toString().toFloatOrNull()
            val duration = durationEditText.text.toString().toFloatOrNull()
            val calories = caloriesEditText.text.toString().toFloatOrNull()
            val intensity = intensitySeekBar.progress
            val type = when {
                walkRadioButton.isChecked -> "Spacer"
                runRadioButton.isChecked -> "Bieg"
                strengthRadioButton.isChecked -> "Trening siłowy"
                else -> ""
            }

            if (distance != null && duration != null && calories != null && type.isNotEmpty()) {
                val workout = Workout(type, distance, duration, calories, intensity)
                workoutList.add(workout)
                adapter.notifyDataSetChanged()
//                saveWorkouts()
//                clearForm()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
//
//
//        filterRadioGroup.setOnCheckedChangeListener { _, _ ->
//            val filteredList = workoutList.filter {
//                when {
//                    walkRadioButton.isChecked -> it.type == "Walk"
//                    runRadioButton.isChecked -> it.type == "Run"
//                    strengthRadioButton.isChecked -> it.type == "Strength"
//                    else -> true
//                }
//            }
//            adapter.updateData(filteredList)
//        }
    }

    private fun loadWorkouts() {
        val json = getSharedPreferences("FitnessTracker", MODE_PRIVATE).getString("workouts", "[]")
        val loadedWorkouts = Gson().fromJson(json, Array<Workout>::class.java).toMutableList()
        workoutList.addAll(loadedWorkouts)
        adapter.notifyDataSetChanged()
    }

//    private fun clearForm() {
//
//        distanceEditText.text.clear()
//        durationEditText.text.clear()
//        caloriesEditText.text.clear()
//        intensitySeekBar.progress = 0
//
//    }
//
//    private fun saveWorkouts() {
//        val json = Gson().toJson(workoutList)
//        getSharedPreferences("FitnessTracker", MODE_PRIVATE).edit().putString("workouts", json).apply()
//    }


}

class WorkoutAdapter(private var workouts: List<Workout>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_item, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.bind(workout)
    }

    override fun getItemCount(): Int = workouts.size

    fun updateData(newWorkouts: List<Workout>) {
        workouts = newWorkouts
        notifyDataSetChanged()
    }

    inner class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val workoutTypeTextView: TextView = view.findViewById(R.id.workoutTypeTextView)
        private val workoutDetailsButton: Button = view.findViewById(R.id.addButton)

        fun bind(workout: Workout) {
            workoutTypeTextView.text = "${workout.type} - ${workout.distance} km"
            workoutDetailsButton.setOnClickListener {
                showDetails(workout)
            }
        }

        private fun showDetails(workout: Workout) {
            val message = "Distance: ${workout.distance} km\n" +
                    "czas trwania: ${workout.duration} min\n" +
                    "kalorie: ${workout.calories} kcal\n" +
                    "Intensywność: ${workout.intensity}"

            AlertDialog.Builder(itemView.context)
                .setTitle("Workout Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
